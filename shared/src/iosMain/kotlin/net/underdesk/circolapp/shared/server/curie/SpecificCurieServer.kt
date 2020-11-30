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
            if (element.parentElement?.parentElement?.parentElement?.tagName == "li") {
                list.last().attachmentsNames.add(element.textContent)
                list.last().attachmentsUrls.add(element.attributes.objectForKey("href").toString())
            } else {
                list.add(curieServer.generateFromString(element.textContent, element.attributes.objectForKey("href").toString()))
            }
        }

        return list
    }
}
