package com.denizd.substitutionplan

import android.app.ActivityManager
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
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

    var bottomSheetExpanded = false
    lateinit var bottomSheetBehaviour: BottomSheetBehavior<*>

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

            val toolbar = findViewById<Toolbar>(R.id.toolbar)
            val appbarlayout = findViewById<AppBarLayout>(R.id.appbarlayout)
            val toolbarTxt = findViewById<TextView>(R.id.toolbarTxt)
            val bottomSheetRoot = findViewById<LinearLayout>(R.id.bottom_sheet)
            val bottomSheet = findViewById<LinearLayout>(R.id.bottom_sheet_linear)
            val bottomSheetHeader = findViewById<TextView>(R.id.bottom_sheet_header)
            val bottomSheetText = findViewById<TextView>(R.id.bottom_sheet_text)
            val bottomNavDivider = findViewById<View>(R.id.bottom_nav_divider)
            bottomSheetBehaviour = BottomSheetBehavior.from(bottomSheetRoot)
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_nav)
            val fragmentContainer = findViewById<CoordinatorLayout>(R.id.fragment_container)
            val bottomSheetCloser = findViewById<LinearLayout>(R.id.bottom_sheet_closer)
            val chip = findViewById<Chip>(R.id.chip)
            val contextView = findViewById<View>(R.id.coordination)
            val iconinfo = findViewById<ImageView>(R.id.info_icon)
            val window = this.window as Window

            val darkwhite = ContextCompat.getColor(this, R.color.darkwhite)
            val black = ContextCompat.getColor(this, R.color.black)
            val accent = ContextCompat.getColor(this, R.color.accent)
            val tintlistLight = ContextCompat.getColorStateList(this, R.color.tintlist_light)
            val tintlistDark = ContextCompat.getColorStateList(this, R.color.tintlist_dark)
            val chipstatelistLight = resources.getColorStateList(R.color.chip_state_list)
            val chipstatelistDark = resources.getColorStateList(R.color.chip_state_list_dark)
            val textLight = ContextCompat.getColor(this, R.color.textLight)
            val textDark = ContextCompat.getColor(this, R.color.textDark)
            val lightgrey = ContextCompat.getColor(this, R.color.lightgrey)
            val lessdark = ContextCompat.getColor(context, R.color.lessdark)
            val background = ContextCompat.getColor(this, R.color.background)
            val accentPastel = ContextCompat.getColor(this, R.color.accentPastel)
            val dark = ContextCompat.getColor(this, R.color.dark)
            val darkdivider = ContextCompat.getColor(this, R.color.darkdivider)

            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            bottomSheetExpanded = bottomSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED

            if (prefs.getInt("themeInt", 0) == 0) {
                setTheme(R.style.AppTheme0)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.statusBarColor = darkwhite
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    toolbar.setBackgroundColor(darkwhite)
                    window.navigationBarColor = darkwhite
                } else {
                    window.statusBarColor = black
                    window.navigationBarColor = black
                }
                toolbarTxt.setTextColor(accent)
                bottomNav.setBackgroundColor(darkwhite)
                bottomNav.itemIconTintList = tintlistLight
                bottomNav.itemTextColor = tintlistLight
                chip.chipBackgroundColor = chipstatelistLight
                chip.setTextColor(textDark)
                bottomSheet.setBackgroundColor(darkwhite)
                bottomSheetHeader.setTextColor(textDark)
                bottomSheetText.setTextColor(textDark)
                bottomNavDivider.setBackgroundColor(lightgrey)
                iconinfo.setColorFilter(lessdark, android.graphics.PorterDuff.Mode.SRC_IN)

                val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
                val taskDesc = ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.darkwhite))
                setTaskDescription(taskDesc)

            } else if (prefs.getInt("themeInt", 0) == 1) {
                setTheme(R.style.AppTheme0Dark)
                window.statusBarColor = background
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                toolbar.setBackgroundColor(dark)
                toolbarTxt.setTextColor(accentPastel)
                bottomNav.setBackgroundColor(dark)
                bottomNav.itemIconTintList = tintlistDark
                bottomNav.itemTextColor = tintlistDark
                window.navigationBarColor = background
                chip.chipBackgroundColor = chipstatelistDark
                chip.setTextColor(textLight)
                bottomSheet.setBackgroundColor(dark)
                bottomSheetHeader.setTextColor(textLight)
                bottomSheetText.setTextColor(textLight)
                bottomNavDivider.setBackgroundColor(darkdivider)
                iconinfo.setColorFilter(lightgrey, android.graphics.PorterDuff.Mode.SRC_IN)

                val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
                val taskDesc = ActivityManager.TaskDescription(getString(R.string.app_name), bm, resources.getColor(R.color.background))
                setTaskDescription(taskDesc)
            }

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

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }
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
                        val alertDialog: AlertDialog.Builder = if (prefs.getInt("themeInt", 0) == 1) {
                            AlertDialog.Builder(context, R.style.AlertDialogCustomDark)
                        } else {
                            AlertDialog.Builder(context, R.style.AlertDialogCustomLight)
                        }
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

            val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val statusMobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).state
            val statusWifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).state
            if (statusMobile != NetworkInfo.State.CONNECTED && statusWifi != NetworkInfo.State.CONNECTED) {
                Snackbar.make(contextView, getText(R.string.nointernet), Snackbar.LENGTH_LONG).show()
            }

            if (prefs.getBoolean("greeting", false)) {
                if (prefs.getString("username", "").isNotEmpty()) {
                    val animationIn = AnimationUtils.loadAnimation(context, R.anim.chip_slide_in)
                    val animationOut = AnimationUtils.loadAnimation(context, R.anim.chip_slide_out)
                    val generator = Random()
                    val res = resources
                    val greetings = res.getStringArray(R.array.greeting8_array)
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
                        else -> getString(R.string.error)
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

            val userPlan: String = when (prefs.getString("username", "")[prefs.getString("username", "").length - 1].toString().toLowerCase()) {
                "s", "x", "z" -> prefs.getString("username", "") + getString(R.string.nosplan)
                else -> prefs.getString("username", "") + getString(R.string.splan)
            }

            if (prefs.getBoolean("defaultPersonalised", false)) {
                loadFragment(FragmentPersonal())
                bottomNav.selectedItemId = R.id.personal
                toolbarTxt.text = userPlan
            } else {
                loadFragment(FragmentPlan())
                bottomNav.selectedItemId = R.id.plan
                toolbarTxt.text = getString(R.string.app_name)
            }

            bottomNav.setOnNavigationItemSelectedListener { item: MenuItem ->
                lateinit var fragment: Fragment
                when (item.itemId) {
                    R.id.plan -> {
                        fragment = FragmentPlan()
                        toolbarTxt.text = getString(R.string.app_name)
                    }
                    R.id.personal -> {
                        fragment = FragmentPersonal()
                        toolbarTxt.text = userPlan
                    }
                    R.id.menu -> {
                        fragment = FragmentFood()
                        toolbarTxt.text = getString(R.string.foodmenu)
                    }
                    R.id.openinfopanel -> {
                        if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_COLLAPSED) {
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                        } else if (bottomSheetBehaviour.state == BottomSheetBehavior.STATE_EXPANDED) {
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                    R.id.settings -> {
                        fragment = SettingsFragment()
                        toolbarTxt.text = getString(R.string.settings)
                    }
                }
                bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                appbarlayout.setExpanded(true)
                loadFragment(fragment)
            }

            bottomNav.setOnNavigationItemReselectedListener { item: MenuItem ->
                when (item.itemId) {
                    R.id.plan, R.id.personal-> {
                        val recyclerView = findViewById<RecyclerView>(R.id.linearRecycler)
                        recyclerView.post {
                            recyclerView.smoothScrollToPosition(0)
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                    R.id.menu -> {
                        val recyclerView = findViewById<RecyclerView>(R.id.linear_food)
                        recyclerView.post {
                            recyclerView.smoothScrollToPosition(0)
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                    R.id.settings -> {
                        val nsv = findViewById<NestedScrollView>(R.id.nsvsettings)
                        nsv.post {
                            nsv.smoothScrollTo(0, 0)
                            bottomSheetBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
                        }
                    }
                }
                appbarlayout.setExpanded(true)
            }

        }

    }

    private fun loadFragment(fragment: Fragment): Boolean {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit()
        return true
    }

    private fun notificationJob() {
        val componentName = ComponentName(this, ScheduledJobService::class.java)
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