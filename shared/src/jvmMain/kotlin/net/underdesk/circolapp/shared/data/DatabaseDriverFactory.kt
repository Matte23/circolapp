package net.underdesk.circolapp.shared.data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY + "circolapp.db")
            .also {
                val currentVer = getVersion(it)
                if (currentVer == 0) {
                    AppDatabase.Schema.create(it)
                    setVersion(it, 1)
                } else {
                    val schemaVer: Int = AppDatabase.Schema.version
                    if (schemaVer > currentVer) {
                        AppDatabase.Schema.migrate(it, currentVer, schemaVer)
                        setVersion(it, schemaVer)
                        println("init: migrated from $currentVer to $schemaVer")
                    }
                }
            }
    }

    private fun getVersion(driver: SqlDriver): Int {
        val sqlCursor = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
        return sqlCursor.getLong(0)!!.toInt()
    }

    private fun setVersion(driver: SqlDriver, version: Int) {
        driver.execute(null, String.format("PRAGMA user_version = %d;", version), 0, null)
    }
}
