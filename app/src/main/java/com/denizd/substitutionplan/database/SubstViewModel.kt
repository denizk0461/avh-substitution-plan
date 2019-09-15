package com.denizd.substitutionplan.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.denizd.substitutionplan.models.Subst

internal class SubstViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubstRepository =
        SubstRepository(application)
    val allSubst: LiveData<List<Subst>>?

    init {
        allSubst = repository.allSubst
    }
}
