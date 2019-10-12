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

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.regex.Pattern

@Parcelize
@Entity(tableName = "circulars")
data class Circular(
    @PrimaryKey val id: Long,
    val name: String,
    val url: String,
    val date: String,
    var favourite: Boolean = false,
    var reminder: Boolean = false,
    val attachmentsNames: MutableList<String> = mutableListOf(),
    val attachmentsUrls: MutableList<String> = mutableListOf()
) : Parcelable {
    companion object {
        fun generateFromString(string: String, url: String): Circular {
            val idRegex = """(\d+)"""
            val matcherId = Pattern.compile(idRegex).matcher(string)
            matcherId.find()
            val id = matcherId.group(1)

            val dateRegex = """(\d{2}/\d{2}/\d{4})"""
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