package net.underdesk.circolapp.shared.server.porporato

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.utils.io.charsets.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.shared.server.KtorFactory
import net.underdesk.circolapp.shared.server.Server
import net.underdesk.circolapp.shared.server.ServerAPI
import kotlin.coroutines.cancellation.CancellationException

class PorporatoServer : Server() {
    private val client = KtorFactory().createClient()

    private val baseUrl = "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/"
    private val endpointUrls = listOf(
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-01-Settembre/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-02-Ottobre/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-03-Novembre/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-04-Dicembre/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-05-Gennaio/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-06-Febbraio/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-07-Marzo/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-08-Aprile/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-09-Maggio/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-10-Giugno/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-11-Luglio/",
        "https://www.liceoporporato.edu.it/ARCHIVIO/PR/VP/circolari.php?dirname=CIRCOLARIP/- CIRCOLARI 2020-21/-12-Agosto/"
    )

    override val serverID = ServerAPI.getServerId(ServerAPI.Companion.Servers.PORPORATO)

    override suspend fun getCircularsFromServer(): Pair<List<Circular>, ServerAPI.Companion.Result> {
        return try {
            val list = arrayListOf<Circular>()

            for (url in endpointUrls) {
                list.addAll(parsePage(url))
            }

            list.sortByDescending { it.id }

            Pair(list, ServerAPI.Companion.Result.SUCCESS)
        } catch (exception: IOException) {
            Pair(emptyList(), ServerAPI.Companion.Result.ERROR)
        }
    }

    override suspend fun newCircularsAvailable(): Pair<Boolean, ServerAPI.Companion.Result> {
        return Pair(true, ServerAPI.Companion.Result.SUCCESS)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Throws(IOException::class, CancellationException::class)
    private suspend fun parsePage(url: String): List<Circular> {
        val response = retrieveDataFromServer(url)

        return withContext(Dispatchers.Default) {
            val list = SpecificPorporatoServer(this@PorporatoServer).parseHtml(response).toMutableList()

            // Identify and group all attachments
            list.removeAll { attachment ->
                if (attachment.name.startsWith("All", true)) {
                    val parent = list.find { it.id == attachment.id && !it.name.startsWith("All") }
                    parent?.attachmentsNames?.add(attachment.name)
                    parent?.attachmentsUrls?.add(attachment.url)

                    return@removeAll true
                }

                false
            }

            // Identify and group attachments not marked with "All"
            var lastIndex = -1
            var lastId = -1L

            list.removeAll { attachment ->
                if (lastId == attachment.id) {
                    val parent = list[lastIndex]
                    parent.attachmentsNames.add(attachment.name)
                    parent.attachmentsUrls.add(attachment.url)

                    return@removeAll true
                }
                lastId = attachment.id
                lastIndex = list.indexOf(attachment)

                false
            }

            list
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Throws(IOException::class, CancellationException::class)
    private suspend fun retrieveDataFromServer(url: String): String {
        return client.request<HttpResponse>(url).readText(Charsets.ISO_8859_1)
    }

    fun generateFromString(string: String, path: String, index: Long): Circular {
        val fullUrl = baseUrl + path
        var title = string

        val idRegex =
            """(\d+)""".toRegex()
        val idMatcher = idRegex.find(string)
        val id = if (!string.startsWith("Avviso") && idMatcher != null) {
            title = title.removeRange(idMatcher.range)
                .removePrefix(" ")
                .removePrefix("-")
                .removePrefix(" ")

            idMatcher.value.toLong()
        } else {
            -index
        }

        val dateRegex =
            """(\d{2}-\d{2}-\d{4})""".toRegex()
        val dateMatcher = dateRegex.find(title)

        return if (dateMatcher != null) {
            title = title.removeRange(dateMatcher.range)
                .removeSuffix(" (pubb.: )")

            Circular(id, serverID, title, fullUrl, dateMatcher.value.replace("-", "/"))
        } else {
            Circular(id, serverID, title, fullUrl, "")
        }
    }
}

expect class SpecificPorporatoServer(porporatoServer: PorporatoServer) {
    fun parseHtml(string: String): List<Circular>
}
