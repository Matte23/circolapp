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

object DatabaseFactory {
    fun createDatabase(sqlDriver: SqlDriver) = AppDatabase(sqlDriver)

    fun getVersion(driver: SqlDriver): Int {
        val sqlCursor = driver.executeQuery(null, "PRAGMA user_version;", 0, null)
        if (!sqlCursor.next())
            return 0

        return sqlCursor.getLong(0)?.toInt() ?: 0
    }

    fun setVersion(driver: SqlDriver, version: Int) {
        driver.execute(null, "PRAGMA user_version = $version;", 0, null)
    }
}
