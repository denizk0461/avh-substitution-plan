package com.denizd.substitutionplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.denizd.substitutionplan.models.Colour
import com.denizd.substitutionplan.R
import com.google.android.material.card.MaterialCardView

/**
 * Adapter class used for displaying the colours available for tinting courses on the substitution
 * plan. Used in SettingsFragment.kt
 *
 * @param colours           a list of all available colours
 * @param onClickListener   a reference to an OnClickListener
 */
internal class ColourPickerAdapter(private var colours: List<Colour>, private val onClickListener: OnColourClickListener) : RecyclerView.Adapter<ColourPickerAdapter.ColourPickerViewHolder>() {

    internal class ColourPickerViewHolder(view: View, private val clickListener: OnColourClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val title: TextView = view.findViewById(R.id.item_text)
        var image: ImageView = view.findViewById(R.id.item_image)
        var colourNoLang = ""
        init { view.setOnClickListener(this) }

        override fun onClick(v: View?) { clickListener.onColourClick(adapterPosition, colourNoLang) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColourPickerViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.colour_picker_item, parent, false)
        return ColourPickerViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: ColourPickerViewHolder, position: Int) {
        val currentItem = colours[position]

        holder.title.text = currentItem.title
        holder.colourNoLang = currentItem.titleNoLang

        val bg = ContextCompat.getDrawable(holder.image.context, R.drawable.circle)
        bg?.setTint((ContextCompat.getColor(holder.image.context, if (currentItem.colour != 0) {
            currentItem.colour
        } else {
            R.color.colorBackgroundLight
        })))

        holder.image.background = bg
    }

    override fun getItemCount(): Int = colours.size

    internal interface OnColourClickListener {
        fun onColourClick(position: Int, colourNoLang: String)
    }
}