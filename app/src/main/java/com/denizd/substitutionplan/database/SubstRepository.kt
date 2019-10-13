package com.denizd.substitutionplan.database

import android.app.Application
import android.os.AsyncTask

import androidx.lifecycle.LiveData
import com.denizd.substitutionplan.models.Substitution

internal class SubstRepository(application: Application) {

    private val substDao: SubstDao?
    val allSubstitutionsSorted: LiveData<List<Substitution>>?
    val allSubstitutionsOriginal: LiveData<List<Substitution>>?

    init {
        val database =
            SubstDatabase.getInstance(application)
        substDao = database?.substDao()
        allSubstitutionsSorted = substDao?.allSubstitutionsSorted
        allSubstitutionsOriginal = substDao?.allSubstitutionsOriginal
    }

    fun insert(substitution: Substitution) {
        substDao?.insertSubst(substitution)
    }

    fun deleteAllSubst() {
        substDao?.deleteAllSubst()
    }
}