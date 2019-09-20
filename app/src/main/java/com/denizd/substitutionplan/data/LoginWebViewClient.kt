package com.denizd.substitutionplan.data

import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * class that extends WebViewClient to provide a function that checks the webpage's cookies to
 * verify a login
 *
 * @param successListener   a reference to the OnLoginSuccessListener implemented in an activity
 */
internal class LoginWebViewClient(successListener: OnLoginSuccessListener) : WebViewClient() {

    private val mSuccessListener = successListener

    /**
     * onPageFinished has been overridden to check if the cookie "joomla_user_state=logged_in"
     * exists, and returns true to the OnLoginSuccessListener
     *
     * @param view  a reference to the WebView
     * @param url   a reference to the url of the WebView
     */
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        val cookies = CookieManager.getInstance().getCookie(url)

        if (cookies.contains("joomla_user_state=logged_in")) {
            mSuccessListener.onLoginSucceeded(true)
        }
    }

    /**
     * the interface that provides onLoginSucceeded to the activity or fragment that implements it
     */
    internal interface OnLoginSuccessListener {

        /**
         * the function that can be overridden to check whether a successful login has occurred
         *
         * @param success   boolean value that returns true if the cookie could be found,
         *                  false otherwise
         */
        fun onLoginSucceeded(success: Boolean)
    }
}