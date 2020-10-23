package net.underdesk.circolapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.server.ServerAPI

class CircularRepository(
    val circularDao: CircularDao,
    private val serverAPI: ServerAPI
) {
    suspend fun updateCirculars(returnNewCirculars: Boolean = true): Pair<List<Circular>, Boolean> {
        var onlyNewCirculars = listOf<Circular>()

        return withContext(Dispatchers.IO) {
            val result = serverAPI.getCircularsFromServer()
            if (result.second == ServerAPI.Companion.Result.ERROR)
                return@withContext Pair(emptyList(), false)

            val oldCirculars = circularDao.getCirculars()
            val newCirculars = result.first

            if (newCirculars.size != oldCirculars.size) {
                if (newCirculars.size < oldCirculars.size) {
                    circularDao.deleteAll()
                }

                if (returnNewCirculars) {
                    val oldCircularsSize =
                        if (newCirculars.size < oldCirculars.size) 0 else oldCirculars.size

                    val circularCount = newCirculars.size - oldCircularsSize
                    onlyNewCirculars = newCirculars.subList(0, circularCount)
                }

                circularDao.insertAll(newCirculars)
            }
            Pair(onlyNewCirculars, true)
        }
    }

    companion object {
        @Volatile
        private var instance: CircularRepository? = null

        fun getInstance(circularDao: CircularDao, serverAPI: ServerAPI) =
            instance ?: synchronized(this) {
                instance ?: CircularRepository(circularDao, serverAPI).also { instance = it }
            }
    }
}
