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
