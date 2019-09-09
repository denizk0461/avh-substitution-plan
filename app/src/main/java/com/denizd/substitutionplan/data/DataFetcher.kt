package com.denizd.substitutionplan.data

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.denizd.substitutionplan.database.FoodRepository
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.database.SubstRepository
import com.denizd.substitutionplan.activities.Main
import com.denizd.substitutionplan.models.Food
import com.denizd.substitutionplan.models.Subst
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Jsoup
import kotlin.collections.ArrayList

/**
 *  This class gets data from the djd4rkn355.github.io domain asynchronously and persists it in the
 *  database. By using booleans as parameters, network usage and speed can be improved as only
 *  requested data will be downloaded and processed. Furthermore, the class provides a boolean
 *  to determine whether to send a notification to the user, if applicable.
 *
 *  @param isPlan           substitution plan will be downloaded and persisted in the database if true
 *  @param isMenu           food menu will be downloaded and persisted in the database if true
 *  @param isJobService     a notification will be prepared and sent if this and isPlan is true
 */
internal class DataFetcher(isPlan: Boolean, isMenu: Boolean, isJobService: Boolean, context: Context, application: Application, parentView: View?, forced: Boolean) : AsyncTask<Void, Void, Void>() {

    private var jobService = isJobService
    private var plan = isPlan
    private var menu = isMenu
    private var forceRefresh = forced

    private var mContext = context
    private var mApplication = application
    private var mView = parentView
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

            if (forceRefresh) {
                edit.putString("timeNew", "").putString("timeFoodNew", "").apply()
            }

            if (menu) {
                requestFoodMenuData()
            }
            if (plan) {
                requestSubstPlan()
            }

            if (plan || menu) {
                mView?.let { v: View ->
                    var snackText = "${mContext.getText(R.string.lastUpdated)} ${when {
                        plan -> currentTime
                        menu -> currentFoodTime
                        else -> "---"
                    }}"
                    snackText = if (forceRefresh) mContext.getString(R.string.forcedRefresh) else snackText
                    val snackBarView = v.findViewById<View>(R.id.coordination)
                    Snackbar.make(snackBarView, snackText, Snackbar.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            mView?.let { v: View ->
                val snackBarView = v.findViewById<View>(R.id.coordination)
                Snackbar.make(snackBarView, mContext.getString(R.string.noInternet), Snackbar.LENGTH_LONG).setBackgroundTint(ContextCompat.getColor(mContext,
                    R.color.colorError
                )).show()
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

    private fun requestFoodMenuData() {
        val docFood = Jsoup.connect(foodUrl).get()
        currentFoodTime = docFood.select("h1")[0].text()
        if (currentFoodTime != prefs.getString("newFoodTime", "")) {
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
            edit.putString("newFoodTime", currentFoodTime).apply()
        }
    }

    private fun requestSubstPlan() {
        val doc = Jsoup.connect(substUrl).get()
        currentTime = doc.select("h1")[0].text()
        if (currentTime != prefs.getString("timeNew", "")) {
            val substRepo = SubstRepository(mApplication)
            val rows = doc.select("tr")
            val paragraphs = doc.select("p")
            val substArray = ArrayList<Subst>()

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
                val subst = Subst(
                    group = group, date = cols[1].text(), time = cols[2].text(), course = course,
                    room = cols[4].text(), additional = additional, teacher = cols[6].text(), priority = priority
                )
                Log.d("AAAA", cols[6].text())
                substArray.add(subst)
                substRepo.insert(subst)
                priority--
            }

            if (jobService && prefs.getBoolean("notif", true)) {
                substArray.filter { substitution ->
                    HelperFunctions.checkPersonalSubstitutions(
                        substitution,
                        coursePreference,
                        classPreference,
                        false
                    )
                }.forEach { substItem ->
                    notifText += "${if (notifText.isNotEmpty()) ",\n" else ""}${substItem.course}: ${if (substItem.additional.isNotEmpty()) substItem.additional else "---"}"
                }
            }

            if (notifText.isNotEmpty()) {
                sendNotification()
            }

            edit.putString("timeNew", currentTime).apply()
        }
    }

    private fun sendNotification() {
        val openApp = Intent(mContext, Main::class.java)
        openApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        openApp.flags += Intent.FLAG_ACTIVITY_CLEAR_TASK
        val openAppPending = PendingIntent.getActivity(mContext, 0, openApp, 0)

        val notificationLayout = RemoteViews(mContext.packageName,
            R.layout.notification
        )
        notificationLayout.setTextViewText(
            R.id.notification_title, mContext.getString(
                R.string.substitutionPlan
            ))
        notificationLayout.setTextViewText(R.id.notification_textview, notifText)

        val manager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            HelperFunctions.getNotificationChannel(mContext, prefs)
        }

        val notification = NotificationCompat.Builder(mContext, HelperFunctions.notificationChannelId)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setSmallIcon(R.drawable.ic_avh)
            .setContentIntent(openAppPending)
            .setAutoCancel(true)
            .setColor(ContextCompat.getColor(mContext,
                R.color.colorAccent
            ))

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

    private fun checkStringForArray(s: String, checking: Array<String>): Boolean {
        for (i in checking.indices) {
            if (s.contains(checking[i])) {
                return true
            }
        }
        return false
    }
}