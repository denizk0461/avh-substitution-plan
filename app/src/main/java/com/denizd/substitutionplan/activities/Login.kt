package com.denizd.substitutionplan.activities

import android.animation.LayoutTransition
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.preference.PreferenceManager
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.data.HelperFunctions
import com.denizd.substitutionplan.data.LoginWebViewClient
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception
import kotlin.math.hypot

/**
 * This class will be instantiated upon first launch of the app to present the user with a WebView
 * allowing them to log in to the school website to verify them as being occupied at the gymnasium.
 */
internal class Login : AppCompatActivity(R.layout.activity_login_webview), LoginWebViewClient.OnLoginSuccessListener {

    private lateinit var prefs: SharedPreferences
    private lateinit var parentLayout: ConstraintLayout
    private lateinit var logInButton: ExtendedFloatingActionButton
    private lateinit var snackBarLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme0)
        parentLayout = findViewById(R.id.constraintLayout)
        snackBarLayout = findViewById(R.id.snackBarLayout)
        parentLayout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        val title = findViewById<TextView>(R.id.txtWelcome)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)

        HelperFunctions.setTheme(window = window, context = this)

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
        } else {
            Snackbar.make(snackBarLayout, getString(R.string.error_please_try_again), Snackbar.LENGTH_LONG).show()
        }
    }

    private fun openLoginPage(webView: WebView) {
        webView.loadUrl("https://307.joomla.schule.bremen.de/index.php/component/users/#top")
    }

    private fun success() {
        prefs.edit().putBoolean("successful_login", true).apply()
        val handler = Handler()
        try {
            val layoutAfterCircleReveal = findViewById<LinearLayout>(R.id.layoutAfterCircleReveal)

            val x: Int = logInButton.right - logInButton.width / 2
            val y: Int = logInButton.bottom - logInButton.height / 2
            val endRadius = hypot(parentLayout.width.toDouble(), parentLayout.height.toDouble()).toInt()
            View.inflate(this, R.layout.unlock_screen, layoutAfterCircleReveal)
            val anim = ViewAnimationUtils.createCircularReveal(layoutAfterCircleReveal, x, y, 0F, endRadius.toFloat())
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
        } catch (e: Exception) {
        } finally {
            handler.postDelayed({
                val main = Intent(this, Main::class.java)
                startActivity(main)
                finish()
            }, 3000)
        }

    }
}