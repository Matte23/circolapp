package net.underdesk.circolapp.shared

import kotlinx.coroutines.CoroutineDispatcher

expect object PlatformDispatcher {
    val IO: CoroutineDispatcher
}
