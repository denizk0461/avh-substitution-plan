package com.denizd.substitutionplan.database

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.denizd.substitutionplan.data.DataFetcher
import com.denizd.substitutionplan.models.Substitution

internal class SubstViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SubstRepository = SubstRepository(application)
    val allSubstitutionsSorted: LiveData<List<Substitution>>?
    val allSubstitutionsOriginal: LiveData<List<Substitution>>?
    private val app = application

    init {
        allSubstitutionsSorted = repository.allSubstitutionsSorted
        allSubstitutionsOriginal = repository.allSubstitutionsOriginal
    }

    fun refresh(swipeRefreshLayout: SwipeRefreshLayout, rootView: View, refreshMenu: Boolean) {
        swipeRefreshLayout.isRefreshing = true
        DataFetcher(
            isPlan = true,
            isMenu = refreshMenu,
            isJobService = false,
            context = app,
            application = app,
            parentView = rootView
        ).execute()
    }
}
