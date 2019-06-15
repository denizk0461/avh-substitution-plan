package com.denizd.substitutionplan

import android.app.ActivityManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.jaredrummler.android.device.DeviceName
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

class Main : AppCompatActivity(R.layout.activity_main) {

    private var bottomSheetExpanded = false
    private lateinit var bottomSheetBehaviour: BottomSheetBehavior<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this as Context
        val prefs = PreferenceManager.getDefaultSharedPreferences(context) as SharedPreferences
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
            val bottomSheetRoot = findViewById<LinearLayout>(R.id.bottom_sheet)
            bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetRoot)
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
            val fragmentContainer = findViewById<CoordinatorLayout>(R.id.fragment_container)
            val bottomSheetCloser = findViewById<LinearLayout>(R.id.bottom_sheet_closer)
            val chip = findViewById<Chip>(R.id.chip)
            val contextView = findViewById<View>(R.id.coordination)
            val window = this.window as Window

            bottomSheetExpanded = bottomSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED

            setTheme(R.style.AppTheme0)

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

            val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
            val taskDesc = ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.background))
            setTaskDescription(taskDesc)

            if (!prefs.getBoolean("showinfotab", true)) {
                bottomNav.menu.removeItem(R.id.openinfopanel)
            }

            fragmentContainer.setOnClickListener {
                bottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED)
            }

            bottomSheetBehaviour.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetCloser.visibility = View.VISIBLE
                    } else if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        bottomSheetCloser.visibility = View.GONE
                    }
                }
                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })

            bottomSheetCloser.setOnClickListener {
                bottomSheetCloser.visibility = View.GONE
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            }

            if (!prefs.getBoolean("autoRefresh", false) && prefs.getInt("firstTimeOpening", 0) != 0) {
                try {
                    val oldFormat = "EEE, dd MMM yyyy HH:mm:ss zzz"
                    val newFormat = "yyyy-MM-dd, HH:mm:ss"

                    val d = Date(prefs.getString("time", ""))
                    val sdf = SimpleDateFormat(oldFormat)
                    sdf.applyPattern(newFormat)
                    val newDate = sdf.format(d)

                    val sb = StringBuilder()
                    val updated = sb.append(getText(R.string.lastupdatedK)).append(newDate).toString()

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
                if (prefs.getString("username", "").isNotEmpty()) {
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

                handler.postDelayed({
                    bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }, 600)

                handler.postDelayed({
                    bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                }, 2000)

                edit.putInt("firstTimeOpening", prefs.getInt("firstTimeOpening", 0) + 1).apply()
            }

            if (prefs.getBoolean("openInfo", false)) {
                handler.postDelayed({
                    bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                }, 600)
            }

            val userPlan: String = if (prefs.getString("username", "").toString().isNotEmpty()) {
                when (prefs.getString("username", "")[prefs.getString("username", "").length - 1].toString().toLowerCase()) {
                    "s", "x", "z" -> prefs.getString("username", "") + getString(R.string.nosplan)
                    else -> prefs.getString("username", "") + getString(R.string.splan)
                }
            } else {
                getString(R.string.personalplan)
            }
            lateinit var fragment: Fragment
            if (prefs.getBoolean("defaultPersonalised", false)) {
                fragment = mPlanFragment(true)
                bottomNav.selectedItemId = R.id.personal
                toolbarTxt.text = userPlan
            } else {
                fragment = mPlanFragment(false)
                bottomNav.selectedItemId = R.id.plan
                toolbarTxt.text = getString(R.string.app_name)
            }
            loadFragment(fragment)

            bottomNav.setOnNavigationItemSelectedListener { item: MenuItem ->
                var fragmentLoading = true
                lateinit var fragment: Fragment
                when (item.itemId) {
                    R.id.plan -> {
                        fragment = mPlanFragment(false)
                        toolbarTxt.text = getString(R.string.app_name)
                    }
                    R.id.personal -> {
                        fragment = mPlanFragment(true)
                        toolbarTxt.text = userPlan
                    }
                    R.id.menu -> {
                        fragment = FoodFragment()
                        toolbarTxt.text = getString(R.string.foodmenu)
                    }
                    R.id.openinfopanel -> {
                        if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_COLLAPSED) {
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        } else if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                        fragmentLoading = false
                    }
                    R.id.settings -> {
                        fragment = SettingsFragment()
                        toolbarTxt.text = getString(R.string.settings)
                    }
                }
                if (fragmentLoading) {
                    bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
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
                                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                        } catch (e: NullPointerException) {}
                    }
                    R.id.menu -> {
                        try {
                            val recyclerView = findViewById<RecyclerView>(R.id.linear_food)
                            recyclerView.post {
                                recyclerView.smoothScrollToPosition(0)
                                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                        } catch (e: NullPointerException) {}
                    }
                    R.id.settings -> {
                        try {
                            val nsv = findViewById<NestedScrollView>(R.id.nsvsettings)
                            nsv.post {
                                nsv.smoothScrollTo(0, 0)
                                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                        } catch (e: NullPointerException) {}
                    }
                }
                appbarlayout.setExpanded(true)
            }
        }
    }

    private fun mPlanFragment(ispersonal: Boolean): PlanFragment {
        val f = PlanFragment()
        val bdl = Bundle(1)
        bdl.putBoolean("ispersonal", ispersonal)
        f.arguments = bdl
        return f
    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
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

    override fun onBackPressed() {
        if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            super.onBackPressed()
        }
    }
}