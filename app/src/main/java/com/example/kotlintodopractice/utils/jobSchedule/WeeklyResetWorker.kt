package com.example.kotlintodopractice.utils.jobSchedule

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.database.FirebaseDatabase

class WeeklyResetWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Get the Firebase Realtime Database instance
        val database = FirebaseDatabase.getInstance()
        val usersRef = database.reference.child("users")

        // Get all users from the Firebase Realtime Database
        usersRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val users = task.result.children
                for (user in users) {
                    // Update the score of each user
                    user.child("score").ref.setValue(0)
                }
            } else {
                // Handle the error
                Log.e("WeeklyResetWorker", "Error getting users: ${task.exception}")
            }
        }

        return Result.success()
    }
}