package com.denizd.substitutionplan.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.denizd.substitutionplan.models.Food
import com.denizd.substitutionplan.models.Substitution

@Dao
internal interface SubstDao {

//    @get:Query("SELECT * FROM subst_table ORDER BY date ASC, `group` ASC, time ASC, priority DESC")
    @get:Query("SELECT * FROM subst_table ORDER BY priority DESC")
    val allSubstitutions: LiveData<List<Substitution>>

    @Insert
    fun insertSubst(substitution: Substitution)

    @Update
    fun updateSubst(substitution: Substitution)

    @Delete
    fun deleteSubst(substitution: Substitution)

    @Query("DELETE FROM subst_table")
    fun deleteAllSubst()

    @get:Query("SELECT * FROM food_table ORDER BY priority ASC")
    val allFoods: LiveData<List<Food>>

    @Insert
    fun insertFood(food: Food)

    @Query("DELETE FROM food_table")
    fun deleteAllFoods()
}
