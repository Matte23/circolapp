/*
 * Circolapp
 * Copyright (C) 2019-2020  Matteo Schiff
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.underdesk.circolapp.push

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import net.underdesk.circolapp.shared.data.Circular

// We don't need to get an Instance Token for topic notifications
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class CircolappFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data["newCircular"] == true.toString()) {
            val id = remoteMessage.data["id"]?.toLong() ?: -1
            val name = remoteMessage.data["name"] ?: ""
            val url = remoteMessage.data["url"] ?: ""

            val circular = Circular(
                id, -1, name, url, "",
                favourite = false,
                reminder = false,
                attachmentsNames = mutableListOf(),
                attachmentsUrls = mutableListOf()
            )

            NotificationsUtils.createNotificationsForCirculars(listOf(circular), applicationContext)
        }
    }
}
