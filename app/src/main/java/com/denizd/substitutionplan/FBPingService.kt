package com.denizd.substitutionplan

import android.app.job.JobParameters
import android.app.job.JobService
import android.preference.PreferenceManager
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

class FBPingService : JobService() {

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

        if (prefs.getBoolean("notif", true)) {
            FirebaseMessaging.getInstance().subscribeToTopic("substitutions-android")
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("substitutions-android")
        }

        if (prefs.getBoolean("subscribedToFBDebugChannel", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic("substitutions-debug")
        } else {
            FirebaseMessaging.getInstance().unsubscribeFromTopic("substitutions-debug")
        }

        FirebaseMessaging.getInstance().subscribeToTopic("substitutions-broadcast")
        prefs.edit().putInt("pingFB", prefs.getInt("pingFB", 0) + 1).apply()

        jobFinished(params, false)
    }
}