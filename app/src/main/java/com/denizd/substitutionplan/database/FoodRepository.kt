package com.denizd.substitutionplan.database

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.denizd.substitutionplan.models.Food

internal class FoodRepository(application: Application) {

    private val substDao: SubstDao?
    val allFoods: LiveData<List<Food>>?

    init {
        val database =
            SubstDatabase.getFoodInstance(application)
        substDao = database?.substDao()
        allFoods = substDao?.allFoods
    }

    fun insert(food: Food) {
        substDao?.insertFood(food)
    }

    fun deleteAll() {
        substDao?.deleteAllFoods()
    }
}