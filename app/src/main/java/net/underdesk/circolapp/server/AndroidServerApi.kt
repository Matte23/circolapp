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
            instance ?: ServerAPI(ServerAPI.createServer(server)).also { instance = it }
        }
    }

    fun getInstance(context: Context): ServerAPI {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val serverID = sharedPreferences.getString("school", "0")?.toInt() ?: 0

        val server = ServerAPI.Companion.Servers.values()[serverID]

        return instance ?: synchronized(this) {
            instance ?: ServerAPI(ServerAPI.createServer(server)).also { instance = it }
        }
    }

    fun changeServer(index: Int, context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val newServer = ServerAPI.Companion.Servers.values()[index]

        val notifyNewCirculars = sharedPreferences.getBoolean("notify_new_circulars", true)
        val enablePolling = sharedPreferences.getBoolean("enable_polling", false)

        if (notifyNewCirculars && !enablePolling)
            FirebaseTopicUtils.selectTopic(newServer.toString(), context)

        instance?.changeServer(ServerAPI.createServer(newServer))
    }
}
