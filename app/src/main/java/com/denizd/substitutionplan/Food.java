package com.denizd.substitutionplan;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

public class Food {

    private String food;

    public Food(String food) {
        this.food = food;
    }

    public String getFood() {
        return food;
    }
}
