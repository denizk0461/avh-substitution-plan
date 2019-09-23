package com.denizd.substitutionplan.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.models.Ringtone
import com.google.android.material.card.MaterialCardView

/**
 * Adapter class used for displaying all available notification ringtones to the user up to
 * Android version 7 (Nougat). Used in SettingsFragment.kt
 *
 * @param ringtones         a list of all ringtones including their URI
 * @param onClickListener   a reference to an OnClickListener to set the ringtone
 */
internal class RingtoneAdapter(private val ringtones: List<Ringtone>, private val onClickListener: OnClickListener) : RecyclerView.Adapter<RingtoneAdapter.RingtoneViewHolder>() {

    internal class RingtoneViewHolder(view: View, private val clickListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val name: TextView = view.findViewById(R.id.item_text)
        val uri: TextView = view.findViewById(R.id.item_text_no_lang)
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
        init { view.setOnClickListener(this) }

        override fun onClick(v: View?) { clickListener.onRingtoneClick(adapterPosition, name.text.toString(), uri.text.toString()) }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingtoneViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return RingtoneViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: RingtoneViewHolder, position: Int) {
        val currentItem = ringtones[position]

        holder.name.text = currentItem.name
        holder.uri.text = currentItem.uri
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.context, R.color.colorBackground))
    }

    override fun getItemCount(): Int = ringtones.size

    internal interface OnClickListener {
        fun onRingtoneClick(position: Int, name: String, uri: String)
    }
}