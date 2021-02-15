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

package net.underdesk.circolapp.shared.server

import io.ktor.client.*
import net.underdesk.circolapp.shared.data.Circular

abstract class Server(
    val ktorClient: HttpClient
) {
    abstract val serverID: Int
    open val idsAreHumanReadable = true
    abstract suspend fun getCircularsFromServer(): Pair<List<Circular>, ServerAPI.Companion.Result>
    abstract suspend fun getRealUrl(rawUrl: String): Pair<String, ServerAPI.Companion.Result>
    abstract suspend fun newCircularsAvailable(): Pair<Boolean, ServerAPI.Companion.Result>
}
