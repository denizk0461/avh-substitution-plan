package com.denizd.substitutionplan

import android.app.job.JobParameters
import android.app.job.JobService
import android.preference.PreferenceManager
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

        Thread(Runnable {
            if (jobCancelled) {
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
                } catch (ignored: NullPointerException) {}
                catch (ignored: IOException) {}
                catch (ignored: MalformedURLException) {}
            }
            jobFinished(params, false)
        }).start()
    }
}