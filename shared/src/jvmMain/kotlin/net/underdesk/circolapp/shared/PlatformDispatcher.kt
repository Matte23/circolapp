package net.underdesk.circolapp.shared

import kotlinx.coroutines.Dispatchers

actual object PlatformDispatcher {
    actual val IO = Dispatchers.IO
}
