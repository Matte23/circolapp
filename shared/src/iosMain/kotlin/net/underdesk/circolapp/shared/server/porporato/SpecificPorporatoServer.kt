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

package net.underdesk.circolapp.shared.server.porporato

import cocoapods.HTMLKit.HTMLElement
import cocoapods.HTMLKit.HTMLParser
import net.underdesk.circolapp.shared.data.Circular

actual class SpecificPorporatoServer actual constructor(private val porporatoServer: PorporatoServer) {
    actual fun parseHtml(string: String): List<Circular> {
        val document = HTMLParser(string).parseDocument()
        val table = document.querySelectorAll("table") as List<HTMLElement>?
        val td = table?.get(2)?.querySelectorAll("td") as List<HTMLElement>?
        val htmlList = td?.get(2)?.querySelectorAll("a") as List<HTMLElement>?

        val list = ArrayList<Circular>()

        if (htmlList == null)
            return list

        for (i in htmlList.indices) {
            porporatoServer.generateFromString(
                htmlList[i].textContent,
                htmlList[i].attributes.objectForKey("href").toString(),
                i.toLong()
            )?.let {
                list.add(
                    it
                )
            }
        }

        return list
    }
}
