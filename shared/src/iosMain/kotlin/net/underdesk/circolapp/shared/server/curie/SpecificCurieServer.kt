package net.underdesk.circolapp.shared.server.curie

import net.underdesk.circolapp.shared.data.Circular

actual class SpecificCurieServer actual constructor(val curieServer: CurieServer) {
    actual fun parseHtml(string: String): List<Circular> {
        TODO("Not yet implemented")
    }
}
