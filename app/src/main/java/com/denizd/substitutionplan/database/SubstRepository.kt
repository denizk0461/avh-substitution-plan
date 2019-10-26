package com.denizd.substitutionplan.database

import android.app.Application
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

import androidx.lifecycle.LiveData
import androidx.preference.PreferenceManager
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.activities.Main
import com.denizd.substitutionplan.data.Caller
import com.denizd.substitutionplan.data.SubstUtil
import com.denizd.substitutionplan.models.Food
import com.denizd.substitutionplan.models.Substitution
import org.jsoup.Jsoup

internal class SubstRepository(private val application: Application) {

    private val substitutionDao: SubstDao?
    private val foodMenuDao: SubstDao?
    val allSubstitutionsSorted: LiveData<List<Substitution>>?
    val allSubstitutionsOriginal: LiveData<List<Substitution>>?
    val allFoodItems: LiveData<List<Food>>?

    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)

    val substitutionPlanColours: Map<String, String>
            get() = mapOf(
                "German" to prefs.getNonNullString("cardGerman"),
                "Maths" to prefs.getNonNullString("cardMaths"),
                "English" to prefs.getNonNullString("cardEnglish"),
                "PhysEd" to prefs.getNonNullString("cardPhysEd"),
                "Politics" to prefs.getNonNullString("cardPolitics"),
                "Theatre" to prefs.getNonNullString("cardTheatre"),
                "Physics" to prefs.getNonNullString("cardPhysics"),
                "Biology" to prefs.getNonNullString("cardBiology"),
                "Chemistry" to prefs.getNonNullString("cardChemistry"),
                "Philosophy" to prefs.getNonNullString("cardPhilosophy"),
                "Latin" to prefs.getNonNullString("cardLatin"),
                "Spanish" to prefs.getNonNullString("cardSpanish"),
                "French" to prefs.getNonNullString("cardFrench"),
                "CompSci" to prefs.getNonNullString("cardCompSci"),
                "History" to prefs.getNonNullString("cardHistory"),
                "Religion" to prefs.getNonNullString("cardReligion"),
                "Geography" to prefs.getNonNullString("cardGeography"),
                "Arts" to prefs.getNonNullString("cardArts"),
                "Music" to prefs.getNonNullString("cardMusic"),
                "Turkish" to prefs.getNonNullString("cardTurkish"),
                "Chinese" to prefs.getNonNullString("cardChinese"),
                "GLL" to prefs.getNonNullString("cardGLL"),
                "WAT" to prefs.getNonNullString("cardWAT"),
                "Forder" to prefs.getNonNullString("cardForder"),
                "WP" to prefs.getNonNullString("cardWP")
    )
    val shouldAutoRefresh: Boolean
        get() = prefs.getBoolean("autoRefresh", false)

    val shouldUseAppSorting: Boolean
        get() = prefs.getBoolean("app_specific_sorting", true)

    val emptyGeneralPlan: List<Substitution> by lazy {
        listOf(Substitution("", "", "", application.getString(R.string.plan_empty), "", "", "", "", 0, 0, 0))
    }

    val emptyPersonalSubstitution: Substitution by lazy {
        Substitution("", "", "", application.getString(R.string.personal_plan_empty), "", "", "", "", 0, 0, 0)
    }

    val emptyFoodMenu: List<Food> by lazy {
        listOf(Food("\n${application.getString(R.string.food_menu_empty)}\n", 0))
    }

    init {
        val substInstance = SubstDatabase.getSubstInstance(application)
        val foodInstance = SubstDatabase.getFoodInstance(application)
        substitutionDao = substInstance?.substDao()
        foodMenuDao = foodInstance?.substDao()
        allSubstitutionsSorted = substitutionDao?.allSubstitutionsSorted
        allSubstitutionsOriginal = substitutionDao?.allSubstitutionsOriginal
        allFoodItems = foodMenuDao?.allFoods
    }

    private fun insert(substitution: Substitution) {
        substitutionDao?.insertSubst(substitution)
    }

    private fun insert(food: Food) {
        foodMenuDao?.insertFood(food)
    }

    private fun deleteAllSubst() {
        substitutionDao?.deleteAllSubst()
    }

    private fun deleteAllFoodItems() {
        foodMenuDao?.deleteAllFoods()
    }

    fun getGridColumnCount(config: Configuration): Int {
        return if (application.resources.getBoolean(R.bool.isTablet)) {
            when (config.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 2
                else -> 3
            }
        } else {
            when (config.orientation) {
                Configuration.ORIENTATION_PORTRAIT -> 1
                else -> 2
            }
        }
    }

    private fun SharedPreferences.getNonNullString(key: String): String {
        return getString(key, "") ?: ""
    }

    fun externalNonNullString(key: String): String {
        return prefs.getNonNullString(key)
    }

    private fun SharedPreferences.putAndApplyString(key: String, value: String) {
        edit().putString(key, value).apply()
    }

    fun putAndApplyString(key: String, value: String) {
        prefs.putAndApplyString(key, value)
    }

    private fun Substitution.checkIfPersonal(classPreference: String, coursePreference: String, includesPsa: Boolean = false): Boolean {
        if (includesPsa && date.isNotEmpty() && date.substring(0, 3) == "psa") {
            return true
        }
        if (coursePreference.isEmpty() && classPreference.isNotEmpty()) {
            if (group.isNotEmpty()) {
                if (classPreference.contains(group) || group.contains(classPreference)) {
                    return true
                }
            }
        } else if (classPreference.isNotEmpty() && coursePreference.isNotEmpty()) {
            if (group != "" && course != "") {
                if ((classPreference.contains(group) || group.contains(classPreference))) {
                    val questionMark = course.indexOf("?")
                    if (questionMark == -1) {
                        if (coursePreference.contains(course)) {
                            return true
                        }
                    } else {
                        try {
                            /*
                            These if statements are separated for if, for example, this shows up:
                            "INF7?"
                            if they were nested in a single if statement, the function may not be
                            able to recognise the course and throw an exception instead
                            */
                            if (coursePreference.contains(course.substring(0, questionMark - 1))) {
                                return true
                            } else if (coursePreference.contains(course.substring(questionMark + 1))) {
                                return true
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }
        }
        return false
    }

    fun checkIfSubstitutionPersonal(substitution: Substitution, classPreference: String, coursePreference: String, includesPsa: Boolean): Boolean {
        return substitution.checkIfPersonal(classPreference, coursePreference, includesPsa)
    }

    private fun getData(caller: Caller): Pair<String, Boolean> {

        fun requestSubstPlan(url: String): Pair<String, Boolean> {
            var notificationText = ""
            var hasUpdated = false

            val doc = Jsoup.connect(url).get()
            val currentTime = doc.select("h1")[0].text()

            if (currentTime != prefs.getNonNullString("timeNew")) {
                val rows = doc.select("tr")
                val paragraphs = doc.select("h6")
                val substArray = ArrayList<Substitution>()

                val coursePreference = prefs.getNonNullString("courses")
                val classPreference = prefs.getNonNullString("classes")

                var informational = ""
                for (i in 0 until paragraphs.size) {
                    if (i == 0) {
                        informational = paragraphs[i].html().replace("<br>", "\n")
                    } else {
                        informational += "\n\n" + paragraphs[i].html().replace("<br>", "\n")
                    }
                }
                prefs.putAndApplyString("informational", informational)

                deleteAllSubst()

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
                            priority = SubstUtil.assignRanking(group, (date.length > 2 && date.substring(0, 3) == "psa")), // TODO make fancier pls
                            date_priority = SubstUtil.assignDatePriority(date),
                            website_priority = i
                    )
                    if (caller == Caller.JOBSERVICE && prefs.getBoolean("notif", true)) {
                        if (subst.checkIfPersonal(classPreference, coursePreference)) {
                            substArray.add(subst)
                        }
                    }
                    insert(subst)
                }
                var countOfNotificationItems = 0
                var countOfMoreNotificationItems = 0
                if (caller == Caller.JOBSERVICE && prefs.getBoolean("notif", true)) {
                    substArray.forEach { substItem ->
                        if (countOfNotificationItems < 4) {
                            notificationText += ("${
                            if (notificationText.isNotEmpty()) ",\n" else ""
                            }${substItem.course}: ${
                            if (substItem.additional.isNotEmpty()) substItem.additional else if (substItem.type.isNotEmpty()) substItem.type else "---"
                            }")
                            countOfNotificationItems += 1
                        } else {
                            countOfMoreNotificationItems += 1
                        }
                    }
                    if (countOfMoreNotificationItems > 0) {
                        notificationText += application.resources.getQuantityString(R.plurals.notification_more_messages, countOfMoreNotificationItems, countOfMoreNotificationItems)
                    }
                }
                prefs.putAndApplyString("timeNew", currentTime)
                hasUpdated = true
            }
            return Pair(notificationText, hasUpdated)
        }
        fun requestFoodMenuData(url: String): Boolean {
            val docFood = Jsoup.connect(url).get()
            val currentFoodTime = docFood.select("h1")[0].text()
            return if (currentFoodTime != prefs.getString("newFoodTime", "")) {
                val foodElements = docFood.select("th")
                deleteAllFoodItems()

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
                    insert(Food(s, l))
                }
                prefs.putAndApplyString("newFoodTime", currentFoodTime)
                true
            } else {
                false
            }
        }
        fun sendNotification(notificationText: String) {
            val openApp = Intent(application, Main::class.java)
            openApp.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            openApp.flags += Intent.FLAG_ACTIVITY_CLEAR_TASK
            val openAppPending = PendingIntent.getActivity(application, 0, openApp, 0)

            val notificationLayout = RemoteViews(application.packageName, R.layout.notification)
            notificationLayout.setTextViewText(R.id.notification_title, application.getString(
                    R.string.substitution_plan
            ))
            notificationLayout.setTextViewText(R.id.notification_textview, notificationText)

            val manager = application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                SubstUtil.getNotificationChannel(application, prefs)
            }

            val notification = NotificationCompat.Builder(application, SubstUtil.notificationChannelId)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setContentIntent(openAppPending)
                    .setAutoCancel(true)
                    .setColor(ContextCompat.getColor(application, R.color.colorAccent))
                    .setSmallIcon(
                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) R.drawable.ic_avh else R.drawable.ic_avhlogo
                    )

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                val sound = if (prefs.getNonNullString("ringtoneUri").isNotEmpty()) {
                    Uri.parse(prefs.getNonNullString("ringtoneUri"))
                } else {
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
                }
                notification.setSound(sound)
            }

            manager.notify(1, notification.build())
        }

        val urlPair = when {
            prefs.getBoolean("testUrls", false) -> Pair(
                    "https://djd4rkn355.github.io/subst_test.html",
                    "https://djd4rkn355.github.io/food_test.html"
            )
            prefs.getNonNullString("custom_test_url").isNotEmpty() -> Pair(
                    "https://djd4rkn355.github.io/${prefs.getString("custom_test_url", "")}",
                    "https://djd4rkn355.github.io/food.html"
            )
            else -> Pair(
                    "https://djd4rkn355.github.io/avh_substitutions.html",
                    "https://djd4rkn355.github.io/food.html"
            )
        }

        var error = false
        val returnText = try {
            val foodMenuHasUpdated = requestFoodMenuData(urlPair.second)
            val planResult = requestSubstPlan(urlPair.first)

            val notificationText = planResult.first
            val planHasUpdated = planResult.second

            if (caller == Caller.JOBSERVICE && notificationText.isNotEmpty()) {
                sendNotification(notificationText)
            }
            when (caller) {
                Caller.SUBSTITUTION -> {
                    if (planHasUpdated) {
                        application.getString(R.string.plan_updated)
                    } else {
                        "${application.getString(R.string.last_updated)} ${prefs.getNonNullString("timeNew")}"
                    }
                }
                Caller.FOODMENU -> {
                    if (foodMenuHasUpdated) {
                        application.getString(R.string.food_menu_updated)
                    } else {
                        "${application.getString(R.string.last_updated)} ${prefs.getNonNullString("newFoodTime")}"
                    }
                }
                Caller.SETTINGS -> application.getString(R.string.force_refresh_successful)
                else -> ""
            }
        } catch (e: Exception) {
            error = true
            prefs.putAndApplyString("debug_recent_exception", Log.getStackTraceString(e))
            application.getString(R.string.an_error_occurred)
        }
        return Pair(returnText, error)
    }

    fun fetchDataOnline(caller: Caller): Pair<String, Boolean> {
        return getData(caller)
    }
}