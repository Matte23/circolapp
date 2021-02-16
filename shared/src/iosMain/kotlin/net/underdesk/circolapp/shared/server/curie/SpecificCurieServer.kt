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

package net.underdesk.circolapp.shared.server.curie

import cocoapods.HTMLKit.HTMLElement
import cocoapods.HTMLKit.HTMLParser
import net.underdesk.circolapp.shared.data.Circular

actual class SpecificCurieServer actual constructor(private val curieServer: CurieServer) {
    actual fun parseHtml(string: String): List<Circular> {
        val document = HTMLParser(string).parseDocument()
        val htmlList = document.querySelector("ul")?.querySelectorAll("a") as List<HTMLElement>?

        val list = ArrayList<Circular>()

        htmlList?.forEach { element ->
            val url = element.attributes.objectForKey("href").toString()
            if (element.parentElement?.parentElement?.parentElement?.tagName == "li") {
                list.last().attachmentsNames.add(element.textContent)
                list.last().attachmentsUrls.add(url)

                if (url.endsWith(".pdf")) {
                    list.last().realAttachmentsUrls.add(url)
                } else {
                    list.last().realAttachmentsUrls.add("")
                }
            } else {
                list.add(curieServer.generateFromString(element.textContent, url))
            }
        }

        return list
    }

    actual fun parseFileUrl(string: String): String {
        val document = HTMLParser(string).parseDocument()
        return document.querySelector(".mtli_attachment")!!.attributes.objectForKey("href").toString()
    }
}
