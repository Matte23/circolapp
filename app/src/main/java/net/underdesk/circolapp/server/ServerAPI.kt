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

import android.content.Context
import androidx.preference.PreferenceManager
import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.server.curie.CurieServer
import net.underdesk.circolapp.server.porporato.PorporatoServer

class ServerAPI(
    private var server: Server
) {
    fun serverID(): Int = server.serverID

    suspend fun getCircularsFromServer(): Pair<List<Circular>, Result> {
        val newCircularsAvailable = server.newCircularsAvailable()

        if (newCircularsAvailable.second == Result.ERROR)
            return Pair(emptyList(), Result.ERROR)

        if (!newCircularsAvailable.first)
            return Pair(emptyList(), Result.SUCCESS)

        return server.getCircularsFromServer()
    }

    fun changeServer(server: Server) {
        this.server = server
    }

    companion object {
        enum class Servers {
            CURIE, PORPORATO
        }

        enum class Result {
            SUCCESS, ERROR
        }

        fun getServerId(server: Servers): Int {
            return Servers.values().indexOf(server)
        }

        fun getServerName(server: Servers) = when (server) {
            Servers.CURIE -> "Liceo scientifico Maria Curie"
            Servers.PORPORATO -> "Liceo G.F. Porporato"
        }

        @Volatile
        private var instance: ServerAPI? = null

        fun getInstance(server: Servers): ServerAPI {
            return instance ?: synchronized(this) {
                instance ?: ServerAPI(createServer(server)).also { instance = it }
            }
        }

        fun getInstance(context: Context): ServerAPI {
            val server = Servers.values()[
                    PreferenceManager.getDefaultSharedPreferences(context).getString("school", "0")
                        ?.toInt() ?: 0
            ]

            return instance ?: synchronized(this) {
                instance ?: ServerAPI(createServer(server)).also { instance = it }
            }
        }

        fun changeServer(index: Int) {
            instance?.changeServer(createServer(Servers.values()[index]))
        }

        private fun createServer(server: Servers) = when (server) {
            Servers.CURIE -> CurieServer()
            Servers.PORPORATO -> PorporatoServer()
        }
    }
}
