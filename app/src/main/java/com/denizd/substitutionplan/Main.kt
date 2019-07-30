package com.denizd.substitutionplan

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.TextView
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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.android.device.DeviceName
import java.lang.IllegalArgumentException
import java.util.*

class Main : AppCompatActivity(R.layout.app_bar_main) {

    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<*>
    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        prefs = PreferenceManager.getDefaultSharedPreferences(context) as SharedPreferences
        val edit = prefs.edit()
        val firstTime = Intent(context, FirstTime::class.java)

        if (prefs.getBoolean("firstTime", true)) {
            startActivity(firstTime)
            finish()
        } else {
            edit.putInt("launchDev", prefs.getInt("launchDev", 0) + 1)
            edit.apply()

            notificationJob()

            val handler = Handler()

            val appbarlayout = findViewById<AppBarLayout>(R.id.appbarlayout)
            val toolbarTxt = findViewById<TextView>(R.id.toolbarTxt)
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
            val chip = findViewById<Chip>(R.id.chip)
            val contextView = findViewById<View>(R.id.coordination)
            val window = this.window as Window

            setTheme(R.style.AppTheme0)

            if (prefs.getBoolean("openInfo", false)) {
                openInfoDialog()
            }

            when (prefs.getInt("themeInt", 0)) {
                0 -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                        window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
                    }
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
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
                    val updated = sb.append(getText(R.string.lastupdatedK)).append(prefs.getString("time", "")).toString()

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
                        title.text = getString(R.string.chinaTitle)
                        val dialogText = dialogView.findViewById<TextView>(R.id.dialogtext)
                        dialogText.text = getString(R.string.chinaDialog)
                        alertDialog.setView(dialogView).show()

                        edit.putBoolean("huaweiDeviceDialog", false).apply()
                    }
                }
            }

            if (prefs.getBoolean("greeting", false)) {
                if ((prefs.getString("username", "") ?: "").isNotEmpty()) {
                    val animationIn = AnimationUtils.loadAnimation(context, R.anim.chip_slide_in)
                    val animationOut = AnimationUtils.loadAnimation(context, R.anim.chip_slide_out)
                    val generator = Random()
                    val greetings = resources.getStringArray(R.array.greeting8_array)
                    val rightNow = Calendar.getInstance()
                    val currentHour = rightNow.get(Calendar.HOUR_OF_DAY)

                    chip.text = when (generator.nextInt(9)) {
                        0 -> getString(R.string.greeting0, prefs.getString("username", ""))
                        1 -> getString(R.string.greeting1, prefs.getString("username", ""))
                        2 -> getString(R.string.greeting2, prefs.getString("username", ""))
                        3 -> getString(R.string.greeting3, prefs.getString("username", ""))
                        4 -> getString(R.string.greeting4, prefs.getString("username", ""))
                        5 -> getString(R.string.greeting5, prefs.getString("username", ""))
                        6 -> getString(R.string.greeting6, prefs.getString("username", ""))
                        7 -> getString(R.string.greeting7, prefs.getString("username", ""))
                        8 -> {
                            when {
                                currentHour < 11 -> greetings[0] + prefs.getString("username", "") + "."
                                currentHour in 11..17 -> greetings[1] + prefs.getString("username", "") + "."
                                else -> greetings[1] + prefs.getString("username", "") + "."
                            }
                        }
                        else -> ";)" // getString(R.string.error)
                    }

                    handler.postDelayed({
                        chip.visibility = View.VISIBLE
                        chip.startAnimation(animationIn)
                    }, 500)

                    handler.postDelayed({
                        chip.startAnimation(animationOut)
                    }, 3000)

                    animationOut.setAnimationListener(object: Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationRepeat(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            chip.visibility = View.GONE
                        }
                    })
                }
            }

            if (prefs.getInt("firstTimeOpening", 0) == 0) {
                edit.putInt("firstTimeOpening", prefs.getInt("firstTimeOpening", 0) + 1).apply()
            }

            val userPlan: String = if ((prefs.getString("username", "") ?: "").toString().isNotEmpty()) {
                when ((prefs.getString("username", "") ?: "")[(prefs.getString("username", "") ?: "").length - 1].toString().toLowerCase()) {
                    "s", "x", "z" -> (prefs.getString("username", "") ?: "") + getString(R.string.nosplan)
                    else -> (prefs.getString("username", "") ?: "") + getString(R.string.splan)
                }
            } else {
                getString(R.string.personalplan)
            }
            lateinit var defaultFragment: Fragment
            if (prefs.getBoolean("defaultPersonalised", false)) {
                defaultFragment = PersonalPlanFragment()
                bottomNav.selectedItemId = R.id.personal
                toolbarTxt.text = userPlan
            } else {
                defaultFragment = GeneralPlanFragment()
                bottomNav.selectedItemId = R.id.plan
                toolbarTxt.text = getString(R.string.app_name)
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
                        toolbarTxt.text = userPlan
                    }
                    R.id.menu -> {
                        fragment = FoodFragment()
                        toolbarTxt.text = getString(R.string.foodmenu)
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

    private fun openInfoDialog() {
        val dialog = AlertDialog.Builder(context)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.simple_dialog, null)
        dialogView.findViewById<TextView>(R.id.textviewtitle).text = getString(R.string.information)
        dialogView.findViewById<TextView>(R.id.dialogtext).text = prefs.getString("informational", "")
        dialog.setView(dialogView).show()
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        return true
    }

    private fun notificationJob() {
        val componentName = ComponentName(this, NotificationService::class.java)
        val info = JobInfo.Builder(42, componentName)
                .setRequiresCharging(false)
                .setPersisted(true)
                .setPeriodic(900000)
                .build()
        val scheduler = getSystemService(JOB_SCHEDULER_SERVICE) as JobScheduler
        scheduler.schedule(info)
    }
}