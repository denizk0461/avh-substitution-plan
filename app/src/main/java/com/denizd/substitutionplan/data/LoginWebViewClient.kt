package com.denizd.substitutionplan.data

import android.graphics.Bitmap
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Class that extends WebViewClient to provide a function that checks the website's cookies to
 * verify a login
 *
 * @param successListener   a reference to the OnLoginSuccessListener implemented in an activity
 */
internal class LoginWebViewClient(private val successListener: OnLoginSuccessListener) : WebViewClient() {

    private val schoolUrls = arrayOf("https://307.joomla.schule.bremen.de/index.php/component/users/#top",
        "https://307.joomla.schule.bremen.de/index.php/component/users/?task=user.login&Itemid=171",
        "https://307.joomla.schule.bremen.de/index.php/component/users/profile?Itemid=171")

    /**
     * onPageStarted has been overridden to prevent users from reaching a domain different from
     * the school's login page
     */
    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)

        if (!SubstUtil.checkStringForArray(url.toString(), schoolUrls, false)) {
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

        if (cookies == null) {
            reloadLoginPage(webView = view)
            successListener.onLoginSucceeded(false)
        } else if (cookies.contains("joomla_user_state=logged_in")) {
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
         * This function returns a boolean value depending on the success status of the login process
         *
         * @param success   boolean value that is set to true if the cookie could be found,
         *                  false if an error occurred
         */
        fun onLoginSucceeded(success: Boolean)
    }
}
