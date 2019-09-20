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

internal class ColourAdapter(private var mColours: List<Colour>, onClickListener: OnClickListener) : RecyclerView.Adapter<ColourAdapter.ColourViewHolder>() {

    private val mOnClickListener = onClickListener

    class ColourViewHolder(view: View, clickListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val title: TextView = view.findViewById(R.id.item_text)
        var image: ImageView = view.findViewById(R.id.item_image)
        val titleNoLang: TextView = view.findViewById(R.id.item_text_no_lang)
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
        private val mClickListener = clickListener
        init { view.setOnClickListener(this) }

        override fun onClick(v: View?) { mClickListener.onClick(adapterPosition, title.text.toString(), titleNoLang.text.toString()) }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColourViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ColourViewHolder(v, mOnClickListener)
    }

    override fun onBindViewHolder(holder: ColourViewHolder, position: Int) {
        val currentItem = mColours[position]

        holder.title.text = currentItem.title
        holder.titleNoLang.text = currentItem.titleNoLang
        holder.image.setImageDrawable(ContextCompat.getDrawable(holder.image.context, currentItem.icon))
        val colour = if (currentItem.colour != 0) {
            currentItem.colour
        } else {
            R.color.colorBackgroundLight
        }
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.image.context, colour))

        val textColor = when (colour) {
            R.color.bgPureWhite -> R.color.colorTextDark
            R.color.bgPureBlack -> R.color.colorTextLight
            else -> R.color.colorText
        }
        holder.image.setColorFilter(ContextCompat.getColor(holder.image.context, textColor))
        holder.title.setTextColor(ContextCompat.getColor(holder.title.context, textColor))
    }

    override fun getItemCount(): Int = mColours.size

    internal interface OnClickListener {
        fun onClick(position: Int, title: String, titleNoLang: String)
    }
}