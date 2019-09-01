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
        InsertAsync(substDao).execute(food)
    }

    fun deleteAll() {
        DeleteAllAsync(substDao).execute()
    }

    class InsertAsync(substDao: SubstDao?) : AsyncTask<Food, Void, Void>() {
        private val mSubstDao = substDao
        override fun doInBackground(vararg foods: Food): Void? {
            mSubstDao?.insertFood(foods[0])
            return null
        }
    }

    class DeleteAllAsync(substDao: SubstDao?) : AsyncTask<Void, Void, Void>() {
        private val mSubstDao = substDao
        override fun doInBackground(vararg voids: Void): Void? {
            mSubstDao?.deleteAllFoods()
            return null
        }
    }
}