package com.denizd.substitutionplan.database

import android.app.Application
import android.os.AsyncTask

import androidx.lifecycle.LiveData
import com.denizd.substitutionplan.models.Subst

internal class SubstRepository(application: Application) {

    private val substDao: SubstDao?
    val allSubst: LiveData<List<Subst>>?

    init {
        val database =
            SubstDatabase.getInstance(application)
        substDao = database?.substDao()
        allSubst = substDao?.allSubst
    }

    fun insert(subst: Subst) {
        InsertSubstAsync(substDao).execute(subst)
    }

    fun deleteAllSubst() {
        DeleteAllSubstAsync(substDao).execute()
    }

    class InsertSubstAsync(substDao: SubstDao?) : AsyncTask<Subst, Void, Void>() {
        private val mSubstDao = substDao
        override fun doInBackground(vararg substs: Subst): Void? {
            mSubstDao?.insertSubst(substs[0])
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