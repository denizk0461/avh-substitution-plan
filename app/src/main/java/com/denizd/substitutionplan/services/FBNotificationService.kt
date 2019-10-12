package com.denizd.substitutionplan.services

import android.annotation.SuppressLint
import com.denizd.substitutionplan.data.DataFetcher
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * This class handles Firebase data notifications and triggers a refresh of the substitution plan
 * as well as the food menu upon arrival
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
internal class FBNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        DataFetcher(
            isPlan = true,
            isMenu = true,
            isJobService = true,
            context = applicationContext,
            application = application,
            parentView = null
        ).execute()
    }
}