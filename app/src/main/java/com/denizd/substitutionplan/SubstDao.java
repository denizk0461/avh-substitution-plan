package com.denizd.substitutionplan;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SubstDao {

    @Insert
    void insert(Subst subst);

    @Update
    void update(Subst subst);

    @Delete
    void delete(Subst subst);

    @Query("DELETE FROM subst_table")
    void deleteAllSubst();

    @Query("SELECT * FROM subst_table ORDER BY priority DESC")
    LiveData<List<Subst>> getAllSubst();
}
