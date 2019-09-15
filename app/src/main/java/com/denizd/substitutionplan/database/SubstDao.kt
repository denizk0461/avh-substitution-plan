package com.denizd.substitutionplan.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.denizd.substitutionplan.models.Food
import com.denizd.substitutionplan.models.Subst

@Dao
internal interface SubstDao {

    @get:Query("SELECT * FROM subst_table ORDER BY priority ASC")
    val allSubst: LiveData<List<Subst>>

    @Insert
    fun insertSubst(subst: Subst)

    @Update
    fun updateSubst(subst: Subst)

    @Delete
    fun deleteSubst(subst: Subst)

    @Query("DELETE FROM subst_table")
    fun deleteAllSubst()

    @get:Query("SELECT * FROM food_table ORDER BY priority ASC")
    val allFoods: LiveData<List<Food>>

    @Insert
    fun insertFood(food: Food)

    @Query("DELETE FROM food_table")
    fun deleteAllFoods()
}
