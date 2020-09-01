/*
 * Circolapp
 * Copyright (C) 2019-2020  Matteo Schiff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.underdesk.circolapp.server

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.data.Circular
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
    suspend fun getCircularsFromServer(): List<Circular> {
        return withContext(Dispatchers.Default) {
            val json = gson.fromJson(retrieveDataFromServer(), Response::class.java)

            val document = Jsoup.parseBodyFragment(json.content!!.rendered)
            val htmlList = document.getElementsByTag("ul")[0].getElementsByTag("a")

            val list = ArrayList<Circular>()

            htmlList.forEach { element ->
                if (element.parents().size == 6) {
                    list.last().attachmentsNames.add(element.text())
                    list.last().attachmentsUrls.add(element.attr("href"))
                } else if (element.parents().size == 4) {
                    list.add(Circular.generateFromString(element.text(), element.attr("href")))
                }
            }

            list
        }
    }

    @Throws(IOException::class)
    private suspend fun retrieveDataFromServer(): String? {
        var connection: HttpsURLConnection? = null

        return withContext(Dispatchers.IO) {
            try {
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
}