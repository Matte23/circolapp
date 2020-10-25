package net.underdesk.circolapp.server.curie

import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.server.Server
import net.underdesk.circolapp.server.ServerAPI
import net.underdesk.circolapp.server.curie.pojo.Response
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException
import java.util.regex.Pattern

class CurieServer : Server() {
    private val moshi = Moshi.Builder().build()
    private val responseAdapter = moshi.adapter(Response::class.java)
    private val client = OkHttpClient()

    override val serverID = ServerAPI.getServerId(ServerAPI.Companion.Servers.CURIE)

    override suspend fun getCircularsFromServer(): Pair<List<Circular>, ServerAPI.Companion.Result> {
        return try {
            withContext(Dispatchers.Default) {
                val json = retrieveDataFromServer()

                val document = Jsoup.parseBodyFragment(json.content.rendered)
                val htmlList = document.getElementsByTag("ul")[0].getElementsByTag("a")

                val list = ArrayList<Circular>()

                htmlList.forEach { element ->
                    if (element.parents().size == 6) {
                        list.last().attachmentsNames.add(element.text())
                        list.last().attachmentsUrls.add(element.attr("href"))
                    } else if (element.parents().size == 4) {
                        list.add(generateFromString(element.text(), element.attr("href")))
                    }
                }
                Pair(list, ServerAPI.Companion.Result.SUCCESS)
            }
        } catch (exception: IOException) {
            Pair(emptyList(), ServerAPI.Companion.Result.ERROR)
        }
    }

    override suspend fun newCircularsAvailable(): Pair<Boolean, ServerAPI.Companion.Result> {
        return Pair(true, ServerAPI.Companion.Result.SUCCESS)
    }

    @Throws(IOException::class)
    private suspend fun retrieveDataFromServer(): Response {
        val request = Request.Builder()
            .url(ENDPOINT_URL)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw IOException("HTTP error code: ${response.code})")
            }

            responseAdapter.fromJson(
                response.body!!.string()
            )!!
        }
    }

    private fun generateFromString(string: String, url: String): Circular {
        val idRegex =
            """(\d+)"""
        val matcherId = Pattern.compile(idRegex).matcher(string)
        matcherId.find()
        val id = matcherId.group(1)

        val dateRegex =
            """(\d{2}/\d{2}/\d{4})"""
        val matcherDate = Pattern.compile(dateRegex).matcher(string)

        var title = string.removeSuffix("-signed")

        return if (matcherDate.find()) {
            title = title.removeRange(0, matcherDate.end())
                .removePrefix(" ")
                .removePrefix("_")
                .removePrefix(" ")

            Circular(id.toLong(), serverID, title, url, matcherDate.group(1) ?: "")
        } else {
            Circular(id.toLong(), serverID, title, url, "")
        }
    }

    companion object {
        const val ENDPOINT_URL = "https://www.curiepinerolo.edu.it/wp-json/wp/v2/pages/5958"
    }
}
