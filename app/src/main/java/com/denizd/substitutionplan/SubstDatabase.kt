package com.denizd.substitutionplan

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Subst::class, Food::class], version = 5, exportSchema = false)
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

        fun getFoodInstance(context: Context): SubstDatabase? {
            if (instance == null) {
                synchronized (SubstDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                            SubstDatabase::class.java,
                            "food_database")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return instance
        }
    }
}