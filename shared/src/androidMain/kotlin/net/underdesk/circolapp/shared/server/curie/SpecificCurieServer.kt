package net.underdesk.circolapp.shared.server.curie

import net.underdesk.circolapp.shared.data.Circular
import org.jsoup.Jsoup

actual class SpecificCurieServer actual constructor(private val curieServer: CurieServer) {
    actual fun parseHtml(string: String): List<Circular> {
        val document = Jsoup.parseBodyFragment(string)
        val htmlList = document.getElementsByTag("ul")[0].getElementsByTag("a")

        val list = ArrayList<Circular>()

        htmlList.forEach { element ->
            if (element.parents().size == 6) {
                list.last().attachmentsNames.add(element.text())
                list.last().attachmentsUrls.add(element.attr("href"))
            } else if (element.parents().size == 4) {
                list.add(curieServer.generateFromString(element.text(), element.attr("href")))
            }
        }

        return list
    }
}
