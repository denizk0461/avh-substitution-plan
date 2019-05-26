package com.denizd.substitutionplan

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subst_table")
public class Subst(val icon: Int, val group: String, val date: String, val time: String, val course: String, val room: String, val additional: String, val priority: Int) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}