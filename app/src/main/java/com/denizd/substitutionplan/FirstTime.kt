package com.denizd.substitutionplan

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.drawable.Animatable
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.AnimationDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import java.util.*

class FirstTime : AppCompatActivity(R.layout.activity_first_time) {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.navigationBarColor = ContextCompat.getColor(this, R.color.background)
        }
        val bm = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        val taskDesc = ActivityManager.TaskDescription(getString(R.string.app_name), bm, ContextCompat.getColor(this, R.color.background))
        setTaskDescription(taskDesc)

        context = this
        prefs = PreferenceManager.getDefaultSharedPreferences(context) as SharedPreferences
        val edit = prefs.edit()
        val fab = findViewById<ExtendedFloatingActionButton>(R.id.efab)
        val start = Intent(this, Main::class.java)
        val name = findViewById<EditText>(R.id.txtName)
        val grade = findViewById<EditText>(R.id.txtClasses)
        val courses = findViewById<EditText>(R.id.txtCourses)
        val notif = findViewById<CheckBox>(R.id.cbNotif)
        val dark = findViewById<CheckBox>(R.id.cbDark)
        val pers = findViewById<CheckBox>(R.id.cbPersonalised)
        val helpClasses = findViewById<ImageButton>(R.id.chipHelpClasses)
        val helpCourses = findViewById<ImageButton>(R.id.chipHelpCourses)

        helpClasses.setOnClickListener {
            createDialog(getString(R.string.helpClassesTitle), getString(R.string.helpClasses))
        }
        helpCourses.setOnClickListener {
            createDialog(getString(R.string.helpCoursesTitle), getString(R.string.helpCourses))
        }

        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        fab.setOnClickListener {
            fab.isClickable = false

            edit.putString("firstTimeDev", Calendar.getInstance().time.toString())
                    .putString("username", name.text.toString())
                    .putString("classes", grade.text.toString())
                    .putString("courses", courses.text.toString())
            if (dark.isChecked) {
                edit.putInt("themeInt", 1)
            } else {
                edit.putInt("themeInt", 0)
            }
            edit.putBoolean("notif", notif.isChecked)
                    .putBoolean("defaultPersonalised", pers.isChecked)
                    .putBoolean("firstTime", false)
                    .apply()

            val cLayout = findViewById<CoordinatorLayout>(R.id.coordinatorLayout)
            val linearInflation = findViewById<LinearLayout>(R.id.linearInflation)

            val x: Int = fab.right - fab.width / 2
            val y: Int = fab.bottom- fab.height / 2
            val endRadius = Math.hypot(cLayout.width.toDouble(), cLayout.height.toDouble()).toInt()
            inflater.inflate(R.layout.welcome_screen, linearInflation, true)
            val anim = ViewAnimationUtils.createCircularReveal(linearInflation, x, y, 0F, endRadius.toFloat())
            val handler = Handler()
            val animOut = AnimationUtils.loadAnimation(context, R.anim.fade_out_short)
            anim.duration = 600
            val colour = findViewById<LinearLayout>(R.id.colourLayout)
            linearInflation.visibility = View.VISIBLE
            anim.start()

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
                val mImgCheck = findViewById<ImageView>(R.id.imageView).drawable as AnimatedVectorDrawable
                mImgCheck.start()
            }, 1000)
            handler.postDelayed({
                startActivity(start)
                finish()
            }, 2000)
        }
    }

    private fun createDialog(title: String, text: String) {
        val alertDialog = AlertDialog.Builder(context, R.style.AlertDialog)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.simple_dialog, null)
        val dialogTitle = dialogView.findViewById<TextView>(R.id.textviewtitle)
        val dialogText = dialogView.findViewById<TextView>(R.id.dialogtext)
        dialogTitle.text = title
        dialogText.text = text
        alertDialog.setView(dialogView).show()
    }
}