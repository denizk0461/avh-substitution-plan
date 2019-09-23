package com.denizd.substitutionplan.data

import android.graphics.Bitmap
import android.util.Log
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import java.lang.IllegalStateException

/**
 * Class that extends WebViewClient to provide a function that checks the webpage's cookies to
 * verify a login
 *
 * @param successListener   a reference to the OnLoginSuccessListener implemented in an activity
 */
internal class LoginWebViewClient(private val successListener: OnLoginSuccessListener) : WebViewClient() {

    /**
     * onPageStarted has been overridden to prevent users from reaching a domain different from
     * the school's login page
     */
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        if (url != "https://307.joomla.schule.bremen.de/index.php/component/users/profile?Itemid=171"
            && url != "https://307.joomla.schule.bremen.de/index.php/component/users/#top") {
            reloadLoginPage(webView = view)
        }
    }

    /**
     * OnPageFinished has been overridden to check if the cookie "joomla_user_state=logged_in"
     * exists, and returns true to the OnLoginSuccessListener
     */
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.scrollY = -10_000

        val cookies = CookieManager.getInstance().getCookie(url)

        if (cookies.contains("joomla_user_state=logged_in")) {
            successListener.onLoginSucceeded(true)
        }
    }

    private fun reloadLoginPage(webView: WebView?) {
        webView?.loadUrl("https://307.joomla.schule.bremen.de/index.php/component/users/#top")
    }

    /**
     * The interface that provides onLoginSucceeded to the activity or fragment that implements it
     */
    internal interface OnLoginSuccessListener {

        /**
         * This function can be overridden to execute code when a login has been successful
         *
         * @param success   boolean value that is set to true if the cookie could be found,
         *                  false otherwise
         */
        fun onLoginSucceeded(success: Boolean)
    }
}