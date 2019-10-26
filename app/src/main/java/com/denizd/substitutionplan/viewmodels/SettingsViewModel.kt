package com.denizd.substitutionplan.viewmodels

import android.annotation.TargetApi
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.denizd.substitutionplan.database.SettingsRepository
import com.denizd.substitutionplan.models.Ringtone
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SettingsRepository(application)
    var shouldGreet: Boolean
        get() = repo.getBool("greeting")
        set(it) = repo.setAndApplyBool("greeting", it)
    var shouldReceiveNotifications: Boolean
        get() = repo.getBool("notif")
        set(it) = repo.setAndApplyBool("notif", it)
    var shouldOpenPersonalisedPlan: Boolean
        get() = repo.getBool("defaultPersonalised")
        set(it) = repo.setAndApplyBool("defaultPersonalised", it)
    var shouldAutoRefresh: Boolean
        get() = repo.getBool("autoRefresh")
        set(it) = repo.setAndApplyBool("autoRefresh", it)
    var shouldUseAppSorting: Boolean
        get() = repo.getBool("app_specific_sorting", true)
        set(it) = repo.setAndApplyBool("app_specific_sorting", it)


    var username: String
        get() = repo.getString("username") ?: ""
        set(it) = repo.setAndApplyString("username", it)
    var group: String
        get() = repo.getString("classes") ?: ""
        set(it) = repo.setAndApplyString("classes", it)
    var courses: String
        get() = repo.getString("courses") ?: ""
        set(it) = repo.setAndApplyString("courses", it)

    var currentTheme: Int
        get() = repo.getInt("themeInt")
        set(it) = repo.setAndApplyInt("themeInt", it)

    @TargetApi(26)
    fun getNotificationChannel() = repo.getNotificationChannel()

    lateinit var ringtones: List<Ringtone>
    val ringtonesInitialised = ::ringtones.isInitialized

    fun getRingtones(updateUi: () -> Unit) {
        GlobalScope.launch {
            val task = async { repo.ringtones }
            ringtones = task.await()
            launch(Dispatchers.Main) { updateUi() }
        }
    }

    fun getInt(key: String, default: Int = 0) = repo.getInt(key, default)
    fun setAndApplyInt(key: String, value: Int) = repo.setAndApplyInt(key, value)
    fun getBool(key: String, default: Boolean = false) = repo.getBool(key, default)
    fun setAndApplyBool(key: String, value: Boolean) = repo.setAndApplyBool(key, value)
    fun getString(key: String, default: String = "", ifNull: String = "") = repo.getString(key, default) ?: ifNull
    fun setAndApplyString(key: String, value: String) = repo.setAndApplyString(key, value)

    fun clearTimes() = repo.clearTimes()
    fun forceRefresh(updateUi: (result: String, error: Boolean) -> Unit) = GlobalScope.launch {
        val task = async { repo.forceRefresh() }
        val result = task.await()
        launch(Dispatchers.Main) { updateUi(result.first, result.second) }
    }
}