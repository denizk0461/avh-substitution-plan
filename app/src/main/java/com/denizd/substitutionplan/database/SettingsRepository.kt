package com.denizd.substitutionplan.database

import android.app.Application
import android.database.Cursor
import android.media.RingtoneManager
import androidx.preference.PreferenceManager
import com.denizd.substitutionplan.data.SubstUtil
import com.denizd.substitutionplan.models.Ringtone

internal class SettingsRepository(private val application: Application) {

    private val prefs = PreferenceManager.getDefaultSharedPreferences(application)
    val notificationChannel = SubstUtil.getNotificationChannel(application, prefs)

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

    fun getInt(key: String, default: Int = 0): Int {
        return prefs.getInt(key, default)
    }

    fun setAndApplyInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun getBool(key: String, default: Boolean = false): Boolean {
        return prefs.getBoolean(key, default)
    }

    fun setAndApplyBool(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun getString(key: String, default: String = ""): String? {
        return prefs.getString(key, default)
    }

    fun setAndApplyString(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }


}