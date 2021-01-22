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

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import net.underdesk.circolapp.MainActivity
import net.underdesk.circolapp.R
import net.underdesk.circolapp.shared.data.Circular
import net.underdesk.circolapp.works.PollWork

object NotificationsUtils {
    fun createNotificationsForCirculars(circulars: List<Circular>, context: Context) {
        createNotificationChannel(context)

        val summaryStyle = NotificationCompat.InboxStyle()
            .setBigContentTitle(context.getString(R.string.notification_summary_title))
            .setSummaryText(context.getString(R.string.notification_summary))

        for (circular in circulars) {
            createNotification(circular, context)
            summaryStyle.addLine(circular.name)
        }

        val summaryNotification =
            NotificationCompat.Builder(context, PollWork.CHANNEL_ID)
                .setContentTitle(context.getString(R.string.notification_summary_title))
                .setContentText(
                    context.resources.getQuantityString(
                        R.plurals.notification_summary_text,
                        circulars.size,
                        circulars.size
                    )
                )
                .setSmallIcon(R.drawable.ic_notification)
                .setStyle(summaryStyle)
                .setGroup(PollWork.CHANNEL_ID)
                .setGroupSummary(true)
                .build()

        with(NotificationManagerCompat.from(context)) {
            notify(-1, summaryNotification)
        }
    }

    private fun createNotification(circular: Circular, context: Context) {
        val mainIntent = Intent(context, MainActivity::class.java)
        val viewIntent = Intent(Intent.ACTION_VIEW)

        if (circular.url.endsWith(".pdf")) {
            viewIntent.setDataAndType(Uri.parse(circular.url), "application/pdf")
        } else {
            viewIntent.data = Uri.parse(circular.url)
        }

        viewIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val taskStackBuilder = TaskStackBuilder.create(context)
        taskStackBuilder.addParentStack(MainActivity::class.java)
        taskStackBuilder.addNextIntent(mainIntent)
        taskStackBuilder.addNextIntent(viewIntent)

        val pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder = NotificationCompat.Builder(context, PollWork.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(context.getString(R.string.notification_title, circular.id))
            .setContentText(circular.name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setGroup(PollWork.CHANNEL_ID)
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
            val name = context.getString(R.string.channel_name_new)
            val descriptionText = context.getString(R.string.channel_description_new)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(PollWork.CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
