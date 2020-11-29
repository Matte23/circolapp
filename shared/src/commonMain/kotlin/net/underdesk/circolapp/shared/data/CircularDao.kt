package net.underdesk.circolapp.shared.data

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.withContext
import net.underdesk.circolapp.shared.PlatformDispatcher
import net.underdesk.circolapp.shared.utils.SqlUtils.joinToString
import net.underdesk.circolapp.shared.utils.SqlUtils.toBoolean
import net.underdesk.circolapp.shared.utils.SqlUtils.toList
import net.underdesk.circolapp.shared.utils.SqlUtils.toLong
import net.underdesk.circolapp.shared.utils.wrap

class CircularDao(
    database: AppDatabase
) {
    private val appDatabaseQueries = database.appDatabaseQueries

    private val circularMapper =
        { id: Long, school: Long, name: String, url: String, date: String, favourite: Long, reminder: Long, attachmentsNames: String, attachmentsUrls: String ->
            Circular(
                id,
                school.toInt(),
                name,
                url,
                date,
                favourite.toBoolean(),
                reminder.toBoolean(),
                attachmentsNames.toList(),
                attachmentsUrls.toList()
            )
        }

    suspend fun insertAll(circulars: List<Circular>) = withContext(PlatformDispatcher.IO) {
        circulars.forEach {
            appDatabaseQueries.insertCircular(
                it.id,
                it.school.toLong(),
                it.name,
                it.url,
                it.date,
                it.favourite.toLong(),
                it.reminder.toLong(),
                it.attachmentsNames.joinToString(),
                it.attachmentsUrls.joinToString()
            )
        }
    }

    suspend fun update(id: Long, school: Int, favourite: Boolean, reminder: Boolean) =
        withContext(PlatformDispatcher.IO) {
            appDatabaseQueries.updateCircular(
                favourite.toLong(),
                reminder.toLong(),
                id,
                school.toLong()
            )
        }

    suspend fun deleteAll() = withContext(PlatformDispatcher.IO) {
        appDatabaseQueries.deleteAllCirculars()
    }

    fun getCircular(id: Long, school: Int) = appDatabaseQueries.getCircular(id, school.toLong(), circularMapper).executeAsOne()

    fun getCirculars(school: Int) =
        appDatabaseQueries.getCirculars(school.toLong(), circularMapper).executeAsList()

    fun getFlowCirculars(school: Int) =
        appDatabaseQueries.getCirculars(school.toLong(), circularMapper).asFlow().mapToList()
    fun getCFlowCirculars(school: Int) = getFlowCirculars(school).wrap()

    fun searchCirculars(query: String, school: Int) =
        appDatabaseQueries.searchCirculars(school.toLong(), query, circularMapper).asFlow()
            .mapToList()
    fun searchCircularsC(query: String, school: Int) = searchCirculars(query, school).wrap()

    fun getFavourites(school: Int) =
        appDatabaseQueries.getFavourites(school.toLong(), circularMapper).asFlow().mapToList()
    fun getFavouritesC(school: Int) = getFavourites(school).wrap()

    fun searchFavourites(query: String, school: Int) =
        appDatabaseQueries.searchFavourites(school.toLong(), query, circularMapper).asFlow()
            .mapToList()
    fun searchFavouritesC(query: String, school: Int) = searchFavourites(query, school).wrap()

    fun getReminders(school: Int) =
        appDatabaseQueries.getReminders(school.toLong(), circularMapper).asFlow().mapToList()
    fun getRemindersC(school: Int) = getReminders(school).wrap()

    fun searchReminders(query: String, school: Int) =
        appDatabaseQueries.searchReminders(school.toLong(), query, circularMapper).asFlow()
            .mapToList()
    fun searchRemindersC(query: String, school: Int) = searchReminders(query, school).wrap()
}
