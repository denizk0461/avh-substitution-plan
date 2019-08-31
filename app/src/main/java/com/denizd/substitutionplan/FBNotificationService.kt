package com.denizd.substitutionplan

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FBNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        DataFetcher(isPlan = true, isMenu = true, isJobService = true, context = applicationContext,
                application = application, parentView = null).execute()
    }
}