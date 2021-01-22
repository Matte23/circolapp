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

package net.underdesk.circolapp.shared.utils

object SqlUtils {
    fun Boolean.toLong() = if (this) 1L else 0L

    fun Long.toBoolean() = this == 1L

    fun String?.toList(): MutableList<String> {
        val list: MutableList<String> = mutableListOf()

        if (this != null) {
            for (attachment in this.split("˜")) {
                list.add(attachment)
            }
        }

        return list.dropLast(1).toMutableList()
    }

    fun List<String>.joinToString(): String {
        var string = ""

        for (attachment in this) {
            string += "$attachment˜"
        }

        return string
    }
}
