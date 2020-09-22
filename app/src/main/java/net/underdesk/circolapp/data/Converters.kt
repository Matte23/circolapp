/*
 * Circolapp
 * Copyright (C) 2019-2020  Matteo Schiff
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

import androidx.room.TypeConverter

class Converters {

    @TypeConverter
    fun stringToList(data: String?): List<String> {
        val list: MutableList<String> = mutableListOf()

        if (data != null) {
            for (attachment in data.split("˜")) {
                list.add(attachment)
            }
        }

        return list.dropLast(1)
    }

    @TypeConverter
    fun listToString(list: List<String>): String {
        var string = ""

        for (attachment in list) {
            string += "$attachment˜"
        }

        return string
    }
}
