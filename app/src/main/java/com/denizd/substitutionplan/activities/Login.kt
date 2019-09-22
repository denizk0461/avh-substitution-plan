package com.denizd.substitutionplan.activities

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.data.LoginWebViewClient
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlin.math.hypot

internal class Login : AppCompatActivity(R.layout.activity_login_webview), LoginWebViewClient.OnLoginSuccessListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var parentLayout: ConstraintLayout
    private lateinit var logInButton: ExtendedFloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme0)
        parentLayout = findViewById(R.id.constraintLayout)
        parentLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        val title = findViewById<TextView>(R.id.txtWelcome)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val barColour = when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M -> ContextCompat.getColor(this, R.color.legacyBlack)
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.P -> ContextCompat.getColor(this, R.color.colorBackground)
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

        logInButton = findViewById(R.id.loginFab)
        logInButton.setOnClickListener {
            findViewById<LinearLayout>(R.id.linearLayout).apply {
                logInButton.hide()
                removeAllViews()
                title.text = getString(R.string.logging_in)
                val view = View.inflate(this@Login, R.layout.webview, null)
                addView(view)
                val webView = view.findViewById<WebView>(R.id.webView)
                webView.webViewClient = LoginWebViewClient(this@Login)
                openLoginPage(webView)
                handler.postDelayed({
                }, 2000)
            }
        }
    }

    override fun onLoginSucceeded(success: Boolean) {
        if (success) {
            success()
        }
    }

    private fun openLoginPage(webView: WebView) {
        webView.loadUrl("https://307.joomla.schule.bremen.de/index.php/component/users/#top")
    }

    private fun success() {
        prefs.edit().putBoolean("successful_login", true).apply()

        val layoutAfterCircleReveal = findViewById<LinearLayout>(R.id.layoutAfterCircleReveal)

        val x: Int = logInButton.right - logInButton.width / 2
        val y: Int = logInButton.bottom - logInButton.height / 2
        val endRadius = hypot(parentLayout.width.toDouble(), parentLayout.height.toDouble()).toInt()
        View.inflate(this, R.layout.unlock_screen, layoutAfterCircleReveal)
        val anim = ViewAnimationUtils.createCircularReveal(layoutAfterCircleReveal, x, y, 0F, endRadius.toFloat())
        val handler = Handler()
        val animOut = AnimationUtils.loadAnimation(this, R.anim.fade_out_short)
        anim.duration = 600
        val colour = findViewById<LinearLayout>(R.id.colourLayout)
        layoutAfterCircleReveal.visibility = View.VISIBLE
        anim.start()
        logInButton.hide()

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
            val main = Intent(this, Main::class.java)
            startActivity(main)
            finish()
        }, 3000)
    }
}