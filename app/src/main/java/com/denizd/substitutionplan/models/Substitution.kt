package com.denizd.substitutionplan.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subst_table")
internal data class Substitution(
    val group: String,
    val date: String,
    val time: String,
    val course: String,
    val room: String,
    val additional: String,
    val teacher: String,
    val type: String,
    val priority: Int,
    val date_priority: Int,
    val website_priority: Int
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}