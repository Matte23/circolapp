package net.underdesk.circolapp.push

import android.annotation.SuppressLint
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import net.underdesk.circolapp.works.PollWork

// We don't need to get an Instance Token for topic notifications
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class CircolappFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            PollWork.runWork(applicationContext)
        }
    }
}
