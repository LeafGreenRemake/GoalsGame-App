package com.example.kotlintodopractice.utils.jobSchedule

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit

class WeeklyJobScheduler(private val context: Context) {

    fun scheduleJob() {
        val calendar = Calendar.getInstance()
        calendar.time = Calendar.getInstance().time
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 0)

        var initialDelay = calendar.timeInMillis - System.currentTimeMillis()

        if (initialDelay < 0) {
            // If the initial delay is negative, it means the desired time has already passed this week
            // So, add 7 days to the initial delay to schedule the job for next Saturday
            initialDelay += 7 * 24 * 60 * 60 * 1000
        }

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        val workRequest = OneTimeWorkRequest.Builder(WeeklyResetWorker::class.java)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "weekly_reset_job",
            ExistingPeriodicWorkPolicy.REPLACE,
            PeriodicWorkRequest.Builder(
                WeeklyResetWorker::class.java,
                7, // interval (7 days)
                TimeUnit.DAYS // time unit
            )
                .setConstraints(constraints)
                .build()
        )
    }
}