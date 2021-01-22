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

package net.underdesk.circolapp.shared.server.curie

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.utils.io.errors.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.shared.server.Server
import net.underdesk.circolapp.shared.server.ServerAPI
import net.underdesk.circolapp.shared.server.pojo.Response
import kotlin.coroutines.cancellation.CancellationException

class CurieServer(ktorClient: HttpClient) : Server(ktorClient) {
    override val serverID = ServerAPI.getServerId(ServerAPI.Companion.Servers.CURIE)

    override suspend fun getCircularsFromServer(): Pair<List<Circular>, ServerAPI.Companion.Result> {
        return try {
            withContext(Dispatchers.Default) {
                val json = retrieveDataFromServer()
                val list = SpecificCurieServer(this@CurieServer).parseHtml(json.content.rendered)
                Pair(list, ServerAPI.Companion.Result.SUCCESS)
            }
        } catch (exception: IOException) {
            Pair(emptyList(), ServerAPI.Companion.Result.NETWORK_ERROR)
        } catch (exception: Exception) {
            Pair(emptyList(), ServerAPI.Companion.Result.GENERIC_ERROR)
        }
    }

    override suspend fun newCircularsAvailable(): Pair<Boolean, ServerAPI.Companion.Result> {
        return Pair(true, ServerAPI.Companion.Result.SUCCESS)
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Throws(IOException::class, CancellationException::class)
    private suspend fun retrieveDataFromServer(): Response {
        return ktorClient.get(ENDPOINT_URL)
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
