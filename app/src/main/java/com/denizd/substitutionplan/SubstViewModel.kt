package com.denizd.substitutionplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData

public class SubstViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubstRepository = SubstRepository(application)
    val allSubst: LiveData<List<Subst>>?

    init {
        allSubst = repository.allSubst
    }

    fun insertSubst(subst: Subst) {
        repository.insert(subst)
    }

    fun updateSubst(subst: Subst) {
        repository.update(subst)
    }

    fun deleteSubst(subst: Subst) {
        repository.delete(subst)
    }

    fun deleteAllSubst() {
        repository.deleteAllSubst()
    }
}
