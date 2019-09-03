package com.denizd.substitutionplan.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
internal data class Food(val food: String, @PrimaryKey val priority: Int)
