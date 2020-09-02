package net.underdesk.circolapp.server.pojo

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Response(
    val content: Content
)
