package com.denizd.substitutionplan.viewmodels

import android.app.Application
import android.content.res.Configuration
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.denizd.substitutionplan.data.Caller
import com.denizd.substitutionplan.database.SubstRepository
import com.denizd.substitutionplan.models.Substitution
import kotlinx.coroutines.*

internal class SubstViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SubstRepository(application)
    val allSubstitutionsSorted: LiveData<List<Substitution>>?
    val allSubstitutionsOriginal: LiveData<List<Substitution>>?
    val substitutionPlanColours = repo.substitutionPlanColours
    val shouldAutoRefresh = repo.shouldAutoRefresh
    val shouldUseAppSorting = repo.shouldUseAppSorting
    val emptyGeneralPlan = repo.emptyGeneralPlan
    val emptyPersonalSubstitution = repo.emptyPersonalSubstitution

    init {
        allSubstitutionsSorted = repo.allSubstitutionsSorted
        allSubstitutionsOriginal = repo.allSubstitutionsOriginal
    }

    fun getNonNullString(key: String): String {
        return repo.externalNonNullString(key)
    }

    fun checkIfSubstitutionPersonal(substitution: Substitution, classPreference: String, coursePreference: String, includesPsa: Boolean): Boolean {
        return repo.checkIfSubstitutionPersonal(substitution, classPreference, coursePreference, includesPsa)
    }

    fun getGridColumnCount(config: Configuration): Int {
        return repo.getGridColumnCount(config)
    }

    fun refresh(updateUi: (result: String, error: Boolean) -> Unit)  {
        GlobalScope.launch {
            val task = GlobalScope.async { repo.fetchDataOnline(Caller.SUBSTITUTION) }
            val result = task.await()
            updateUi(result.first, result.second)
        }
    }
}
