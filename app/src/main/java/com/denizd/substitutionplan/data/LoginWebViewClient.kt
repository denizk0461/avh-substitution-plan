package com.denizd.substitutionplan.data

import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Class that extends WebViewClient to provide a function that checks the webpage's cookies to
 * verify a login
 *
 * @param successListener   a reference to the OnLoginSuccessListener implemented in an activity
 */
internal class LoginWebViewClient(successListener: OnLoginSuccessListener) : WebViewClient() {

    private val mSuccessListener = successListener

    /**
     * OnPageFinished has been overridden to check if the cookie "joomla_user_state=logged_in"
     * exists, and returns true to the OnLoginSuccessListener
     *
     * @param view  a reference to the WebView
     * @param url   a reference to the url of the WebView
     */
    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        view?.scrollY = -4000
        val cookies = CookieManager.getInstance().getCookie(url)

        if (cookies.contains("joomla_user_state=logged_in")) {
            mSuccessListener.onLoginSucceeded(true)
        }
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