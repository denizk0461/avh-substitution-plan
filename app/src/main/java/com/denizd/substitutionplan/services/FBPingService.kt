package com.denizd.substitutionplan.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

internal class FBPingService : JobService() {

    private var jobCancelled = false
    private val context = this

    override fun onStartJob(params: JobParameters): Boolean {
        doBackgroundWork(params)
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        jobCancelled = true
        return true
    }

    private fun doBackgroundWork(params: JobParameters) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)

        FirebaseApp.initializeApp(context)

        FirebaseMessaging.getInstance().apply {

            if (prefs.getBoolean("notif", true)) {
                subscribeToTopic("substitutions-android")
            } else {
                unsubscribeFromTopic("substitutions-android")
            }

            if (prefs.getBoolean("subscribedToFBDebugChannel", false)) {
                subscribeToTopic("substitutions-debug")
            } else {
                unsubscribeFromTopic("substitutions-debug")
            }

            if (prefs.getBoolean("subscribedToiOSChannel", false)) {
                subscribeToTopic("substitutions-ios")
            } else {
                unsubscribeFromTopic("substitutions-ios")
            }

            subscribeToTopic("substitutions-broadcast")

            prefs.edit().putInt("pingFB", prefs.getInt("pingFB", 0) + 1).apply()

        }
        jobFinished(params, false)
    }
}