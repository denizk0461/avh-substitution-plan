package com.denizd.substitutionplan

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.jaredrummler.android.device.DeviceName
import java.lang.IllegalArgumentException
import java.util.*

class Main : AppCompatActivity(R.layout.app_bar_main) {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        prefs = PreferenceManager.getDefaultSharedPreferences(context) as SharedPreferences
        val edit = prefs.edit()
        val firstTime = Intent(context, FirstTime::class.java)

        if (prefs.getBoolean("firstTime", true)) {
//        if (true) {
            startActivity(firstTime)
            finish()
        } else {
            if (!prefs.getBoolean("colourTransferred", false)) {
                MiscData.transferOldColourIntsToString(prefs)
                edit.putBoolean("colourTransferred", true).apply()
            }

            edit.putInt("launchDev", prefs.getInt("launchDev", 0) + 1)
            edit.apply()

            FirebaseApp.initializeApp(context)

            if (prefs.getBoolean("notif", true)) {
                FirebaseMessaging.getInstance().subscribeToTopic("substitutions-android")
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("substitutions-android")
            }
            FirebaseMessaging.getInstance().subscribeToTopic("substitutions-broadcast")

            val appbarlayout = findViewById<AppBarLayout>(R.id.appbarlayout)
            val toolbarTxt = findViewById<TextView>(R.id.toolbarTxt)
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
            val contextView = findViewById<View>(R.id.coordination)
            val window = this.window as Window

            setTheme(R.style.AppTheme0)

            when (prefs.getInt("themeInt", 0)) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackground)
                        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorBackground)
                    } else {
                        window.statusBarColor = ContextCompat.getColor(this, R.color.legacyBlack)
                        window.navigationBarColor = ContextCompat.getColor(this, R.color.legacyBlack)
                    }
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    window.navigationBarColor = ContextCompat.getColor(this, R.color.colorBackground)
                    window.statusBarColor = ContextCompat.getColor(this, R.color.colorBackground)
                }
//                else -> {
//                    when (context.resources.configuration.uiMode) {
//                        Configuration.UI_MODE_NIGHT_YES, Configuration.UI_MODE_NIGHT_UNDEFINED -> {
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
//                                window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
//                            }
//                        }
//                        Configuration.UI_MODE_NIGHT_NO -> {
//                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//                            window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
//                        }
//                    }
//                } // TODO theming based on system settings for Android Q
            }

            if (!prefs.getBoolean("autoRefresh", false) && prefs.getInt("firstTimeOpening", 0) != 0) {
                try {
                    val sb = StringBuilder()
                    val updated = sb.append(getText(R.string.lastUpdated)).append(prefs.getString("time", "")).toString()

                    Snackbar.make(contextView, updated, Snackbar.LENGTH_LONG).show()
                } catch (e: IllegalArgumentException) {}
            }

            if (prefs.getBoolean("huaweiDeviceDialog", true)) {
                DeviceName.with(context).request { info, _ ->
                    if (info.manufacturer.contains("Huawei") ||
                            info.manufacturer.contains("Honor") ||
                            info.manufacturer.contains("Xiaomi")) {
                        val alertDialog = AlertDialog.Builder(context, R.style.AlertDialog)
                        val dialogView = LayoutInflater.from(context).inflate(R.layout.secret_dialog, null)
                        val title = dialogView.findViewById<TextView>(R.id.textviewtitle)
                        title.text = getString(R.string.chineseDevicesTitle)
                        val dialogText = dialogView.findViewById<TextView>(R.id.dialogtext)
                        dialogText.text = getString(R.string.chineseDevicesHelp)
                        alertDialog.setView(dialogView).show()

                        edit.putBoolean("huaweiDeviceDialog", false).apply()
                    }
                }
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
            in 5..10 -> resources.getStringArray(R.array.greetingsMorning)[gen.nextInt(resources.getStringArray(R.array.greetingsMorning).size)]
            in 11..17 -> resources.getStringArray(R.array.greetingsNoon)[gen.nextInt(resources.getStringArray(R.array.greetingsNoon).size)]
            else -> resources.getStringArray(R.array.greetingsEvening)[gen.nextInt(resources.getStringArray(R.array.greetingsEvening).size)]
        }, prefs.getString("username", ""))
    }

    private fun openInfoDialog() {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.simple_dialog, null)
        dialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(R.string.information)
        val sb = StringBuilder()
        val dialogText = sb.append(getText(R.string.lastUpdated)).append((prefs.getString("time", "") ?: "") + ".\n\n").append(prefs.getString("informational", ""))
        dialogView.findViewById<TextView>(R.id.dialogtext).text = dialogText
        dialog.setView(dialogView).show()
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        return true
    }
}