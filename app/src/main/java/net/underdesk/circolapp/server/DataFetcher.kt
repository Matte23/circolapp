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
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import java.io.IOException

class DataFetcher {
    companion object {
        const val ENDPOINT_URL = "https://www.curiepinerolo.edu.it/wp-json/wp/v2/pages/5958"

        val gson = Gson()
        val client = OkHttpClient()
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
        val request = Request.Builder()
            .url(ENDPOINT_URL)
            .build()

        return withContext(Dispatchers.IO) {
            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                throw IOException("HTTP error code: ${response.code})")
            }

            response.body?.string()
        }
    }
}