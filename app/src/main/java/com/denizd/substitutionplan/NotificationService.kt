package com.denizd.substitutionplan

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager

class NotificationService : JobService() {

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
        val edit = prefs.edit()
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkStatus = connectivityManager.activeNetworkInfo

        Thread(Runnable {
            if (!prefs.getBoolean("notif", false) || jobCancelled || networkStatus == null) {
                jobFinished(params, false)
                return@Runnable
            } else {
                edit.putInt("notificationTestNumberDev", prefs.getInt("notificationTestNumberDev", 0) + 1).apply()
                try {
                    DataFetcher(true, true, true, context, application, null).execute()
                } catch (ignored: Exception) {}

                jobFinished(params, false)
            }
        }).start()
    }
}