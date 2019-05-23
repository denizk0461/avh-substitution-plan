package com.denizd.substitutionplan

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.media.RingtoneManager
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.preference.PreferenceManager
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ProgressBar
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.madapps.prefrences.EasyPrefrences
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.lang.IndexOutOfBoundsException
import java.lang.NullPointerException
import java.net.URL
import java.net.URLConnection
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DataFetcher(isplan: Boolean, ismenu: Boolean, isjobservice: Boolean, context: Context, application: Application, parentview: View?) : AsyncTask<Void, Void, Void>() {

    var jobservice = isjobservice
    var plan = isplan
    var menu = ismenu

    /*  booleans to improve performance when fetching data
        "plan" is required to fetch the substitution data
        "menu" is required to fetch the food menu
        "jobservice" is required to send a notification
        "jobservice" = true has no effect unless "plan" is also true
     */

    var mContext = context
    var mApplication = application
    var mView = parentview
    var priority = 200
    var notifText = ""
    var informational = ""
    var foodList = ArrayList<String>()
    lateinit var connection: URLConnection
    lateinit var rows: Elements
    lateinit var paragraphs: Elements
    lateinit var foodElements: Elements
    lateinit var doc: Document
    lateinit var docFood: Document
    var manager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "general"
    var channelName = mContext.getString(R.string.general)
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    lateinit var channel: NotificationChannel
    lateinit var notification: Notification
    val prefs = PreferenceManager.getDefaultSharedPreferences(mContext) as SharedPreferences
    val edit = prefs.edit() as SharedPreferences.Editor
    lateinit var pullToRefresh: SwipeRefreshLayout
    lateinit var progressBar: ProgressBar
    val easyPrefs = EasyPrefrences(mContext)
    val substViewModel = SubstViewModel(mApplication)
    val oldDateFormat = "EEE, dd MMM yyyy HH:mm:ss zzz"
    val newDateFormat = "yyyy-MM-dd, HH:mm:ss"
    var sdf = SimpleDateFormat(oldDateFormat)
    lateinit var d: Date

    override fun doInBackground(vararg params: Void?): Void? {

        try {
            mView?.let { v: View ->
                pullToRefresh = v.findViewById(R.id.pullToRefresh)
                progressBar = v.findViewById(R.id.progressBar)
                progressBar.progress = 0
            }
            if (menu) {
                docFood = Jsoup.connect("https://djd4rkn355.github.io/food.html").get()
                foodElements = docFood.select("th")

                mView?.let {
                    progressBar.max = foodElements.size
                }

                var foodInt = 0
                while (foodInt in 0 until foodElements.size) {
                    try { // what a mess this is!
                        if (foodElements[foodInt].text().contains("Montag") ||
                                foodElements[foodInt].text().contains("Dienstag") ||
                                foodElements[foodInt].text().contains("Mittwoch") ||
                                foodElements[foodInt].text().contains("Donnerstag") ||
                                foodElements[foodInt].text().contains("Freitag")) {

                            if (foodElements[foodInt + 3].text().contains("Montag") ||
                                    foodElements[foodInt + 3].text().contains("Dienstag") ||
                                    foodElements[foodInt + 3].text().contains("Mittwoch") ||
                                    foodElements[foodInt + 3].text().contains("Donnerstag") ||
                                    foodElements[foodInt + 3].text().contains("Freitag") ||
                                    foodElements[foodInt + 3].text().contains("von")) {
                                foodList.add(foodElements[foodInt].text() + "\n"
                                        + foodElements[foodInt + 1].text() + "\n"
                                        + foodElements[foodInt + 2].text())
                                foodInt += 2
                            } else if (foodElements[foodInt + 2].text().contains("Montag") ||
                                    foodElements[foodInt + 2].text().contains("Dienstag") ||
                                    foodElements[foodInt + 2].text().contains("Mittwoch") ||
                                    foodElements[foodInt + 2].text().contains("Donnerstag") ||
                                    foodElements[foodInt + 2].text().contains("Freitag") ||
                                    foodElements[foodInt + 2].text().contains("von")) {
                                foodList.add(foodElements[foodInt].text() + "\n"
                                        + foodElements[foodInt + 1].text())
                                foodInt += 1
                            }

                        } else {
                            foodList.add(foodElements[foodInt].text())
                        }
                    } catch (e: IndexOutOfBoundsException) {
                        try {
                            foodList.add(foodElements[foodInt].text() + "\n"
                                    + foodElements[foodInt + 1].text() + "\n"
                                    + foodElements[foodInt + 2].text())
                            break
                        } catch (e1: IndexOutOfBoundsException) {
                            foodList.add(foodElements[foodInt].text() + "\n"
                                    + foodElements[foodInt + 1].text())
                            break
                        }
                    }
                    foodInt++
                    mView?.let {
                        progressBar.incrementProgressBy(1)
                    }
                }

                easyPrefs.putListString("foodListPrefs", foodList)
            }

            if (plan) {
                doc = Jsoup.connect("https://djd4rkn355.github.io/subst").get()
                connection = URL("https://djd4rkn355.github.io/subst").openConnection()
                edit.putString("time", connection.getHeaderField("Last-Modified")).apply()
                d = Date(connection.getHeaderField("Last-Modified"))
                rows = doc.select("tr")
                paragraphs = doc.select("p")

                val groupS = arrayOfNulls<String>(rows.size)
                val dateS = arrayOfNulls<String>(rows.size)
                val timeS = arrayOfNulls<String>(rows.size)
                val courseS = arrayOfNulls<String>(rows.size)
                val roomS = arrayOfNulls<String>(rows.size)
                val additionalS = arrayOfNulls<String>(rows.size)

                mView?.let {
                    progressBar.max = rows.size
                }

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

                    groupS[i] = cols[0].text()
                    dateS[i] = cols[1].text()
                    timeS[i] = cols[2].text()
                    courseS[i] = cols[3].text()
                    roomS[i] = cols[4].text()
                    additionalS[i] = cols[5].text()
                    if (!jobservice) {
                        mView?.let {
                            progressBar.incrementProgressBy(1)
                        }
                    }

                    val md = MiscData()
                    val drawable = md.getIcon(courseS[i].toString())
                    val subst = Subst(drawable, groupS[i].toString(), dateS[i].toString(), timeS[i].toString(), courseS[i].toString(),
                            roomS[i].toString(), additionalS[i].toString(), priority)
                    priority--
                    substViewModel.insert(subst)

                    if (jobservice) {
                        if (prefs.getString("courses", "").isEmpty() && prefs.getString("classes", "").isNotEmpty()) {
                            if (groupS[i].toString().isNotEmpty() && !groupS[i].equals("")) {
                                if (prefs.getString("classes", "").contains(groupS[i].toString()) || groupS[i].toString().contains(prefs.getString("classes", "").toString())) {
                                    if (notifText.isNotEmpty()) {
                                        notifText += ", "
                                    }
                                    notifText += courseS[i] + ": " + additionalS[i]
                                }
                            }
                        } else if (prefs.getString("classes", "").isNotEmpty() && prefs.getString("courses", "").isNotEmpty()) {
                            if (!groupS[i].equals("") && !courseS[i].equals("")) {
                                if (prefs.getString("courses", "").contains(courseS[i].toString())) {
                                    if (prefs.getString("classes", "").contains(groupS[i].toString()) || groupS[i].toString().contains(prefs.getString("classes", "").toString())) {
                                        if (notifText.isNotEmpty()) {
                                            notifText += ", "
                                        }
                                        notifText += courseS[i] + ": " + additionalS[i]
                                    }
                                }
                            }
                        }
                    }
                }
                if (jobservice) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        channel = NotificationChannel(channelId, channelName, importance)
                        channel.enableLights(true)
                        channel.lightColor = Color.BLUE
                        manager.createNotificationChannel(channel)
                    }

                    val openApp = Intent(mContext, Main::class.java)
                    openApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    openApp.flags += Intent.FLAG_ACTIVITY_CLEAR_TASK
                    val openAppPending = PendingIntent.getActivity(mContext, 0, openApp, 0) // PendingIntent.FLAG_ONE_SHOT

                    val notificationLayout = RemoteViews(mContext.packageName, R.layout.notification)
                    notificationLayout.setTextViewText(R.id.notification_title, mContext.getString(R.string.subst))
                    notificationLayout.setTextViewText(R.id.notification_textview, notifText)

                    if (notifText.isNotEmpty()) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            notification = NotificationCompat.Builder(mContext) // TODO switch out the deprecated notification delivery method
                                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                                    .setCustomContentView(notificationLayout)
                                    .setSmallIcon(R.drawable.ic_avh)
                                    .setChannelId(channelId)
                                    .setContentIntent(openAppPending)
                                    .setAutoCancel(true)
                                    .build()
                        } else {
                            notification = NotificationCompat.Builder(mContext)
                                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                                    .setCustomContentView(notificationLayout)
                                    .setSmallIcon(R.drawable.ic_avh)
                                    .setContentIntent(openAppPending)
                                    .setAutoCancel(true)
                                    .build()
                        }
                        notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                        manager.notify(1, notification) // TODO notification plays multiple times
                    }
                }

                mView?.let { v: View ->
                    val fadeOut = AnimationUtils.loadAnimation(mContext, R.anim.fade_out)

                    val handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({ progressBar.startAnimation(fadeOut) }, 200)

                    fadeOut.setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationStart(arg0: Animation) {}

                        override fun onAnimationRepeat(arg0: Animation) {}

                        override fun onAnimationEnd(arg0: Animation) {
                            progressBar.progress = 0
                        }
                    })
                    pullToRefresh.isRefreshing = false
                    val snackbarview = v.findViewById<View>(R.id.coordination)
                    sdf.applyPattern(newDateFormat)
                    val sb = StringBuilder()
                    val lastupdated = sb.append(mContext.getText(R.string.lastupdatedK)).append(sdf.format(d)).toString()
                    Snackbar.make(snackbarview, lastupdated, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                }

            }

        } catch (e: NullPointerException) {
            mView?.let { v: View ->
                pullToRefresh.isRefreshing = false
                val snackbarview = v.findViewById<View>(R.id.coordination)
                Snackbar.make(snackbarview, mContext.getText(R.string.nointernet), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
            }
        }
        return null
    }
}