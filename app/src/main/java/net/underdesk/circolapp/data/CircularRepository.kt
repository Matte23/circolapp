package net.underdesk.circolapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.server.DataFetcher
import java.io.IOException

class CircularRepository(
    val circularDao: CircularDao,
    private val fetcher: DataFetcher = DataFetcher()
) {
    suspend fun updateCirculars(returnNewCirculars: Boolean = true): Pair<List<Circular>, Boolean> {
        var onlyNewCirculars = listOf<Circular>()

        try {
            withContext(Dispatchers.IO) {
                val oldCirculars = circularDao.getCirculars()
                val newCirculars = fetcher.getCircularsFromServer()
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
            }
        } catch (exception: IOException) {
            return Pair(onlyNewCirculars, false)
        }

        return Pair(onlyNewCirculars, true)
    }

    companion object {
        @Volatile
        private var instance: CircularRepository? = null

        fun getInstance(circularDao: CircularDao) =
            instance ?: synchronized(this) {
                instance ?: CircularRepository(circularDao).also { instance = it }
            }
    }
}
