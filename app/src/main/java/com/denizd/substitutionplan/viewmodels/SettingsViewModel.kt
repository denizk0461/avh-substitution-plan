package com.denizd.substitutionplan.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.denizd.substitutionplan.database.SettingsRepository
import com.denizd.substitutionplan.models.Ringtone
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SettingsRepository(application)

    lateinit var ringtones: List<Ringtone>
    val ringtonesInitialised = ::ringtones.isInitialized

    fun getRingtones(updateUi: () -> Unit) {
        GlobalScope.launch {
            val task = GlobalScope.async { repo.ringtones }
            ringtones = task.await()
            updateUi()
        }
    }
}