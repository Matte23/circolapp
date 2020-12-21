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
        if (result.second == ServerAPI.Companion.Result.ERROR)
            return Pair(emptyList(), -1)

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
}
