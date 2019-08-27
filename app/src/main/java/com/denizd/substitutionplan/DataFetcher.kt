package com.denizd.substitutionplan

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.preference.PreferenceManager
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Jsoup
import kotlin.collections.ArrayList

class DataFetcher(isplan: Boolean, ismenu: Boolean, isjobservice: Boolean, context: Context, application: Application, parentview: View?) : AsyncTask<Void, Void, Void>() {

    private var jobservice = isjobservice
    private var plan = isplan
    private var menu = ismenu

    /*  booleans to improve speed and decrease network usage when fetching data by only grabbing the required HTML pages
        "plan" = true is required to fetch the substitution data
        "menu" = true is required to fetch the food menu
        "jobservice" = true is required to send a notification
        "jobservice" = true has no effect unless "plan" is also true
     */

    private var mContext = context
    private var mApplication = application
    private var mView = parentview
    private var priority = 200
    private var notifText = ""
    private var informational = ""
    private val prefs = PreferenceManager.getDefaultSharedPreferences(mContext)
    private val edit = prefs.edit()
    private var currentTime = ""
    private var currentFoodTime = ""
    private var substUrl = "https://djd4rkn355.github.io/subst.html"
    private var foodUrl = "https://djd4rkn355.github.io/food.html"

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            if (prefs.getBoolean("testUrls", false)) {
                substUrl = "https://djd4rkn355.github.io/subst_test.html"
                foodUrl = "https://djd4rkn355.github.io/food_test.html"
            }
            if (menu) {
                val docFood = Jsoup.connect(foodUrl).get()
                currentFoodTime = docFood.select("h1")[0].text()
                if (currentFoodTime != prefs.getString("timeFoodNew", "")) {
                    val foodRepository = FoodRepository(mApplication)
                    val foodElements = docFood.select("th")
                    foodRepository.deleteAll()

                    val indices = ArrayList<Int>()
                    val daysAndVon = arrayOf("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "von")
                    for (i in 0 until foodElements.size) {
                        if (checkStringForArray(foodElements[i].text(), daysAndVon)) indices.add(i)
                    }
                    indices.add(foodElements.size)

                    for ((priority, l) in (0 until indices.size - 1).withIndex()) {
                        var s = ""
                        for (i2 in indices[l] until indices[l + 1]) {
                            if (s.isEmpty()) {
                                s = foodElements[i2].text()
                            } else {
                                s += "\n${foodElements[i2].text()}"
                            }
                        }
                        foodRepository.insert(Food(s, priority))
                    }
                    edit.putString("timeFoodNew", currentFoodTime).apply()
                }
            }

            if (plan) {
                val doc = Jsoup.connect(substUrl).get()
                currentTime = doc.select("h1")[0].text()
                if (currentTime != prefs.getString("timeNew", "")) {
                    val substRepo = SubstRepository(mApplication)
                    val rows = doc.select("tr")
                    val paragraphs = doc.select("p")

                    val coursePreference = prefs.getString("courses", "") ?: ""
                    val classPreference = prefs.getString("classes", "") ?: ""

                    for (i in 0 until paragraphs.size) {
                        if (i == 0) {
                            informational = paragraphs[i].text()
                        } else {
                            informational += "\n\n" + paragraphs[i].text()
                        }
                    }
                    edit.putString("informational", informational).apply()

                    substRepo.deleteAllSubst()

                    for (i in 0 until rows.size) {
                        val row = rows[i]
                        val cols = row.select("th")

                        val group = cols[0].text()
                        val course = cols[3].text()
                        val additional = cols[5].text()
                        substRepo.insert(Subst(group = group, date = cols[1].text(), time = cols[2].text(), course = course,
                                room = cols[4].text(), additional = additional, priority = priority))
                        priority--

                        if (jobservice && prefs.getBoolean("notif", true)) {
                            if (coursePreference.isEmpty() && classPreference.isNotEmpty()) {
                                if (group.isNotEmpty() && group != "") {
                                    if (classPreference.contains(group) || group.contains(classPreference)) {

                                        notifText += "${if (notifText.isNotEmpty()) ",\n" else ""}$course: ${if (additional.isNotEmpty()) additional else "---"}"
                                    }
                                }
                            } else if (classPreference.isNotEmpty() && coursePreference.isNotEmpty()) {
                                if (group != "" && course != "") {
                                    if (coursePreference.contains(course)) {
                                        if (classPreference.contains(group) || group.contains(classPreference)) {

                                            notifText += "${if (notifText.isNotEmpty()) ",\n" else ""}$course: ${if (additional.isNotEmpty()) additional else "---"}"
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (notifText.isNotEmpty()) {

                        val openApp = Intent(mContext, Main::class.java)
                        openApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        openApp.flags += Intent.FLAG_ACTIVITY_CLEAR_TASK
                        val openAppPending = PendingIntent.getActivity(mContext, 0, openApp, 0)

                        val notificationLayout = RemoteViews(mContext.packageName, R.layout.notification)
                        notificationLayout.setTextViewText(R.id.notification_title, mContext.getString(R.string.substitutionPlan))
                        notificationLayout.setTextViewText(R.id.notification_textview, notifText)

                        val manager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        val channelId = "general"
                        val channelName = mContext.getString(R.string.general)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
                            channel.enableLights(true)
                            channel.lightColor = Color.BLUE
                            manager.createNotificationChannel(channel)
                        }

                        val notification = NotificationCompat.Builder(mContext, channelId)
                                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                                .setCustomContentView(notificationLayout)
                                .setSmallIcon(R.drawable.ic_avh)
                                .setContentIntent(openAppPending)
                                .setAutoCancel(true)
                                .setColor(ContextCompat.getColor(mContext, R.color.colorAccent))

                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                            val sound = if ((prefs.getString("ringtoneUri", "") ?: "").isNotEmpty()) {
                                Uri.parse(prefs.getString("ringtoneUri", "") ?: "")
                            } else {
                                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                            }
                            notification.setSound(sound)
                        }

                        manager.notify(1, notification.build())
                    }

                    edit.putString("timeNew", currentTime).apply()
                }
            }

            if (plan || menu) {
                mView?.let { v: View ->
                    val snackText = mContext.getText(R.string.lastUpdated).toString() + when {
                        menu -> currentFoodTime
                        else -> currentTime
                    }
                    val snackBarView = v.findViewById<View>(R.id.coordination)
                    Snackbar.make(snackBarView, snackText, Snackbar.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            mView?.let { v: View ->
                val snackBarView = v.findViewById<View>(R.id.coordination)
                Snackbar.make(snackBarView, mContext.getString(R.string.noInternet), Snackbar.LENGTH_LONG).setBackgroundTint(ContextCompat.getColor(mContext, R.color.colorError)).show()
            }
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        mView?.let {
            try {
                it.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh).isRefreshing = false
            } catch (ignored: Exception) {}
        }
    }

    private fun checkStringForArray(s: String, checking: Array<String>): Boolean {
        for (i in checking.indices) {
            if (s.contains(checking[i])) {
                return true
            }
        }
        return false
    }
}