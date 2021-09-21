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

import net.underdesk.circolapp.shared.data.Circular
import org.jsoup.Jsoup

actual class SpecificPorporatoServer actual constructor(private val porporatoServer: PorporatoServer) {
    actual fun parseHtml(string: String): List<Circular> {
        val document = Jsoup.parseBodyFragment(string)
        val htmlList = document.getElementsByTag("table")[2]
            .getElementsByTag("td")[2]
            .getElementsByTag("a")

        val list = ArrayList<Circular>()

        for (i in 0 until htmlList.size) {
            porporatoServer.generateFromString(
                htmlList[i].text(),
                htmlList[i].attr("href"),
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
