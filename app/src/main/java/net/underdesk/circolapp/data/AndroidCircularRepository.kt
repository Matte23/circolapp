package net.underdesk.circolapp.data

import android.content.Context
import net.underdesk.circolapp.server.AndroidServerApi
import net.underdesk.circolapp.shared.data.CircularDao
import net.underdesk.circolapp.shared.data.CircularRepository
import net.underdesk.circolapp.shared.server.ServerAPI

object AndroidCircularRepository {
    @Volatile
    private var instance: CircularRepository? = null

    fun getInstance(circularDao: CircularDao, serverAPI: ServerAPI) =
        instance ?: synchronized(this) {
            instance ?: CircularRepository(circularDao, serverAPI).also { instance = it }
        }

    fun getInstance(context: Context) =
        getInstance(AndroidDatabase.getDaoInstance(context), AndroidServerApi.getInstance(context))
}
