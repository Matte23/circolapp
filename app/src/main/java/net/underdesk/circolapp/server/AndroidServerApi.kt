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

package net.underdesk.circolapp.server

import android.content.Context
import androidx.preference.PreferenceManager
import net.underdesk.circolapp.push.FirebaseTopicUtils
import net.underdesk.circolapp.shared.server.ServerAPI

object AndroidServerApi {
    @Volatile
    private var instance: ServerAPI? = null

    fun getInstance(server: ServerAPI.Companion.Servers): ServerAPI {
        return instance ?: synchronized(this) {
            instance ?: ServerAPI(server).also { instance = it }
        }
    }

    fun getInstance(context: Context): ServerAPI {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val serverID = sharedPreferences.getString("school", "0")?.toInt() ?: 0

        val server = ServerAPI.getServer(serverID)

        return instance ?: synchronized(this) {
            instance ?: ServerAPI(server).also { instance = it }
        }
    }

    fun changeServer(index: Int, context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val newServer = ServerAPI.Companion.Servers.values()[index]

        val notifyNewCirculars = sharedPreferences.getBoolean("notify_new_circulars", true)
        val enablePolling = sharedPreferences.getBoolean("enable_polling", false)

        if (notifyNewCirculars && !enablePolling)
            FirebaseTopicUtils.selectTopic(newServer.toString(), context)

        instance?.changeServer(newServer)
    }
}
