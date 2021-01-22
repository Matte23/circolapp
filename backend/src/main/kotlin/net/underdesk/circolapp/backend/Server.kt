/*
 * Circolapp
 * Copyright (C) 2019-2021  Matteo Schiff
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

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

const val CHECK_DELAY_MILLIS = 900000L

fun main(args: Array<String>) {
    print("Starting Circolapp Push microservice \n")

    if (args.isEmpty()) {
        print("ERROR: Database path not specified! Please specify the database path as a parameter \n")
        return
    } else {
        print("Database path is: " + args[0] + " \n")
    }

    var enableNotifications = true
    if (args.size >= 2 && args[1] == "--do-not-notify") {
        print("Notifications are disabled \n")
        enableNotifications = false
    }

    print("Initializing Firebase SDK \n")
    val options = FirebaseOptions.builder()
        .setCredentials(GoogleCredentials.getApplicationDefault())
        .build()

    FirebaseApp.initializeApp(options)

    print("Initializing database and server interfaces \n")
    val serverUtils = ServerUtils(args[0], enableNotifications)

    print("Microservice started! \n")
    runBlocking {
        while (true) {
            serverUtils.checkServers()

            delay(CHECK_DELAY_MILLIS)
        }
    }
}
