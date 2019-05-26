package com.denizd.substitutionplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

public class SubstViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubstRepository
    val allSubst: LiveData<List<Subst>>?

    init {
        repository = SubstRepository(application)
        allSubst = repository.allSubst
    }

    fun insert(subst: Subst) {
        repository.insert(subst)
    }

    fun update(subst: Subst) {
        repository.update(subst)
    }

    fun delete(subst: Subst) {
        repository.delete(subst)
    }

    fun deleteAllSubst() {
        repository.deleteAllSubst()
    }
}
