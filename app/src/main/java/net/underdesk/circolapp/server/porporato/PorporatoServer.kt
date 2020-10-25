package net.underdesk.circolapp.server.porporato

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.server.Server
import net.underdesk.circolapp.server.ServerAPI
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.util.regex.Pattern

class PorporatoServer : Server() {
    private val client = OkHttpClient()

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

    @Throws(IOException::class)
    private suspend fun parsePage(url: String): List<Circular> {
        val response = retrieveDataFromServer(url)

        return withContext(Dispatchers.Default) {
            val document = Jsoup.parseBodyFragment(response)
            val htmlList = document.getElementsByTag("table")[2]
                .getElementsByTag("td")[2]
                .getElementsByTag("a")

            val list = ArrayList<Circular>()

            for (i in 0 until htmlList.size) {
                list.add(
                    generateFromString(
                        htmlList[i].text(),
                        htmlList[i].attr("href"),
                        i.toLong()
                    )
                )
            }

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

    @Throws(IOException::class)
    private suspend fun retrieveDataFromServer(url: String): String {
        val request = Request.Builder()
            .url(url)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw IOException("HTTP error code: ${response.code})")
            }

            response.body!!.string()
        }
    }

    private fun generateFromString(string: String, path: String, index: Long): Circular {
        val fullUrl = baseUrl + path
        var title = string

        val idRegex =
            """(\d+)"""
        val matcherId = Pattern.compile(idRegex).matcher(string)
        val id = if (!string.startsWith("Avviso") && matcherId.find()) {
            title = title.removeRange(matcherId.start(), matcherId.end())
                .removePrefix(" ")
                .removePrefix("-")
                .removePrefix(" ")

            matcherId.group(1)?.toLong() ?: -index
        } else {
            -index
        }

        val dateRegex =
            """(\d{2}-\d{2}-\d{4})"""
        val matcherDate = Pattern.compile(dateRegex).matcher(title)

        return if (matcherDate.find()) {
            title = title.removeRange(matcherDate.start(), matcherDate.end())
                .removeSuffix(" (pubb.: )")

            Circular(id, serverID, title, fullUrl, matcherDate.group(1)?.replace("-", "/") ?: "")
        } else {
            Circular(id, serverID, title, fullUrl, "")
        }
    }
}
