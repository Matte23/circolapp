package net.underdesk.circolapp.shared.data

import com.squareup.sqldelight.db.SqlDriver

object DatabaseFactory {
    fun createDatabase(sqlDriver: SqlDriver) = AppDatabase(sqlDriver)
}