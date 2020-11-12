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
            list.add(
                porporatoServer.generateFromString(
                    htmlList[i].text(),
                    htmlList[i].attr("href"),
                    i.toLong()
                )
            )
        }

        return list
    }
}
