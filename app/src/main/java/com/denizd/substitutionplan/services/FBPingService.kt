package com.denizd.substitutionplan.services

import android.app.job.JobParameters
import android.app.job.JobService
import android.preference.PreferenceManager
import com.denizd.substitutionplan.data.Topic
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging

/**
 * This class regularly resubscribes to keep the app in the background. This is supposed to resolve
 * issues regarding the app being force-closed in the background and not being able to receive
 * notifications.
 *
 * I am unsure about its effectiveness, though notifications do seem to arrive relatively reliably
 */
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
        if (!prefs.getBoolean("firstTime", true)) {
            FirebaseMessaging.getInstance().apply {
                if (prefs.getBoolean("notif", true)) {
                    subscribeToTopic(Topic.ANDROID.tag)
                } else {
                    unsubscribeFromTopic(Topic.ANDROID.tag)
                }

                if (prefs.getBoolean("subscribedToFBDebugChannel", false)) {
                    subscribeToTopic(Topic.DEVELOPMENT.tag)
                } else {
                    unsubscribeFromTopic(Topic.DEVELOPMENT.tag)
                }

                if (prefs.getBoolean("subscribedToiOSChannel", false)) {
                    subscribeToTopic(Topic.IOS.tag)
                } else {
                    unsubscribeFromTopic(Topic.IOS.tag)
                }

                subscribeToTopic(Topic.BROADCAST.tag)

                prefs.edit().putInt("pingFB", prefs.getInt("pingFB", 0) + 1).apply()
            }
        }
        jobFinished(params, false)
    }
}