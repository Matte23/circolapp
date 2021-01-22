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

package net.underdesk.circolapp.push

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class FirebaseTopicUtils {
    companion object {
        fun selectTopic(newTopic: String, context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            unsubscribeFromTopic(sharedPreferences)

            Firebase.messaging.subscribeToTopic(newTopic)

            sharedPreferences.edit {
                putString("topic", newTopic)
            }
        }

        fun unsubscribe(context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

            unsubscribeFromTopic(sharedPreferences)

            sharedPreferences.edit {
                putString("topic", null)
            }
        }

        private fun unsubscribeFromTopic(sharedPreferences: SharedPreferences) {
            val oldTopic = sharedPreferences.getString("topic", null)
            oldTopic?.let { Firebase.messaging.unsubscribeFromTopic(it) }
        }
    }
}
