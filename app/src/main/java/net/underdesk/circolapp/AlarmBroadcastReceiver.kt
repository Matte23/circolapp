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

package net.underdesk.circolapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.underdesk.circolapp.data.AndroidDatabase
import net.underdesk.circolapp.shared.data.Circular

class AlarmBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "net.underdesk.circolapp.REMINDER"
        const val CIRCULAR_ID = "circular_id"
        const val SCHOOL_ID = "school_id"
    }

    override fun onReceive(context: Context, intent: Intent) {
        GlobalScope.launch {
            createNotificationChannel(context)
            val circular = AndroidDatabase.getDaoInstance(context).getCircular(
                intent.getLongExtra(
                    CIRCULAR_ID,
                    0
                ),
                intent.getIntExtra(
                    SCHOOL_ID,
                    0
                )
            )
            createNotification(
                context,
                circular
            )
            AndroidDatabase.getDaoInstance(context)
                .update(circular.id, circular.school, circular.favourite, false)
        }
    }

    private fun createNotification(context: Context, circular: Circular) {
        val mainIntent = Intent(context, MainActivity::class.java)
        val viewIntent = Intent(Intent.ACTION_VIEW)
        viewIntent.setDataAndType(Uri.parse(circular.url), "application/pdf").apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addParentStack(MainActivity::class.java)
        taskStackBuilder.addNextIntent(mainIntent)
        taskStackBuilder.addNextIntent(viewIntent)

        val pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title_reminder))
            .setContentText(circular.name)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(CHANNEL_ID)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(circular.name)
            )

        with(NotificationManagerCompat.from(context)) {
            notify(circular.id.toInt(), builder.build())
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name_reminder)
            val descriptionText = context.getString(R.string.channel_description_reminder)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
