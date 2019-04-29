package com.denizd.substitutionplan;

import android.graphics.drawable.Drawable;

public class PlanCard {
    private Drawable mIcon;
    private String mGroup, mDate, mTime, mCourse, mRoom, mAdditional;

    public PlanCard(Drawable icon, String group, String date, String time, String course, String room, String additional) {
        mIcon = icon;
        mGroup = group;
        mDate = date;
        mTime = time;
        mCourse = course;
        mRoom = room;
        mAdditional = additional;
    }

    public Drawable getImageResource() {
        return mIcon;
    }

    public String getGroup() {
        return mGroup;
    }

    public String getDate() {
        return mDate;
    }

    public String getTime() {
        return mTime;
    }

    public String getCourse() {
        return mCourse;
    }

    public String getRoom() {
        return mRoom;
    }

    public String getAdditional() {
        return mAdditional;
    }
}
