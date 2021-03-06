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

package net.underdesk.circolapp.data

import android.content.Context
import net.underdesk.circolapp.shared.data.AppDatabase
import net.underdesk.circolapp.shared.data.CircularDao
import net.underdesk.circolapp.shared.data.DatabaseDriverFactory

object AndroidDatabase {

    @Volatile
    private var instance: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase {
        return instance ?: synchronized(this) {
            instance ?: AppDatabase(
                DatabaseDriverFactory(
                    context
                ).createDriver()
            ).also { instance = it }
        }
    }

    fun getDaoInstance(context: Context): CircularDao {
        return CircularDao(getInstance(context))
    }
}
