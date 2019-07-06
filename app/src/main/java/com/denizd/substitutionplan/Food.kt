package com.denizd.substitutionplan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food_table")
class Food(val food: String, @PrimaryKey val priority: Int)
