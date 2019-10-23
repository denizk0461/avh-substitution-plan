package com.denizd.substitutionplan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.denizd.substitutionplan.models.Food
import com.denizd.substitutionplan.models.Substitution

@Database(entities = [Substitution::class, Food::class], version = 9, exportSchema = false)
internal abstract class SubstDatabase : RoomDatabase() {

    abstract fun substDao(): SubstDao

    companion object {

        private val addTeacherColumn = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE subst_table ADD COLUMN teacher TEXT NOT NULL DEFAULT ''")
            }
        }
        private val addTypeColumn = object : Migration(6, 7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE subst_table ADD COLUMN type TEXT NOT NULL DEFAULT ''")
            }
        }
        private val addDatePriorityColumn = object : Migration(7, 8) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE subst_table ADD COLUMN date_priority INTEGER NOT NULL DEFAULT 0")
            }
        }
        private val addWebsitePriorityColumn = object : Migration(8, 9) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE subst_table ADD COLUMN website_priority INTEGER NOT NULL DEFAULT 0")
            }
        }

        private var substInstance: SubstDatabase? = null

        fun getSubstInstance(context: Context): SubstDatabase? {
            if (substInstance == null) {
                synchronized (SubstDatabase::class) {
                    substInstance = Room.databaseBuilder(context.applicationContext,
                        SubstDatabase::class.java,
                        "subst_database")
                        .addMigrations(addTeacherColumn, addTypeColumn, addDatePriorityColumn, addWebsitePriorityColumn)
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return substInstance
        }

        private var foodInstance: SubstDatabase? = null

        fun getFoodInstance(context: Context): SubstDatabase? {
            if (foodInstance == null) {
                synchronized (SubstDatabase::class) {
                    foodInstance = Room.databaseBuilder(context.applicationContext,
                        SubstDatabase::class.java,
                        "food_database")
                        .fallbackToDestructiveMigrationFrom(5, 6)
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return foodInstance
        }
    }
}