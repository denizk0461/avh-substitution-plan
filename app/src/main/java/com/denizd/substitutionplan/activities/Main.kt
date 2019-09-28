package com.denizd.substitutionplan.activities

import android.annotation.SuppressLint
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.denizd.substitutionplan.data.HelperFunctions
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.fragments.FoodFragment
import com.denizd.substitutionplan.fragments.GeneralPlanFragment
import com.denizd.substitutionplan.fragments.PersonalPlanFragment
import com.denizd.substitutionplan.fragments.SettingsFragment
import com.denizd.substitutionplan.services.FBPingService
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import java.lang.IllegalArgumentException
import java.util.*

/**
 * The main class of the app. This controls navigation and enables the user to navigate through the
 * app's fragments and view the information provided on the substitution table. Theming as well as
 * setting up Firebase Cloud Messaging and Crashlytics are handled here. Also checks whether
 * the user has completed the setup (logging in, setting preferences)
 */
internal class Main : AppCompatActivity(R.layout.app_bar_main) {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val edit = prefs.edit()
        val firstTime = Intent(context, FirstTime::class.java)
        val login = Intent(context, Login::class.java)
        Crashlytics.setUserIdentifier(prefs.getString("username", "") ?: "")

        /**
         * This cascade of if-else's allows the user to return to any point of the setup if they
         * decide to postpone it, e.g. logging in but not setting up their preferences. Furthermore,
         * it prevents existing users from upgrading from a previous version of the app and skip the
         * mandatory login
         */
        if (!prefs.getBoolean("successful_login", false)) {
            startActivity(login)
            finish()
        } else if (prefs.getBoolean("firstTime", true)) {
            startActivity(firstTime)
            finish()
        } else {

            if (!prefs.getBoolean("colourTransferred", false)) {
                HelperFunctions.transferOldColourIntsToString(prefs)
                edit.putBoolean("colourTransferred", true).apply()
            }

            edit.putInt("launchDev", prefs.getInt("launchDev", 0) + 1)
            edit.apply()

            FirebaseApp.initializeApp(this)
            pingFirebaseTopics()

            val appBarLayout = findViewById<AppBarLayout>(R.id.appbarlayout)
            val toolbarTxt = findViewById<TextView>(R.id.toolbarTxt)
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
            val contextView = findViewById<View>(R.id.coordination)
            val window = this.window

            setTheme(R.style.AppTheme0)

            val barColour = when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> ContextCompat.getColor(context, R.color.legacyBlack)
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> ContextCompat.getColor(context, R.color.colorBackground)
                else -> 0
            }
            if (barColour != 0) {
                window.navigationBarColor = barColour
                window.statusBarColor = barColour
            }

            when (prefs.getInt("themeInt", 0)) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    if (Build.VERSION.SDK_INT in 23..28) {
                        @SuppressLint("InlinedApi")
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }

            if (!prefs.getBoolean("autoRefresh", false) && prefs.getInt("firstTimeOpening", 0) != 0) {
                try {
                    Snackbar.make(contextView, "${getString(R.string.last_updated)} ${prefs.getString("timeNew", "")}", Snackbar.LENGTH_LONG).show()
                } catch (e: IllegalArgumentException) {}
            }

            val textViewGreeting = findViewById<TextView>(R.id.text_greeting)
            textViewGreeting.text = if (prefs.getBoolean("greeting", true) && (prefs.getString("username", "") ?: "").isNotEmpty()) {
                getGreetingString()
            } else {
                ""
            }

            if (prefs.getInt("firstTimeOpening", 0) == 0) {
                edit.putInt("firstTimeOpening", prefs.getInt("firstTimeOpening", 0) + 1).apply()
            }

            /// Launch the user-specified fragment (general or personal plan)
            val defaultFragment = if (prefs.getBoolean("defaultPersonalised", false)) {
                bottomNav.selectedItemId = R.id.personal
                toolbarTxt.text = personalPlanTitle()
                PersonalPlanFragment()
            } else {
                bottomNav.selectedItemId = R.id.plan
                toolbarTxt.text = getString(R.string.app_name)
                GeneralPlanFragment()
            }
            loadFragment(defaultFragment)

