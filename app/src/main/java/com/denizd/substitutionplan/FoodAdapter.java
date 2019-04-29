package com.denizd.substitutionplan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.CardViewHolder> {
    private List<Food> mFood;

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView mFood;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            mFood = itemView.findViewById(R.id.cardInfoText);
        }
    }

    public FoodAdapter(ArrayList<Food> food) {
        mFood = food;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.just_a_card, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    public Food getFoodAt(int i) {
        return mFood.get(i);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Food currentItem = mFood.get(position);

        holder.mFood.setText(currentItem.getFood());
    }

    @Override
    public int getItemCount() {
        return mFood.size();
    }

    public void setFood(List<Food> food) {
        mFood = food;
        notifyDataSetChanged();
    }
}
