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

package net.underdesk.circolapp.shared.data

import net.underdesk.circolapp.shared.server.ServerAPI

class CircularRepository(
    val circularDao: CircularDao,
    private val serverAPI: ServerAPI
) {
    suspend fun updateCirculars(returnNewCirculars: Boolean = true): Pair<List<Circular>, Int> {
        var onlyNewCirculars = listOf<Circular>()

        var errorCode = 0
        val result = serverAPI.getCircularsFromServer()

        if (result.second == ServerAPI.Companion.Result.GENERIC_ERROR)
            return Pair(emptyList(), -1)

        if (result.second == ServerAPI.Companion.Result.NETWORK_ERROR)
            return Pair(emptyList(), -2)

        val oldCirculars = circularDao.getCirculars(serverAPI.serverID())
        val newCirculars = result.first

        if (newCirculars.size != oldCirculars.size) {
            if (newCirculars.size < oldCirculars.size) {
                circularDao.deleteAll()
                errorCode = 1
            }

            if (returnNewCirculars) {
                val oldCircularsSize =
                    if (newCirculars.size < oldCirculars.size) 0 else oldCirculars.size

                val circularCount = newCirculars.size - oldCircularsSize
                onlyNewCirculars = newCirculars.subList(0, circularCount)
            }

            circularDao.insertAll(newCirculars)
        }
        return Pair(onlyNewCirculars, errorCode)
    }

    suspend fun getRealUrl(rawUrl: String, id: Long, school: Int): String {
        val result = serverAPI.getRealUrl(rawUrl)

        if (result.second != ServerAPI.Companion.Result.SUCCESS)
            return rawUrl

        circularDao.setRealUrl(id, school, result.first)
        return result.first
    }

    suspend fun getRealUrlForAttachment(
        index: Int,
        rawUrls: List<String>,
        realUrls: List<String>,
        id: Long,
        school: Int
    ): List<String> {
        val result = serverAPI.getRealUrl(rawUrls[index])

        if (result.second != ServerAPI.Companion.Result.SUCCESS)
            return realUrls

        val newList =
            if (realUrls.size != rawUrls.size) MutableList(rawUrls.size) { "" } else realUrls.toMutableList()
        newList[index] = result.first

        circularDao.setRealAttachmentsUrls(id, school, newList)
        return newList
    }
}
