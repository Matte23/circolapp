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

data class Circular(
    val id: Long,
    val school: Int,
    val name: String,
    val url: String,
    val realUrl: String?,
    val date: String,
    var favourite: Boolean = false,
    var reminder: Boolean = false,
    var read: Boolean = false,
    val attachmentsNames: MutableList<String> = mutableListOf(),
    val attachmentsUrls: MutableList<String> = mutableListOf(),
    val realAttachmentsUrls: MutableList<String> = mutableListOf()
)
