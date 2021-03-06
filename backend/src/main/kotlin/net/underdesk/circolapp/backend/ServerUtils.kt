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

package net.underdesk.circolapp.backend

import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.shared.server.KtorFactory
import net.underdesk.circolapp.shared.server.Server
import net.underdesk.circolapp.shared.server.ServerAPI

class ServerUtils(databasePath: String, private val enableNotifications: Boolean = true) {
    private val serverList: MutableList<Server> = mutableListOf()
    private val ktorClient = KtorFactory().createClient()
    private val circularDao = JavaDatabase.getDaoInstance(databasePath)

    init {
        for (serverId in ServerAPI.Companion.Servers.values()) {
            serverList.add(ServerAPI.createServer(serverId, ktorClient))
        }
    }

    suspend fun checkServers() {
        for (server in serverList) {
            checkServer(server)
        }
    }

    private suspend fun updateCirculars(server: Server): Pair<List<Circular>, Int> {
        var onlyNewCirculars = listOf<Circular>()

        var errorCode = 0
        val result = server.getCircularsFromServer()
        if (result.second == ServerAPI.Companion.Result.GENERIC_ERROR)
            return Pair(emptyList(), -1)
        if (result.second == ServerAPI.Companion.Result.NETWORK_ERROR)
            return Pair(emptyList(), -2)

        val oldCirculars = circularDao.getCirculars(server.serverID)
        val newCirculars = result.first

        if (newCirculars.size != oldCirculars.size) {
            if (newCirculars.size < oldCirculars.size) {
                circularDao.deleteAll()
                errorCode = 1
            }

            circularDao.insertAll(newCirculars)

            // Do not send anything if the database was empty
            if (oldCirculars.isEmpty()) {
                print("Database was empty, not sending notifications \n")
                return Pair(emptyList(), 0)
            }

            val oldCircularsSize =
                if (newCirculars.size < oldCirculars.size) 0 else oldCirculars.size

            val circularCount = newCirculars.size - oldCircularsSize
            onlyNewCirculars = newCirculars.subList(0, circularCount)
        }
        return Pair(onlyNewCirculars, errorCode)
    }

    private suspend fun checkServer(server: Server) {
        val newCirculars = updateCirculars(server)

        if (newCirculars.second >= 0 && newCirculars.first.isNotEmpty()) {
            for (circular in newCirculars.first) {
                if (enableNotifications) {
                    PushNotificationUtils.createPushNotification(
                        circular,
                        ServerAPI.getServerTopic(server.serverID)
                    )
                    PushNotificationUtils.createPushNotificationiOS(
                        circular,
                        ServerAPI.getServerTopic(server.serverID)
                    )
                } else {
                    print("Skipping notification for circular ${circular.id} of school ${circular.school} \n")
                }
            }
        }
    }
}
