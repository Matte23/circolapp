package net.underdesk.circolapp.server

import com.google.gson.Gson
import net.underdesk.circolapp.server.pojo.Response
import org.jsoup.Jsoup
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class DataFetcher {
    companion object {
        const val ENDPOINT_URL = "https://www.curiepinerolo.edu.it/wp-json/wp/v2/pages/5958"

        val gson = Gson()
    }

    @Throws(IOException::class)
    fun getCircularsFromServer(): ArrayList<Pair<String, String>> {
        val json = gson.fromJson(retrieveDataFromServer(), Response::class.java)

        val document = Jsoup.parseBodyFragment(json.content!!.rendered)
        val htmlList = document.getElementsByTag("ul")[0].getElementsByTag("a")

        val list = ArrayList<Pair<String, String>>()

        htmlList.forEach { element ->
            list.add(Pair(element.text(), element.attr("href")))
        }

        return list
    }

    @Throws(IOException::class)
    fun retrieveDataFromServer(): String? {
        var connection: HttpsURLConnection? = null
        return try {
            connection = (URL(ENDPOINT_URL).openConnection() as? HttpsURLConnection)
            connection?.run {
                // Set GET HTTP method
                requestMethod = "GET"

                setRequestProperty("Accept-Encoding", "none")

                connect()
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }

                inputStream?.reader()?.readText()
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            connection?.inputStream?.close()
            connection?.disconnect()
        }

    }
}