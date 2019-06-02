package com.denizd.substitutionplan

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import java.io.IOException
import java.lang.NullPointerException
import java.net.MalformedURLException
import java.net.URL

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
        val statusMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state
        val statusWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state

        Thread(Runnable {
            if (jobCancelled) {
                return@Runnable
            } else if (statusMobile != NetworkInfo.State.CONNECTED && statusWifi != NetworkInfo.State.CONNECTED) {
                return@Runnable
            }
            if (prefs.getBoolean("notif", false)) {
                edit.putInt("notificationTestNumberDev", prefs.getInt("notificationTestNumberDev", 0) + 1).apply()
                try {
                    val url = URL("https://djd4rkn355.github.io/subst.html")
                    val connection = url.openConnection()
                    if (!connection.getHeaderField("Last-Modified").equals(prefs.getString("time", ""))) {
                        DataFetcher(true, true, true, context, application, null).execute()
                        edit.putString("time", connection.getHeaderField("Last-Modified")).apply()
                    }
                } catch (ignored: Exception) {}
            }
            jobFinished(params, false)
        }).start()
    }
}