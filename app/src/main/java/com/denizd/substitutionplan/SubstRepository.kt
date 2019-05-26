package com.denizd.substitutionplan

import android.app.Application
import android.os.AsyncTask

import androidx.lifecycle.LiveData

public class SubstRepository(application: Application) {

    private val substDao: SubstDao?
    val allSubst: LiveData<List<Subst>>?

    init {
        val database = SubstDatabase.getInstance(application)
        substDao = database?.substDao()
        allSubst = substDao?.allSubst
    }

    fun insert(subst: Subst) {
        InsertSubstAsync(substDao).execute(subst)
    }

    fun update(subst: Subst) {
        UpdateSubstAsync(substDao).execute(subst)
    }

    fun delete(subst: Subst) {
        DeleteSubstAsync(substDao).execute(subst)
    }

    fun deleteAllSubst() {
        DeleteAllSubstAsync(substDao).execute()
    }

    class InsertSubstAsync(substDao: SubstDao?) : AsyncTask<Subst, Void, Void>() {
        val mSubstDao = substDao
        override fun doInBackground(vararg substs: Subst): Void? {
            mSubstDao?.insert(substs[0])
            return null
        }
    }

    class UpdateSubstAsync(substDao: SubstDao?) : AsyncTask<Subst, Void, Void>() {
        private val mSubstDao = substDao
        override fun doInBackground(vararg substs: Subst): Void? {
            mSubstDao?.update(substs[0])
            return null
        }
    }

    class DeleteSubstAsync(substDao: SubstDao?) : AsyncTask<Subst, Void, Void>() {
        private val mSubstDao = substDao
        override fun doInBackground(vararg substs: Subst): Void? {
            mSubstDao?.delete(substs[0])
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