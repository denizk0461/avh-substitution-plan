package com.denizd.substitutionplan

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FBNotificationService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        DataFetcher(isplan = true, ismenu = true, isjobservice = true, context = applicationContext,
                application = application, parentview = null).execute()
    }
}