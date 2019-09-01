package com.denizd.substitutionplan.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.denizd.substitutionplan.models.Food

internal class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FoodRepository =
        FoodRepository(application)
    val allFoods: LiveData<List<Food>>?

    init {
        allFoods = repository.allFoods
    }

    fun insert(food: Food) {
        repository.insert(food)
    }

    fun deleteAll() {
        repository.deleteAll()
    }
}
