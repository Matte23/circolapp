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
            list.add(
                porporatoServer.generateFromString(
                    htmlList[i].textContent,
                    htmlList[i].attributes.objectForKey("href").toString(),
                    i.toLong()
                )
            )
        }

        return list
    }
}
