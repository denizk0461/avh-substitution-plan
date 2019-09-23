package com.denizd.substitutionplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.denizd.substitutionplan.models.Food
import com.denizd.substitutionplan.R

/**
 * Adapter class used in FoodFragment.kt to display the food menu to the user
 *
 * @param foods     a list of all food entries on the menu
 */
internal class FoodAdapter(private var foods: List<Food>) : RecyclerView.Adapter<FoodAdapter.FoodViewHolder>() {

    internal class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.cardInfoText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.just_a_card, parent, false)
        return FoodViewHolder(v)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val currentItem = foods[position]

        holder.text.text = currentItem.food
    }

    override fun getItemCount(): Int {
        return foods.size
    }

    fun setFood(foods: List<Food>) {
        this.foods = foods
        notifyDataSetChanged()
    }
}
