package com.denizd.substitutionplan.database

import android.app.Application
import android.database.Cursor
import android.media.RingtoneManager
import com.denizd.substitutionplan.models.Ringtone

internal class SettingsRepository(private val application: Application) {

    val ringtones: List<Ringtone> by lazy {
        lateinit var ringtoneCursor: Cursor
        val ringtoneManager = RingtoneManager(application).apply {
            setType(RingtoneManager.TYPE_NOTIFICATION)
            ringtoneCursor = cursor
        }
        val alarms = ArrayList<Ringtone>()

        while (!ringtoneCursor.isAfterLast && ringtoneCursor.moveToNext()) {
            val position = ringtoneCursor.position
            alarms.add(
                Ringtone(
                    ringtoneManager.getRingtone(position).getTitle(application),
                    ringtoneManager.getRingtoneUri(position).toString()
                )
            )
        }
        alarms
    }


}