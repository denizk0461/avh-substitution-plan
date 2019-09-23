package com.denizd.substitutionplan.data

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Environment
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.FragmentActivity
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.models.Substitution
import java.util.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import javax.xml.parsers.DocumentBuilderFactory

/**
 * An object class used for declaring functions and variables used by multiple classes throughout
 * the project. As it is an object class, it can be accessed statically without initialisation
 */
internal object HelperFunctions {

    /**
     * A language-independent list of all courses that is used to save the custom colours of any
     * course in SharedPreferences without depending on the user's device language
     */
    val languageIndependentCourses = arrayOf("German", "English", "French", "Spanish", "Latin", "Turkish", "Chinese", "Arts", "Music",
            "Theatre", "Geography", "History", "Politics", "Philosophy", "Religion", "Maths", "Biology", "Chemistry",
            "Physics", "CompSci", "PhysEd", "GLL", "WAT", "Forder", "WP")

    val colourNames = arrayOf("default", "red", "orange", "yellow", "green", "teal", "cyan", "blue", "purple", "pink",
            "brown", "grey", "pureWhite", "salmon", "tangerine", "banana", "flora", "spindrift", "sky", "orchid",
            "lavender", "carnation", "brown2", "pureBlack")

    /**
     * A list of all phrases used to describe that a course has been cancelled. Expand this
     * if necessary. No further code changes required if this is expanded
     */
    val cancellations = arrayOf("eigenverantwortliches arbeiten", "entfall", "entfällt", "fällt aus", "freisetzung", "vtr. ohne lehrer")

    private val colourIntegers = intArrayOf(
        0,
        R.color.bgRed,
        R.color.bgOrange,
        R.color.bgYellow,
        R.color.bgGreen,
        R.color.bgTeal,
        R.color.bgCyan,
        R.color.bgBlue,
        R.color.bgPurple,
        R.color.bgPink,
        R.color.bgBrown,
        R.color.bgGrey,
        R.color.bgPureWhite,
        R.color.bgSalmon,
        R.color.bgTangerine,
        R.color.bgBanana,
        R.color.bgFlora,
        R.color.bgSpindrift,
        R.color.bgSky,
        R.color.bgOrchid,
        R.color.bgLavender,
        R.color.bgCarnation,
        R.color.bgBrown2,
        R.color.bgPureBlack
    )
    const val notificationChannelId = "general"

    fun getColourForString(name: String): Int {
        return when (name) {
            "red" -> R.color.bgRed
            "orange" -> R.color.bgOrange
            "yellow" -> R.color.bgYellow
            "green" -> R.color.bgGreen
            "teal" -> R.color.bgTeal
            "cyan" -> R.color.bgCyan
            "blue" -> R.color.bgBlue
            "purple" -> R.color.bgPurple
            "pink" -> R.color.bgPink
            "brown" -> R.color.bgBrown
            "grey" -> R.color.bgGrey
            "pureWhite" -> R.color.bgPureWhite
            "salmon" -> R.color.bgSalmon
            "tangerine" -> R.color.bgTangerine
            "banana" -> R.color.bgBanana
            "flora" -> R.color.bgFlora
            "spindrift" -> R.color.bgSpindrift
            "sky" -> R.color.bgSky
            "orchid" -> R.color.bgOrchid
            "lavender" -> R.color.bgLavender
            "carnation" -> R.color.bgCarnation
            "brown2" -> R.color.bgBrown2
            "pureBlack" -> R.color.bgPureBlack
            else -> R.color.colorBackgroundLight
        }
    }

    fun getIconForCourse(course: String): Int {
        return with (course.toLowerCase(Locale.ROOT)) {
            when {
                contains("deu") || contains("dep") || contains("daz") -> R.drawable.ic_german
                contains("mat") || contains("map") -> R.drawable.ic_maths
                contains("eng") || contains("enp") || contains("ena") -> R.drawable.ic_english
                contains("spo") || contains("spp") || contains("spth") -> R.drawable.ic_pe
                contains("pol") || contains("pop") -> R.drawable.ic_politics
                contains("dar") || contains("dap") -> R.drawable.ic_drama
                contains("phy") || contains("php") -> R.drawable.ic_physics
                contains("bio") || contains("bip") || contains("nw") -> R.drawable.ic_biology
                contains("che") || contains("chp") -> R.drawable.ic_chemistry
                contains("phi") || contains("psp") -> R.drawable.ic_philosophy
                contains("laa") || contains("laf") || contains("lat") -> R.drawable.ic_latin
                contains("spa") || contains("spf") -> R.drawable.ic_spanish
                contains("fra") || contains("frf") || contains("frz") -> R.drawable.ic_french
                contains("inf") -> R.drawable.ic_compsci
                contains("ges") -> R.drawable.ic_history
                contains("rel") -> R.drawable.ic_religion
                contains("geg") || contains("wuk") -> R.drawable.ic_geography
                contains("kun") -> R.drawable.ic_arts
                contains("mus") -> R.drawable.ic_music
                contains("tue") -> R.drawable.ic_turkish
                contains("chi") -> R.drawable.ic_chinese
                contains("gll") -> R.drawable.ic_gll
                contains("wat") -> R.drawable.ic_wat
                contains("för") -> R.drawable.ic_help
                contains("wp") || contains("met") -> R.drawable.ic_pencil
                else -> R.drawable.ic_empty
            }
        }
    }