            bottomNav.setOnNavigationItemSelectedListener { item: MenuItem ->
                var fragmentLoading = true
                lateinit var fragment: Fragment
                when (item.itemId) {
                    R.id.plan -> {
                        fragment = GeneralPlanFragment()
                        toolbarTxt.text = getString(R.string.app_name)
                    }
                    R.id.personal -> {
                        fragment = PersonalPlanFragment()
                        toolbarTxt.text = personalPlanTitle()
                    }
                    R.id.menu -> {
                        fragment = FoodFragment()
                        toolbarTxt.text = getString(R.string.food_menu)
                    }
                    R.id.openinfopanel -> {
                        openInfoDialog()
                        fragmentLoading = false
                    }
                    R.id.settings -> {
                        fragment = SettingsFragment()
                        toolbarTxt.text = getString(R.string.settings)
                    }
                }
                if (fragmentLoading) {
                    appBarLayout.setExpanded(true)
                    loadFragment(fragment)
                } else {
                    false
                }
            }

            bottomNav.setOnNavigationItemReselectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.plan, R.id.personal -> {
                        try {
                            val recyclerView = findViewById<RecyclerView>(R.id.linearRecycler)
                            recyclerView.post {
                                recyclerView.smoothScrollToPosition(0)
                            }
                            appBarLayout.setExpanded(true)
                        } catch (e: NullPointerException) {}
                    }
                    R.id.menu -> {
                        try {
                            val recyclerView = findViewById<RecyclerView>(R.id.linear_food)
                            recyclerView.post {
                                recyclerView.smoothScrollToPosition(0)
                            }
                            appBarLayout.setExpanded(true)
                        } catch (e: NullPointerException) {}
                    }
                    R.id.settings -> {
                        try {
                            val nsv = findViewById<NestedScrollView>(R.id.nsvsettings)
                            nsv.post {
                                nsv.smoothScrollTo(0, 0)
                            }
                            appBarLayout.setExpanded(true)
                        } catch (e: NullPointerException) {}
                    }
                }
            }
        }
    }

    /**
     * Picks a greeting according to the time of day to present to the user if they choose to
     * enable greetings
     *
     * @return the greeting
     */
    private fun getGreetingString(): String {
        val gen = Random()
        return String.format(when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..10 -> resources.getStringArray(R.array.greetings_morning)[gen.nextInt(resources.getStringArray(
                R.array.greetings_morning
            ).size)]
            in 11..17 -> resources.getStringArray(R.array.greetings_noon)[gen.nextInt(resources.getStringArray(
                R.array.greetings_noon
            ).size)]
            else -> resources.getStringArray(R.array.greetings_evening)[gen.nextInt(resources.getStringArray(
                R.array.greetings_evening
            ).size)]
        }, prefs.getString("username", ""))
    }

    private fun openInfoDialog() {
        val dialog = AlertDialog.Builder(context)
        val dialogView = View.inflate(context, R.layout.simple_dialog, null)
        dialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(
            R.string.information
        )
        val dialogText = "${getString(R.string.last_updated)} ${prefs.getString("timeNew", "")}.\n\n${prefs.getString("informational", "")}".trim()
        dialogView.findViewById<TextView>(R.id.dialogtext).text = dialogText
        dialog.setView(dialogView).show()
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        return true
    }

    /**
     * Triggers FBPingService.kt to re-subscribe to the selected Firebase topics every 15 minutes
     */
    private fun pingFirebaseTopics() {
        val componentName = ComponentName(this, FBPingService::class.java)
        val info = JobInfo.Builder(42, componentName)
                .setRequiresCharging(false)
                .setPersisted(true)
                .setPeriodic(900000)
                .build()
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.schedule(info)
    }

    /**
     * Decides on the title for PersonalPlanFragment.kt. Picks a generic "Your plan" if greetings
     * are enabled or "$user's Plan" if they are disabled
     *
     * @return the title
     */
    private fun personalPlanTitle(): String {
        val user = prefs.getString("username", "") ?: ""
        return if (prefs.getBoolean("greeting", true) || user.isEmpty()) {
            getString(R.string.yourPlan)
        } else {
            if (user.endsWith("s", true) || user.endsWith("x", true) || user.endsWith("z", true)) {
                "$user${getString(R.string.no_s_plan)}"
            } else {
                "$user${getString(R.string.s_plan)}"
            }
        }
    }
}