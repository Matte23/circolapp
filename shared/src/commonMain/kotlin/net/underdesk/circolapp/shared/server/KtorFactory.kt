package net.underdesk.circolapp.shared.server

import io.ktor.client.*

expect class KtorFactory() {
    fun createClient(): HttpClient
}
