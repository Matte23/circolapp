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

package net.underdesk.circolapp.works

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.*
import kotlinx.coroutines.coroutineScope
import net.underdesk.circolapp.data.AndroidCircularRepository
import net.underdesk.circolapp.push.NotificationsUtils
import java.util.concurrent.TimeUnit

class PollWork(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val CHANNEL_ID = "net.underdesk.circolapp.NEW_CIRCULAR"

        private const val pollWorkName = "net.underdesk.circolapp.POLL_WORK"
        private const val repeatIntervalMin: Long = 15
        private const val flexIntervalMin: Long = 10

        private fun getPollWorkRequest(repeatInterval: Long): PeriodicWorkRequest {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            return PeriodicWorkRequestBuilder<PollWork>(
                repeatInterval,
                TimeUnit.MINUTES,
                flexIntervalMin,
                TimeUnit.MINUTES
            ).setConstraints(constraints).build()
        }

        fun enqueue(context: Context) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

            val notifyNewCirculars = sharedPreferences.getBoolean("notify_new_circulars", true)
            val enablePolling = sharedPreferences.getBoolean("enable_polling", false)

            if (notifyNewCirculars && enablePolling) {
                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(
                        pollWorkName,
                        ExistingPeriodicWorkPolicy.KEEP,
                        getPollWorkRequest(
                            sharedPreferences.getString(
                                "poll_interval",
                                null
                            )?.toLong() ?: repeatIntervalMin
                        )
                    )
            } else {
                WorkManager.getInstance(context)
                    .cancelUniqueWork(pollWorkName)
            }
        }

        fun runWork(context: Context) {
            val oneTimeWork = OneTimeWorkRequestBuilder<PollWork>().build()
            WorkManager.getInstance(context).enqueue(oneTimeWork)
        }
    }

    override suspend fun doWork(): Result = coroutineScope {
        val circularRepository = AndroidCircularRepository.getInstance(applicationContext)

        val result = circularRepository.updateCirculars()

        // Retry only if it's a network error, otherwise there is probably an issue with the parser code which can only be solved with an update
        if (result.second == -2)
            return@coroutineScope Result.retry()

        val newCirculars = result.first

        if (newCirculars.isNotEmpty()) {
            NotificationsUtils.createNotificationsForCirculars(newCirculars, applicationContext)
        }

        Result.success()
    }
}
