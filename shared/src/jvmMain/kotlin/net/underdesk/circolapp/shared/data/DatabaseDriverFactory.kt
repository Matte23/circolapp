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

package net.underdesk.circolapp.shared.data

import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver

actual class DatabaseDriverFactory(private val path: String) {
    actual fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY + path)
            .also {
                val currentVer = DatabaseFactory.getVersion(it)
                if (currentVer == 0) {
                    AppDatabase.Schema.create(it)
                    DatabaseFactory.setVersion(it, 1)
                } else {
                    val schemaVer: Int = AppDatabase.Schema.version
                    if (schemaVer > currentVer) {
                        AppDatabase.Schema.migrate(it, currentVer, schemaVer)
                        DatabaseFactory.setVersion(it, schemaVer)
                        println("init: migrated from $currentVer to $schemaVer")
                    }
                }
            }
    }
}
