package com.denizd.substitutionplan

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FBNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage?) {
        DataFetcher(true, true, true, applicationContext, application, null).execute()
    }
}