package com.denizd.substitutionplan.database

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.denizd.substitutionplan.data.DataFetcher
import com.denizd.substitutionplan.models.Food

internal class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FoodRepository = FoodRepository(application)
    val allFoods: LiveData<List<Food>>?
    private val app = application

    init {
        allFoods = repository.allFoods
    }

    fun refresh(swipeRefreshLayout: SwipeRefreshLayout, rootView: View) {
        swipeRefreshLayout.isRefreshing = true
        DataFetcher(
            isPlan = false,
            isMenu = true,
            isJobService = false,
            context = app,
            application = app,
            parentView = rootView,
            forced = false
        ).execute()
    }
}
