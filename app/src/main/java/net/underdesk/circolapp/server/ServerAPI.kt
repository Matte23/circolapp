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

import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.server.curie.CurieServer
import net.underdesk.circolapp.server.porporato.PorporatoServer

class ServerAPI(
    private val server: Server
) {
    suspend fun getCircularsFromServer(): Pair<List<Circular>, Result> {
        val newCircularsAvailable = server.newCircularsAvailable()

        if (newCircularsAvailable.second == Result.ERROR)
            return Pair(emptyList(), Result.ERROR)

        if (!newCircularsAvailable.first)
            return Pair(emptyList(), Result.SUCCESS)

        return server.getCircularsFromServer()
    }

    companion object {
        enum class Servers {
            CURIE, PORPORATO
        }

        enum class Result {
            SUCCESS, ERROR
        }

        @Volatile
        private var instance: ServerAPI? = null

        fun getInstance(server: Servers): ServerAPI {
            return instance ?: synchronized(this) {
                instance ?: createServerAPI(server).also { instance = it }
            }
        }

        private fun createServerAPI(server: Servers): ServerAPI {
            return when (server) {
                Servers.CURIE -> ServerAPI(CurieServer())
                Servers.PORPORATO -> ServerAPI(PorporatoServer())
            }
        }
    }
}
