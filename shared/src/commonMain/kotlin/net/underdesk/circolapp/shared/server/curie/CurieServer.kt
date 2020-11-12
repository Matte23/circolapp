package net.underdesk.circolapp.shared.server.curie

import io.ktor.client.request.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.shared.server.KtorFactory
import net.underdesk.circolapp.shared.server.Server
import net.underdesk.circolapp.shared.server.ServerAPI
import net.underdesk.circolapp.shared.server.pojo.Response
import kotlin.coroutines.cancellation.CancellationException

class CurieServer : Server() {
    private val client = KtorFactory().createClient()

    override val serverID = ServerAPI.getServerId(ServerAPI.Companion.Servers.CURIE)

    override suspend fun getCircularsFromServer(): Pair<List<Circular>, ServerAPI.Companion.Result> {
        return try {
            withContext(Dispatchers.Default) {
                val json = retrieveDataFromServer()
                val list = SpecificCurieServer(this@CurieServer).parseHtml(json.content.rendered)
                Pair(list, ServerAPI.Companion.Result.SUCCESS)
            }
        } catch (exception: IOException) {
            Pair(emptyList(), ServerAPI.Companion.Result.ERROR)
        }
    }

    override suspend fun newCircularsAvailable(): Pair<Boolean, ServerAPI.Companion.Result> {
        return Pair(true, ServerAPI.Companion.Result.SUCCESS)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Throws(IOException::class, CancellationException::class)
    private suspend fun retrieveDataFromServer(): Response {
        return client.get(ENDPOINT_URL)
    }

    fun generateFromString(string: String, url: String): Circular {
        val idRegex =
            """(\d+)""".toRegex()
        val idMatcher = idRegex.find(string)

        val id = idMatcher?.value?.toLong() ?: -1L

        val dateRegex =
            """(\d{2}/\d{2}/\d{4})""".toRegex()
        val dateMatcher = dateRegex.find(string)

        var title = string.removeSuffix("-signed")

        return if (dateMatcher != null) {
            title = title.removeRange(0, dateMatcher.range.last + 1)
                .removePrefix(" ")
                .removePrefix("_")
                .removePrefix(" ")

            Circular(id, serverID, title, url, dateMatcher.value)
        } else {
            Circular(id, serverID, title, url, "")
        }
    }

    companion object {
        const val ENDPOINT_URL = "https://www.curiepinerolo.edu.it/wp-json/wp/v2/pages/5958"
    }
}

expect class SpecificCurieServer(curieServer: CurieServer) {
    fun parseHtml(string: String): List<Circular>
}
