/*
 * Circolapp
 * Copyright (C) 2019  Matteo Schiff
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

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.regex.Pattern

@Entity(tableName = "circulars")
data class Circular(
    @PrimaryKey val id: Long,
    val name: String,
    val url: String,
    val date: String,
    val attachmentsNames: MutableList<String> = mutableListOf(),
    val attachmentsUrls: MutableList<String> = mutableListOf()
) {
    companion object {
        fun generateFromString(string: String, url: String): Circular {
            val id = string.split(" ")[1]

            val dateRegex = """(\d{2}\/\d{2}\/\d{4})"""
            val matcherDate = Pattern.compile(dateRegex).matcher(string)

            var title = string.removeSuffix("-signed")

            return if (matcherDate.find()) {
                title = title.removeRange(0, matcherDate.end())
                    .removePrefix(" ")
                    .removePrefix("_")
                    .removePrefix(" ")

                Circular(id.toLong(), title, url, matcherDate.group(1) ?: "")
            } else {
                Circular(id.toLong(), title, url, "")
            }
        }
    }
}