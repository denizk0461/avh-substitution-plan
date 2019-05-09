package com.denizd.substitutionplan;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Subst.class}, version = 2, exportSchema = false)
public abstract class SubstDatabase extends RoomDatabase {

    private static SubstDatabase instance;

    public abstract SubstDao substDao();

    public static synchronized SubstDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    SubstDatabase.class,
                    "subst_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
