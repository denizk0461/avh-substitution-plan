package com.denizd.substitutionplan.services

import android.annotation.SuppressLint
import com.denizd.substitutionplan.data.Caller
import com.denizd.substitutionplan.database.SubstRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * This class handles Firebase data notifications and triggers a refresh of the substitution plan
 * as well as the food menu upon arrival
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
internal class FBNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        SubstRepository(application).fetchDataOnline(Caller.JOBSERVICE)
    }
}