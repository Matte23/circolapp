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

package net.underdesk.circolapp.backend

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import com.google.firebase.messaging.Notification
import net.underdesk.circolapp.shared.data.Circular
import java.time.LocalDateTime

object PushNotificationUtils {
    fun createPushNotification(circular: Circular, topic: String) {
        val message = Message.builder()
            .putData("newCircular", true.toString())
            .putData("id", circular.id.toString())
            .putData("name", circular.name)
            .putData("url", circular.url)
            .setTopic(topic)

        val response = FirebaseMessaging.getInstance().send(message.build())

        val current = LocalDateTime.now()

        print("Sent data push notification with circular ${circular.id} for topic $topic with response $response at time $current \n")
    }

    fun createPushNotificationiOS(circular: Circular, topic: String) {
        val realTopic = topic + "IOS"

        val message = Message.builder()
            .setNotification(
                Notification.builder()
                    .setTitle("Circolare numero " + circular.id)
                    .setBody(circular.name)
                    .build()
            )
            .putData("url", circular.url)
            .setTopic(realTopic)
            .build()

        val response = FirebaseMessaging.getInstance().send(message)

        val current = LocalDateTime.now()

        print("Sent managed push notification with circular ${circular.id} for topic $realTopic with response $response at time $current \n")
    }
}
