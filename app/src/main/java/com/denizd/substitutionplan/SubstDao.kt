package com.denizd.substitutionplan

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
public interface SubstDao {

    @get:Query("SELECT * FROM subst_table ORDER BY priority DESC")
    val allSubst: LiveData<List<Subst>>

    @Insert
    fun insert(subst: Subst)

    @Update
    fun update(subst: Subst)

    @Delete
    fun delete(subst: Subst)

    @Query("DELETE FROM subst_table")
    fun deleteAllSubst()
}
