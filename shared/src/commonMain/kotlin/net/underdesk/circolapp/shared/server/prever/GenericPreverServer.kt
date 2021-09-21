/*
 * Circolapp
 * Copyright (C) 2019-2021  Matteo Schiff
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

package net.underdesk.circolapp.shared.server.prever

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.shared.server.Server
import net.underdesk.circolapp.shared.server.ServerAPI
import net.underdesk.circolapp.shared.server.pojo.Post
import kotlin.coroutines.cancellation.CancellationException

abstract class GenericPreverServer(ktorClient: HttpClient) : Server(ktorClient) {
    abstract val categoryId: Int

    override val idsAreHumanReadable = false

    override suspend fun getCircularsFromServer()
            : Pair<List<Circular>, ServerAPI.Companion.Result> {
        return try {
            val list = arrayListOf<Circular>()
            var page = 1
            var newCircularsInPage: List<Circular>

            do {
                newCircularsInPage = parsePage(page)
                list.addAll(newCircularsInPage)

                page++
            } while (newCircularsInPage.size >= 99)

            list.sortByDescending { it.id }

            Pair(list, ServerAPI.Companion.Result.SUCCESS)
        } catch (exception: IOException) {
            Pair(emptyList(), ServerAPI.Companion.Result.NETWORK_ERROR)
        } catch (exception: Exception) {
            Pair(emptyList(), ServerAPI.Companion.Result.GENERIC_ERROR)
        }
    }

    @Throws(IOException::class, CancellationException::class)
    private suspend fun parsePage(page: Int): List<Circular> {
        val posts = retrievePageFromServer(page)

        return withContext(Dispatchers.Default) {
            val list = arrayListOf<Circular>()

            for (post in posts) {
                list.add(generateFromString(post.id, post.title.rendered, post.date, post.link))
            }

            list
        }
    }

    override suspend fun getRealUrl(rawUrl: String): Pair<String, ServerAPI.Companion.Result> {
        return Pair(rawUrl, ServerAPI.Companion.Result.SUCCESS)
    }

    override suspend fun newCircularsAvailable(): Pair<Boolean, ServerAPI.Companion.Result> {
        return Pair(true, ServerAPI.Companion.Result.SUCCESS)
    }

    @Throws(IOException::class, CancellationException::class)
    private suspend fun retrievePageFromServer(page: Int): List<Post> {
        return try {
            ktorClient.get(getEndpointUrl(page))
        } catch (ex: ClientRequestException) {
            emptyList()
        }
    }

    private fun generateFromString(id: Long, string: String, date: String, url: String): Circular {
        val title = string.replace("&#8211;", "-")

        val dateList = date.split("T")[0].split("-")
        val realDate = dateList[2] + "/" + dateList[1] + "/" + dateList[0]

        return Circular(id, serverID, title, url, url, realDate)
    }

    private fun getEndpointUrl(page: Int) =
        "https://www.prever.edu.it/wp-json/wp/v2/posts?_fields=id,date,link,title&categories=${categoryId}&per_page=100&page=${page}&after=2020-09-01T00:00:00Z"
}