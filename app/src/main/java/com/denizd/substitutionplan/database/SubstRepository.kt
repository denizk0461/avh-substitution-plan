package com.denizd.substitutionplan.database

import android.app.Application
import android.os.AsyncTask

import androidx.lifecycle.LiveData
import com.denizd.substitutionplan.models.Substitution

internal class SubstRepository(application: Application) {

    private val substDao: SubstDao?
    val allSubstitutions: LiveData<List<Substitution>>?

    init {
        val database =
            SubstDatabase.getInstance(application)
        substDao = database?.substDao()
        allSubstitutions = substDao?.allSubstitutions
    }

    fun insert(substitution: Substitution) {
        InsertSubstAsync(substDao).execute(substitution)
    }

    fun deleteAllSubst() {
        DeleteAllSubstAsync(substDao).execute()
    }

    class InsertSubstAsync(substDao: SubstDao?) : AsyncTask<Substitution, Void, Void>() {
        private val mSubstDao = substDao
        override fun doInBackground(vararg substitutions: Substitution): Void? {
            mSubstDao?.insertSubst(substitutions[0])
            return null
        }
    }

    class DeleteAllSubstAsync(substDao: SubstDao?) : AsyncTask<Void, Void, Void>() {
        private val mSubstDao = substDao
        override fun doInBackground(vararg voids: Void): Void? {
            mSubstDao?.deleteAllSubst()
            return null
        }
    }
}