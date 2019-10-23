package com.denizd.substitutionplan.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.denizd.substitutionplan.data.Caller
import com.denizd.substitutionplan.database.SubstRepository
import com.denizd.substitutionplan.models.Food
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

internal class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = SubstRepository(application)
    val allFoodItems: LiveData<List<Food>>?
    val emptyFoodMenu = repo.emptyFoodMenu
    val shouldAutoRefresh = repo.shouldAutoRefresh

    init {
        allFoodItems = repo.allFoodItems
    }

    fun refresh(updateUi: (result: String, error: Boolean) -> Unit)  {
        GlobalScope.launch {
            val task = GlobalScope.async { repo.fetchDataOnline(Caller.FOODMENU) }
            val result = task.await()
            updateUi(result.first, result.second)
        }
    }
}
