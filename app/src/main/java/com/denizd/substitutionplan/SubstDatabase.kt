package com.denizd.substitutionplan

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Subst::class], version = 3, exportSchema = false)
public abstract class SubstDatabase : RoomDatabase() {

    abstract fun substDao(): SubstDao

    companion object {

        private var instance: SubstDatabase? = null

        fun getInstance(context: Context): SubstDatabase? {
            if (instance == null) {
                synchronized (SubstDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                            SubstDatabase::class.java,
                            "subst_database")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return instance
        }
    }
}