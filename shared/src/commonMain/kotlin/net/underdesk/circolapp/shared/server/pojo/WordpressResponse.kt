package net.underdesk.circolapp.shared.server.pojo

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val content: Content
)

@Serializable
data class Content(
    val rendered: String
)
