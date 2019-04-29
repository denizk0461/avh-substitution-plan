package com.denizd.substitutionplan;

import android.graphics.drawable.Drawable;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "subst_table")
public class Subst {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int icon;
    private String group, date, time, course, room, additional;
    private int priority;

    public Subst(int icon, String group, String date, String time, String course, String room, String additional, int priority) {
        this.icon = icon;
        this.group = group;
        this.date = date;
        this.time = time;
        this.course = course;
        this.room = room;
        this.additional = additional;
        this.priority = priority;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getIcon() {
        return icon;
    }

    public String getGroup() {
        return group;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getCourse() {
        return course;
    }

    public String getRoom() {
        return room;
    }

    public String getAdditional() {
        return additional;
    }

    public int getPriority() {
        return priority;
    }
}
