package net.underdesk.circolapp.server.curie.pojo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Content(
    val rendered: String
)
