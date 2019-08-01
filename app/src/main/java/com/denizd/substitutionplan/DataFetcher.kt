package com.denizd.substitutionplan

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.AsyncTask
import android.os.Build
import android.preference.PreferenceManager
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import java.lang.IndexOutOfBoundsException
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
    private val substUrl = "https://djd4rkn355.github.io/subst.html"
    private val foodUrl = "https://djd4rkn355.github.io/food.html"

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            if (menu) {
                val foodViewModel = FoodViewModel(mApplication)
                val docFood = Jsoup.connect(foodUrl).get()
                val foodElements = docFood.select("th")
                currentFoodTime = docFood.select("h1")[0].text()
                if (currentFoodTime != prefs.getString("timeFood", "")) {

                    var foodInt = 0
                    var priority = 0
                    foodViewModel.deleteAll()
                    while (foodInt in 0 until foodElements.size) {
                        try { // what a mess this is!
                            with(foodElements[foodInt].text()) {
                                if (contains("Montag") || contains("Dienstag") || contains("Mittwoch") ||
                                        contains("Donnerstag") || contains("Freitag")) {
                                    val three = foodElements[foodInt + 3].text()
                                    val two = foodElements[foodInt + 2].text()
                                    if (three.contains("Montag") || three.contains("Dienstag") || three.contains("Mittwoch") ||
                                            three.contains("Donnerstag") || three.contains("Freitag") || three.contains("von")) {
                                        foodViewModel.insert(Food(foodElements[foodInt].text() + "\n"
                                                + foodElements[foodInt + 1].text() + "\n"
                                                + foodElements[foodInt + 2].text(), priority))
                                        foodInt += 2
                                    } else if (two.contains("Montag") || two.contains("Dienstag") || two.contains("Mittwoch") ||
                                            two.contains("Donnerstag") || two.contains("Freitag") || two.contains("von")) {
                                        foodViewModel.insert(Food(foodElements[foodInt].text() + "\n"
                                                + foodElements[foodInt + 1].text(), priority))
                                        foodInt += 1
                                    }
                                } else {
                                    foodViewModel.insert(Food(foodElements[foodInt].text(), priority))
                                }
                            }

                        } catch (e: IndexOutOfBoundsException) {
                            try {
                                foodViewModel.insert(Food(foodElements[foodInt].text() + "\n"
                                        + foodElements[foodInt + 1].text() + "\n"
                                        + foodElements[foodInt + 2].text(), priority))
                                break
                            } catch (e1: IndexOutOfBoundsException) {
                                foodViewModel.insert(Food(foodElements[foodInt].text() + "\n"
                                        + foodElements[foodInt + 1].text(), priority))
                                break
                            }
                        }
                        foodInt++
                        priority++
                    }
                    edit.putString("timeFood", currentFoodTime).apply()
                }
            }

            if (plan) {
                val substViewModel = SubstViewModel(mApplication)
                val doc = Jsoup.connect(substUrl).get()
                currentTime = doc.select("h1")[0].text()
                if (currentTime != prefs.getString("time", "")) {
                    val rows = doc.select("tr")
                    val paragraphs = doc.select("p")

                    val groupS = ArrayList<String>()
                    val dateS = ArrayList<String>()
                    val timeS = ArrayList<String>()
                    val courseS = ArrayList<String>()
                    val roomS = ArrayList<String>()
                    val additionalS = ArrayList<String>()

                    for (i in 0 until paragraphs.size) {
                        if (i == 0) {
                            informational = paragraphs[i].text()
                        } else {
                            informational += "\n\n" + paragraphs[i].text()
                        }
                    }
                    edit.putString("informational", informational).apply()

                    substViewModel.deleteAllSubst()

                    for (i in 0 until rows.size) {
                        val row = rows[i]
                        val cols = row.select("th") as Elements

                        groupS.add(cols[0].text())
                        dateS.add(cols[1].text())
                        timeS.add(cols[2].text())
                        courseS.add(cols[3].text())
                        roomS.add(cols[4].text())
                        additionalS.add(cols[5].text())

                        val drawable = MiscData.getIcon(courseS[i])
                        val subst = Subst(drawable, groupS[i], dateS[i], timeS[i], courseS[i],
                                roomS[i], additionalS[i], priority)
                        priority--
                        substViewModel.insertSubst(subst)

                        if (jobservice && prefs.getBoolean("notif", true)) {
                            if ((prefs.getString("courses", "")
                                            ?: "").isEmpty() && (prefs.getString("classes", "")
                                            ?: "").isNotEmpty()) {
                                if (groupS[i].isNotEmpty() && groupS[i] != "") {
                                    if ((prefs.getString("classes", "")
                                                    ?: "").contains(groupS[i]) || groupS[i].contains((prefs.getString("classes", "")
                                                    ?: "").toString())) {
                                        if (notifText.isNotEmpty()) {
                                            notifText += ", "
                                        }
                                        notifText += courseS[i] + ": " + additionalS[i]
                                    }
                                }
                            } else if ((prefs.getString("classes", "")
                                            ?: "").isNotEmpty() && (prefs.getString("courses", "")
                                            ?: "").isNotEmpty()) {
                                if (groupS[i] != "" && courseS[i] != "") {
                                    if ((prefs.getString("courses", "") ?: "").contains(courseS[i])) {
                                        if ((prefs.getString("classes", "")
                                                        ?: "").contains(groupS[i]) || groupS[i].contains((prefs.getString("classes", "")
                                                        ?: "").toString())) {
                                            if (notifText.isNotEmpty()) {
                                                notifText += ", "
                                            }
                                            notifText += courseS[i] + ": " + additionalS[i]
                                        }
                                    }
                                }
                            }
                        }
                        edit.putString("time", currentTime).apply()
                    }
                    if (jobservice && prefs.getBoolean("notif", true)) {
                        val openApp = Intent(mContext, Main::class.java)
                        openApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        openApp.flags += Intent.FLAG_ACTIVITY_CLEAR_TASK
                        val openAppPending = PendingIntent.getActivity(mContext, 0, openApp, 0)

                        val notificationLayout = RemoteViews(mContext.packageName, R.layout.notification)
                        notificationLayout.setTextViewText(R.id.notification_title, mContext.getString(R.string.substitutionPlan))
                        notificationLayout.setTextViewText(R.id.notification_textview, notifText)

                        if (notifText.isNotEmpty()) {
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
                                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                    .build()
                            manager.notify(1, notification)
                        }
                    }
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
                Snackbar.make(snackBarView, mContext.getString(R.string.noInternet), Snackbar.LENGTH_LONG).show()
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
}