    /**
     * This function returns a string that is used to get the colour set for a specific course
     * from Shared Preferences
     *
     * @param course    the course abbreviation as a string
     *
     * @return the string used to store the course's colour in Shared Preferences
     */
    fun getColourString(course: String): String {
        var colorCheck: String
        return try {
            colorCheck = course.toLowerCase(Locale.ROOT).substring(0, 3)
            when (colorCheck) {
                "deu", "dep", "daz", "fda" -> "German"
                "mat", "map" -> "Maths"
                "eng", "enp", "ena" -> "English"
                "spo", "spp", "spth" -> "PhysEd"
                "pol", "pop" -> "Politics"
                "dar", "dap" -> "Theatre"
                "phy", "php" -> "Physics"
                "bio", "bip", "nw1", "nw2", "nw3", "nw4" -> "Biology"
                "che", "chp" -> "Chemistry"
                "phi", "psp" -> "Philosophy"
                "laa", "laf", "lat" -> "Latin"
                "spa", "spf" -> "Spanish"
                "fra", "frf", "frz" -> "French"
                "inf" -> "CompSci"
                "ges" -> "History"
                "rel" -> "Religion"
                "geg" -> "Geography"
                "kun" -> "Arts"
                "mus" -> "Music"
                "tue" -> "Turkish"
                "chi" -> "Chinese"
                "gll" -> "GLL"
                "wat" -> "WAT"
                "för" -> "Forder"
                "met", "wpb" -> "WP"
                else -> ""
            }
        } catch (e: StringIndexOutOfBoundsException) {
            try {
                colorCheck = course.toLowerCase(Locale.ROOT).substring(0, 2)
                when (colorCheck) {
                    "nw" -> "Biology"
                    "wp" -> "WP"
                    else -> ""
                }
            } catch (e2: StringIndexOutOfBoundsException) {
                ""
            }
        }
    }

    /**
     * This function served as a way to transfer a deprecated method of storing user-defined
     * course colours to a new method. It is not required otherwise, but still remains in
     * the code, as users from older versions (2.1.3 and below) may wish to upgrade without
     * losing their settings
     *
     * @param prefs     a reference to the app's SharedPreferences
     */
    fun transferOldColourIntsToString(prefs: SharedPreferences) {
        var colour = ""
        val edit = prefs.edit()
        for (course in languageIndependentCourses) {
            for (i in colourIntegers.indices) {
                if (prefs.getInt("bg$course", 0) == colourIntegers[i]) {
                    colour = colourNames[i]
                    break
                }
                colour = ""
            }
            edit.putString("card$course", colour)
        }
        edit.apply()
    }

