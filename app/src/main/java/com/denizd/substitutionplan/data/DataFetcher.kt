package com.denizd.substitutionplan.data

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.util.Log
import androidx.preference.PreferenceManager
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
import com.denizd.substitutionplan.models.Substitution
import com.google.android.material.snackbar.Snackbar
import org.jsoup.Jsoup
import java.lang.ref.WeakReference
import kotlin.collections.ArrayList

/**
 *  This class gets data from the djd4rkn355.github.io domain asynchronously and persists it in the
 *  database. By using booleans as parameters, network usage and speed can be improved as only
 *  requested data will be downloaded and processed. Furthermore, the class provides a boolean
 *  to determine whether to send a notification to the user, if applicable.
 *
 *  @param isPlan           substitution plan will be downloaded and persisted in the database if true
 *  @param isMenu           food menu will be downloaded and persisted in the database if true
 *  @param isJobService     enables sending of a notification if true
 *  @param forced           if true, times for food menu and substitution table will be overwritten
 *                          with empty values to enable a forced refresh
 *  @param context          the context of the application that will be stored as a WeakReference
 *                          to avoid memory leakage
 *  @param application      a reference to the application that will be stored as a WeakReference
 *  @param parentView       a reference to the parent view that will be stored as a WeakReference
 */
internal class DataFetcher(
        isPlan: Boolean,
        isMenu: Boolean,
        isJobService: Boolean = false,
        forced: Boolean = false,
        context: Context,
        application: Application,
        parentView: View?
) : AsyncTask<Void, Void, Void>() {

    private var jobService = isJobService
    private var plan = isPlan
    private var menu = isMenu
    private var forceRefresh = forced

    private var mContext = WeakReference(context)
    private var mApplication = application
    private var mView = WeakReference(parentView)
    private var notificationText = ""
    private var informational = ""
    private val prefs = PreferenceManager.getDefaultSharedPreferences(mContext.get())
    private val edit = prefs.edit()
    private var currentTime = ""
    private var currentFoodTime = ""
    private var substUrl = "https://djd4rkn355.github.io/avh_substitutions.html"
    private var foodUrl = "https://djd4rkn355.github.io/food.html"
    private var websitePriority = 0

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            if (prefs.getBoolean("testUrls", false)) {
                substUrl = "https://djd4rkn355.github.io/subst_test.html"
                foodUrl = "https://djd4rkn355.github.io/food_test.html"
            } else if ((prefs.getString("custom_test_url", "") ?: "").isNotEmpty()) {
                substUrl = "https://djd4rkn355.github.io/${prefs.getString("custom_test_url", "")}"
            }

            if (forceRefresh) {
                edit.putString("timeNew", "").putString("newFoodTime", "").apply()
            }

            if (menu) {
                requestFoodMenuData()
            }
            if (plan) {
                requestSubstPlan()
            }

            if (plan || menu) {
                mView.get()?.let { v: View ->
                    var snackText = "${mContext.get()?.getText(R.string.last_updated)} ${when {
                        plan -> currentTime
                        menu -> currentFoodTime
                        else -> "---"
                    }}"
                    snackText = if (forceRefresh) mContext.get()?.getString(R.string.force_refresh_successful) ?: "" else snackText
                    val snackBarView = v.findViewById<View>(R.id.coordination)
                    Snackbar.make(snackBarView, snackText, Snackbar.LENGTH_LONG).show()
                }
            }
        } catch (e: Exception) {
            mView.get()?.let { v: View ->
                val snackBarView = v.findViewById<View>(R.id.coordination)
                Snackbar.make(snackBarView, mContext.get()?.getString(R.string.no_internet_connection) ?: "", Snackbar.LENGTH_LONG).setBackgroundTint(ContextCompat.getColor(mContext.get()!!,
                    R.color.colorError
                )).show()
            }
            prefs.edit().putString("debug_recent_exception", Log.getStackTraceString(e)).apply()
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        mView.get()?.let {
            try {
                it.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout).isRefreshing = false
            } catch (ignored: Exception) {
            }
        }
        mView.clear()
        mContext.clear()
    }

    private fun requestFoodMenuData() {
        val docFood = Jsoup.connect(foodUrl).get()
        currentFoodTime = docFood.select("h1")[0].text()
        val foodRepository = FoodRepository(mApplication)
        if (currentFoodTime != prefs.getString("newFoodTime", "")) {
            val foodElements = docFood.select("th")
            foodRepository.deleteAll()

            val indices = ArrayList<Int>()
            val daysAndVon = arrayOf("montag", "dienstag", "mittwoch", "donnerstag", "freitag", "von", "wünschen")
            for (i in 0 until foodElements.size) {
                if (SubstUtil.checkStringForArray(foodElements[i].text(), daysAndVon, true)) indices.add(i)
            }
            indices.add(foodElements.size)

            for (l in 0 until indices.size - 1) {
                var s = ""
                for (i2 in indices[l] until indices[l + 1]) {
                    if (s.isEmpty()) {
                        s = foodElements[i2].text()
                    } else {
                        s += "\n${foodElements[i2].text()}"
                    }
                }
                foodRepository.insert(Food(s, l))
            }
            edit.putString("newFoodTime", currentFoodTime).apply()
        }
    }

    private fun requestSubstPlan() {
        val doc = Jsoup.connect(substUrl).get()
        currentTime = doc.select("h1")[0].text()
        val substRepo = SubstRepository(mApplication)
        if (currentTime != prefs.getString("timeNew", "")) {
            val rows = doc.select("tr")
            val paragraphs = doc.select("h6")
            val substArray = ArrayList<Substitution>()

            val coursePreference = prefs.getString("courses", "") ?: ""
            val classPreference = prefs.getString("classes", "") ?: ""

            for (i in 0 until paragraphs.size) {
                if (i == 0) {
                    informational = formatElement(paragraphs[i].html())
                } else {
                    informational += "\n\n" + formatElement(paragraphs[i].html())
                }
            }
            edit.putString("informational", informational).apply()

            substRepo.deleteAllSubst()

            for (i in 0 until rows.size) {
                val row = rows[i]
                val cols = row.select("th")

                val group = cols[0].text()
                val date = cols[1].text()
                val subst = Substitution(
                    group = group,
                    date = date,
                    time = cols[2].text(),
                    course = cols[3].text(),
                    room = cols[4].text(),
                    additional = cols[5].text(),
                    teacher = cols[6].text(),
                    type = cols[7].text(),
                    priority = SubstUtil.assignRanking(group, (date.length > 2 && date.substring(0, 3) == "psa")),
                    date_priority = SubstUtil.assignDatePriority(date),
                    website_priority = websitePriority
                )
                substArray.add(subst)
                substRepo.insert(subst)
                websitePriority += 1
            }
            var countOfNotificationItems = 0
            var countOfMoreNotificationItems = 0
            if (jobService && prefs.getBoolean("notif", true)) {
                substArray.filter { substItem ->
                    SubstUtil.checkPersonalSubstitutions(
                        substItem,
                        coursePreference,
                        classPreference,
                        false
                    )
                }.forEach { substItem ->
                    if (countOfNotificationItems < 4) {
                        notificationText += ("${if (notificationText.isNotEmpty()) ",\n" else ""}${substItem.course}: ${if (substItem.additional.isNotEmpty()) substItem.additional else if (substItem.type.isNotEmpty()) substItem.type else "---"}")
                        countOfNotificationItems += 1
                    } else {
                        countOfMoreNotificationItems += 1
                    }
                }
                if (countOfMoreNotificationItems > 0) {
                    notificationText += mContext.get()?.resources?.getQuantityString(R.plurals.notification_more_messages, countOfMoreNotificationItems, countOfMoreNotificationItems)
                }
            }

            if (notificationText.isNotEmpty()) {
                sendNotification()
            }

            edit.putString("timeNew", currentTime).apply()
        }
    }

    private fun sendNotification() {
        mContext.get()?.let { context ->
            val openApp = Intent(context, Main::class.java)
            openApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            openApp.flags += Intent.FLAG_ACTIVITY_CLEAR_TASK
            val openAppPending = PendingIntent.getActivity(context, 0, openApp, 0)

            val notificationLayout = RemoteViews(context.packageName, R.layout.notification)
            notificationLayout.setTextViewText(R.id.notification_title, context.getString(
                R.string.substitution_plan
            ))
            notificationLayout.setTextViewText(R.id.notification_textview, notificationText)

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SubstUtil.getNotificationChannel(context, prefs)
            }

            val notification = NotificationCompat.Builder(context, SubstUtil.notificationChannelId)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(notificationLayout)
                .setContentIntent(openAppPending)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))

            notification.setSmallIcon(if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) R.drawable.ic_avh else R.drawable.ic_avhlogo)

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
    }

    private fun formatElement(element: String): String {
        return element
                .replace("<br>", "\n")
    }
}