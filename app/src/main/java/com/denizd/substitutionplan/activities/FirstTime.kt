package com.denizd.substitutionplan.activities

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.denizd.substitutionplan.R
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import kotlin.math.hypot

internal class FirstTime : AppCompatActivity(R.layout.activity_first_time) {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = this.window
        context = this

        val parentLayout = findViewById<ConstraintLayout>(R.id.coordinatorLayout)
        parentLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)

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

        prefs = PreferenceManager.getDefaultSharedPreferences(context) as SharedPreferences
        val edit = prefs.edit()
        val fab = findViewById<ExtendedFloatingActionButton>(R.id.efab)
        val mainActivity = Intent(this, Main::class.java)
        val nameEditText = findViewById<TextInputEditText>(R.id.txtName)
        val gradeEditText = findViewById<TextInputEditText>(R.id.txtClasses)
        val courseEditText = findViewById<TextInputEditText>(R.id.txtCourses)
        val helpGradeButton = findViewById<ImageButton>(R.id.chipHelpClasses)
        val helpCoursesButton = findViewById<ImageButton>(R.id.chipHelpCourses)
        val greetingCheckBox = findViewById<CheckBox>(R.id.cbGreetings)
        val notificationCheckBox = findViewById<CheckBox>(R.id.cbNotif)
        val darkModeCheckBox = findViewById<CheckBox>(R.id.cbDark)
        val personalPlanCheckBox = findViewById<CheckBox>(R.id.cbPersonalised)

        notificationCheckBox.isChecked = true

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            darkModeCheckBox.visibility = View.GONE
        }

        helpGradeButton.setOnClickListener {
            createDialog(getString(R.string.enterGradeHelpTitle), getString(
                R.string.enterGradeHelp
            ))
        }
        helpCoursesButton.setOnClickListener {
            createDialog(getString(R.string.enterCoursesHelpTitle), getString(
                R.string.enterCoursesHelp
            ))
        }

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        fab.setOnClickListener {
            fab.isClickable = false

            edit.putString("firstTimeDev", Calendar.getInstance().time.toString())
                    .putString("username", nameEditText.text.toString())
                    .putString("classes", gradeEditText.text.toString())
                    .putString("courses", courseEditText.text.toString())
            val themeInt = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                2
            } else {
                if (darkModeCheckBox.isChecked) {
                    1
                } else {
                    0
                }
            }
            edit.putInt("themeInt", themeInt)
            edit.putBoolean("notif", notificationCheckBox.isChecked)
                    .putBoolean("greeting", greetingCheckBox.isChecked)
                    .putBoolean("defaultPersonalised", personalPlanCheckBox.isChecked)
                    .putBoolean("firstTime", false)
                    .putBoolean("colourTransferred", true)
                    .apply()

            val linearInflation = findViewById<LinearLayout>(R.id.linearInflation)

            val x: Int = fab.right - fab.width / 2
            val y: Int = fab.bottom - fab.height / 2
            val endRadius = hypot(parentLayout.width.toDouble(), parentLayout.height.toDouble()).toInt()
            inflater.inflate(R.layout.welcome_screen, linearInflation, true)
            val anim = ViewAnimationUtils.createCircularReveal(linearInflation, x, y, 0F, endRadius.toFloat())
            val handler = Handler()
            val animOut = AnimationUtils.loadAnimation(context,
                R.anim.fade_out_short
            )
            anim.duration = 600
            val colour = findViewById<LinearLayout>(R.id.colourLayout)
            linearInflation.visibility = View.VISIBLE
            anim.start()
            fab.hide()

            handler.postDelayed({
                colour.startAnimation(animOut)
            }, 400)
            animOut.setAnimationListener(object: Animation.AnimationListener {
                override fun onAnimationStart(arg0: Animation) {}
                override fun onAnimationRepeat(arg0: Animation) {}
                override fun onAnimationEnd(arg0: Animation) {
                    colour.visibility = View.GONE
                }
            })
            handler.postDelayed({
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    val mImgCheck = findViewById<ImageView>(R.id.imageView).drawable as AnimatedVectorDrawable
                    mImgCheck.start()
                } else {
                    val mImgCheck = findViewById<ImageView>(R.id.imageView).drawable as AnimatedVectorDrawableCompat
                    mImgCheck.start()
                }
            }, 600)
            handler.postDelayed({
                startActivity(mainActivity)
                finish()
            }, 3000)
        }
    }

    private fun createDialog(title: String, text: String) {
        val alertDialog = AlertDialog.Builder(context, R.style.AlertDialog)
        val dialogView = View.inflate(context, R.layout.simple_dialog, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.textviewtitle)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialogtext)
        dialogTitle.text = title
        dialogText.text = text
        alertDialog.setView(dialogView).show()
    }
}