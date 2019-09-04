package com.denizd.substitutionplan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.denizd.substitutionplan.models.Food
import com.denizd.substitutionplan.models.Subst

@Database(entities = [Subst::class, Food::class], version = 6, exportSchema = false)
internal abstract class SubstDatabase : RoomDatabase() {

    abstract fun substDao(): SubstDao

    companion object {

        private var instance: SubstDatabase? = null
        private val addTeacherColumn = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    "ALTER TABLE subst_table ADD COLUMN teacher TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getInstance(context: Context): SubstDatabase? {
            if (instance == null) {
                synchronized (SubstDatabase::class) {
                    instance = Room.databaseBuilder(context.applicationContext,
                            SubstDatabase::class.java,
                            "subst_database")
                            .addMigrations(addTeacherColumn)
                            .build()
                }
            }
            return instance
        }

        private var foodInstance: SubstDatabase? = null

        fun getFoodInstance(context: Context): SubstDatabase? {
            if (foodInstance == null) {
                synchronized (SubstDatabase::class) {
                    foodInstance = Room.databaseBuilder(context.applicationContext,
                            SubstDatabase::class.java,
                            "food_database")
                            .fallbackToDestructiveMigration()
                            .build()
                }
            }
            return foodInstance
        }
    }
}