    /**
     * This function serves for checking whether a course on the substitution plan is relevant to
     * the user and should be shown on their personal plan as well as in their notifications
     *
     * @param substitution             the substitution item
     * @param coursePreference  a string that represents the courses the user is enrolled in
     * @param classPreference   a string that represents the group the user is in
     * @param psa               decide whether or not any PSA items should be included in the filter
     *
     * @return true if the substitution is relevant to the user, false otherwise
     */
    fun checkPersonalSubstitutions(substitution: Substitution, coursePreference: String, classPreference: String, psa: Boolean): Boolean {
        val group = substitution.group
        val course = substitution.course
        if (psa && substitution.date.isNotEmpty() && substitution.date.substring(0, 3) == "psa") {
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
                if ((classPreference.contains(group) || group.contains(classPreference)) && coursePreference.contains(course)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * This function is used throughout some classes to set the theme according to the device's
     * Android version before an app theme may be picked in the settings. As Main.kt as well as
     * SettingsFragment.kt are only accessible after the inital setup and serve slightly
     * different purposes, they use their own implementations
     */
    fun setTheme(window: Window, context: Context) {
        val barColour = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> ContextCompat.getColor(context, R.color.legacyBlack)
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> ContextCompat.getColor(context, R.color.colorBackground)
            else -> 0
        }
        if (barColour != 0) {
            window.navigationBarColor = barColour
            window.statusBarColor = barColour
        }
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_NO) {
            if (Build.VERSION.SDK_INT in 23..28) {
                @SuppressLint("InlinedApi")
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            }
        }
    }

    /**
     * Returns the notification channel that is reponsible for handling substitution notifications
     * on Android Oreo and above. If it does not exist, this function creates it
     *
     * @param context               the context used for gathering the notification system service
     * @param prefs                 a reference to the app's SharedPreferences
     * @return the notification channel
     */
    @TargetApi(26)
    fun getNotificationChannel(context: Context, prefs: SharedPreferences): NotificationChannel {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (!prefs.getBoolean("notifChannelCreated", false)) {
            val channel = NotificationChannel(
                notificationChannelId, context.getString(
                    R.string.general
                ), NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableLights(true)
            channel.lightColor = Color.BLUE
            manager.createNotificationChannel(channel)
            prefs.edit().putBoolean("notifChannelCreated", true).apply()
            channel
        } else {
            manager.getNotificationChannel(notificationChannelId)
        }
    }

    fun checkStringForArray(s: String, checking: Array<String>): Boolean {
        checking.forEach { check ->
            if (s.contains(check)) return true
        }
        return false
    }

    /**
     * The code below is used for saving and restoring user settings. However, it is currently
     * inaccessible to the end user without entering a code in the hidden settings menu,
     * as no interface has been created for it and it is not built around Android's Scoped Storage
     * (which deprecates java.io.File and is likely to be required for file access on Android 11
     * and above). The functions can still be accessed by entering "_READ" and "_WRITE"
     * respectively in the hidden settings menu
     */
    private val prefKeys = arrayOf("username", "classes", "courses", "greeting", "themeInt", "notif",
        "defaultPersonalised", "autoRefresh", "firstTimeDev", "launchDev", "pingFB",
        "subscribedToFBDebugChannel", "subscribedToiOSChannel")
    private val prefTypes = arrayOf("string", "string", "string", "bool", "int", "bool", "bool", "bool",
        "string", "int", "int", "bool", "bool")

    private fun getPrefValue(prefs: SharedPreferences, type: String, key: String): Any {
        return when (type) {
            "string" -> prefs.getString(key, "")
            "int" -> prefs.getInt(key, 0)
            "bool" -> prefs.getBoolean(key, false)
            else -> ""
        }
    }

    private fun setPrefValue(prefs: SharedPreferences, key: String, value: String, type: String) {
        when (type) {
            "int" -> prefs.edit().putInt(key, value.toInt()).apply()
            "string" -> prefs.edit().putString(key, value).apply()
            "bool" -> prefs.edit().putBoolean(key, value.toBoolean()).apply()
        }
    }

    fun writePrefsToXml(prefs: SharedPreferences, context: Context, activity: FragmentActivity) {
        if (checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val localPrefKeys = ArrayList<String>()
            val localPrefTypes = ArrayList<String>()
            for (i in prefKeys.indices) {
                localPrefKeys.add(prefKeys[i])
                localPrefTypes.add(prefTypes[i])
            }
            for (i in languageIndependentCourses.indices) {
                localPrefKeys.add("card${languageIndependentCourses[i]}")
                localPrefTypes.add("string")
            }
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "avh_plan_data.xml")
            val out = OutputStreamWriter(FileOutputStream(file, false))

            var input = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n<items>"
            for (i in localPrefKeys.indices) {
                input += "\n\t<key>${localPrefKeys[i]}</key>" +
                        "\n\t<value>${getPrefValue(prefs, localPrefTypes[i], localPrefKeys[i])}</value>"
            }
            input += "\n</items>"
            out.write(input)
            out.flush()
            out.close()
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, context.getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
            requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 42)
        }
    }

    fun readPrefsFromXml(prefs: SharedPreferences, context: Context, activity: FragmentActivity) {
        if (checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            val localPrefTypes = ArrayList<String>()
            for (i in prefKeys.indices) {
                localPrefTypes.add(prefTypes[i])
            }
            for (i in languageIndependentCourses.indices) {
                localPrefTypes.add("string")
            }
            val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "avh_plan_data.xml")
            val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            val document = documentBuilder.parse(file)

            for (i in localPrefTypes.indices) {
                val key = document.getElementsByTagName("key").item(i).textContent
                val value = document.getElementsByTagName("value").item(i).textContent
                setPrefValue(prefs, key, value, localPrefTypes[i])
            }
            Toast.makeText(context, context.getString(R.string.success), Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, context.getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
            requestPermissions(activity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 42)
        }
    }
}
