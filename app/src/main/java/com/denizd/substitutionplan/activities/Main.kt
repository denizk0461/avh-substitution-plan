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

internal class Main : AppCompatActivity(R.layout.app_bar_main) {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        prefs = PreferenceManager.getDefaultSharedPreferences(context) as SharedPreferences
        val edit = prefs.edit()
        val firstTime = Intent(context, FirstTime::class.java)
        Crashlytics.setUserIdentifier(prefs.getString("username", "") ?: "")

        if (prefs.getBoolean("firstTime", true)) {
//        if (true) {
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

            val appbarlayout = findViewById<AppBarLayout>(R.id.appbarlayout)
            val toolbarTxt = findViewById<TextView>(R.id.toolbarTxt)
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
            val contextView = findViewById<View>(R.id.coordination)
            val window = this.window as Window

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
                    Snackbar.make(contextView, "${getString(R.string.lastUpdated)} ${prefs.getString("timeNew", "")}", Snackbar.LENGTH_LONG).show()
                } catch (e: IllegalArgumentException) {}
            }

            val textViewGreeting = findViewById<TextView>(R.id.text_greeting)
            if (prefs.getBoolean("greeting", true)) {
                if ((prefs.getString("username", "") ?: "").isNotEmpty()) {
                    textViewGreeting.text = getGreetingString()
                }
            } else {
                textViewGreeting.visibility = View.GONE
            }

            if (prefs.getInt("firstTimeOpening", 0) == 0) {
                edit.putInt("firstTimeOpening", prefs.getInt("firstTimeOpening", 0) + 1).apply()
            }

            lateinit var defaultFragment: Fragment
            if (prefs.getBoolean("defaultPersonalised", false)) {
                defaultFragment = PersonalPlanFragment()
                bottomNav.selectedItemId = R.id.personal
                toolbarTxt.text = getString(R.string.yourPlan)
            } else {
                defaultFragment = GeneralPlanFragment()
                bottomNav.selectedItemId = R.id.plan
                toolbarTxt.text = getString(R.string.appName)
            }
            loadFragment(defaultFragment)

            bottomNav.setOnNavigationItemSelectedListener { item: MenuItem ->
                var fragmentLoading = true
                lateinit var fragment: Fragment
                when (item.itemId) {
                    R.id.plan -> {
                        fragment = GeneralPlanFragment()
                        toolbarTxt.text = getString(R.string.appName)
                    }
                    R.id.personal -> {
                        fragment = PersonalPlanFragment()
                        toolbarTxt.text = getString(R.string.yourPlan)
                    }
                    R.id.menu -> {
                        fragment = FoodFragment()
                        toolbarTxt.text = getString(R.string.foodMenu)
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
                    appbarlayout.setExpanded(true)
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
                            appbarlayout.setExpanded(true)
                        } catch (e: NullPointerException) {}
                    }
                    R.id.menu -> {
                        try {
                            val recyclerView = findViewById<RecyclerView>(R.id.linear_food)
                            recyclerView.post {
                                recyclerView.smoothScrollToPosition(0)
                            }
                            appbarlayout.setExpanded(true)
                        } catch (e: NullPointerException) {}
                    }
                    R.id.settings -> {
                        try {
                            val nsv = findViewById<NestedScrollView>(R.id.nsvsettings)
                            nsv.post {
                                nsv.smoothScrollTo(0, 0)
                            }
                            appbarlayout.setExpanded(true)
                        } catch (e: NullPointerException) {}
                    }
                }
            }
        }
    }

    private fun getGreetingString(): String {
        val gen = Random()

        return String.format(when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 5..10 -> resources.getStringArray(R.array.greetingsMorning)[gen.nextInt(resources.getStringArray(
                R.array.greetingsMorning
            ).size)]
            in 11..17 -> resources.getStringArray(R.array.greetingsNoon)[gen.nextInt(resources.getStringArray(
                R.array.greetingsNoon
            ).size)]
            else -> resources.getStringArray(R.array.greetingsEvening)[gen.nextInt(resources.getStringArray(
                R.array.greetingsEvening
            ).size)]
        }, prefs.getString("username", ""))
    }

    private fun openInfoDialog() {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.simple_dialog, null)
        dialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(
            R.string.information
        )
        val dialogText = "${getString(R.string.lastUpdated)} ${prefs.getString("timeNew", "")}.\n\n${prefs.getString("informational", "")}"
        dialogView.findViewById<TextView>(R.id.dialogtext).text = dialogText
        dialog.setView(dialogView).show()
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        return true
    }

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
}