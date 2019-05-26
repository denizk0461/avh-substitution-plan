package com.denizd.substitutionplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.util.ArrayList
import androidx.recyclerview.widget.RecyclerView

class FoodAdapter(food: ArrayList<Food>) : RecyclerView.Adapter<FoodAdapter.CardViewHolder>() {
    private var mFood: List<Food>? = null

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var mFood: TextView

        init {
            mFood = itemView.findViewById(R.id.cardInfoText)
        }
    }

    init {
        mFood = food
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.just_a_card, parent, false)
        return CardViewHolder(v)
    }

    fun getFoodAt(i: Int): Food {
        return mFood!![i]
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = mFood!![position]

        holder.mFood.text = currentItem.food
    }

    override fun getItemCount(): Int {
        return mFood!!.size
    }

    fun setFood(food: List<Food>) {
        mFood = food
        notifyDataSetChanged()
    }
}
