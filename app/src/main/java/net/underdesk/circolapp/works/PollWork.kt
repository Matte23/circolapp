/*
 * Circolapp
 * Copyright (C) 2019  Matteo Schiff
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

package net.underdesk.circolapp.works

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import net.underdesk.circolapp.MainActivity
import net.underdesk.circolapp.R
import net.underdesk.circolapp.data.AppDatabase
import net.underdesk.circolapp.data.Circular
import net.underdesk.circolapp.server.DataFetcher
import java.util.concurrent.TimeUnit


class PollWork(appContext: Context, workerParams: WorkerParameters) :
    Worker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "net.underdesk.circolapp.NEW_CIRCULAR"

        private const val pollWorkName = "net.underdesk.circolapp.POLL_WORK"
        private const val repeatIntervalMin: Long = 30
        private const val flexIntervalMin: Long = 10

        private fun getPollWorkRequest(): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequestBuilder<PollWork>(
                repeatIntervalMin,
                TimeUnit.MINUTES,
                flexIntervalMin,
                TimeUnit.MINUTES
            ).setConstraints(constraints).build()
        }

        fun enqueue(context: Context) {
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    pollWorkName,
                    ExistingPeriodicWorkPolicy.KEEP,
                    getPollWorkRequest()
                )
        }
    }

    override fun doWork(): Result {
        val fetcher = DataFetcher()

        val oldCirculars = AppDatabase.getInstance(applicationContext).circularDao().getCirculars()
        val newCirculars = fetcher.getCircularsFromServer()

        if (newCirculars.size != oldCirculars.size) {
            for (i in oldCirculars.lastIndex..newCirculars.lastIndex) {
                createNotificationChannel()
                createNotification(newCirculars[i])
            }

            AppDatabase.getInstance(applicationContext).circularDao().deleteAll()
            AppDatabase.getInstance(applicationContext).circularDao().insertAll(newCirculars)
        }
        return Result.success()
    }

    private fun createNotification(circular: Circular) {
        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivity(applicationContext, 0, intent, 0)

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_black_24dp)
            .setContentTitle(applicationContext.getString(R.string.notification_title, circular.id))
            .setContentText(circular.name)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setGroup(CHANNEL_ID)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(circular.name)
            )

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(circular.id.toInt(), builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.channel_name)
            val descriptionText = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
