package com.denizd.substitutionplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class RingtoneAdapter(private var _ringtones: List<Ringtone>, onClickListener: OnClickListener) : RecyclerView.Adapter<RingtoneAdapter.RingtoneViewHolder>() {

    val _onClickListener = onClickListener

    class RingtoneViewHolder(view: View, clickListener: OnClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val name: TextView = view.findViewById(R.id.item_text)
        val uri: TextView = view.findViewById(R.id.item_text_no_lang)
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
        val _clickListener = clickListener
        init { view.setOnClickListener(this) }

        override fun onClick(v: View?) { _clickListener.onRingtoneClick(adapterPosition, name.text.toString(), uri.text.toString()) }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingtoneViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return RingtoneViewHolder(v, _onClickListener)
    }

    override fun onBindViewHolder(holder: RingtoneViewHolder, position: Int) {
        val currentItem = _ringtones[position]

        holder.name.text = currentItem.name
        holder.uri.text = currentItem.uri
        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(holder.cardView.context, R.color.colorBackground))
    }

    override fun getItemCount(): Int = _ringtones.size

    fun setRingtones(ringtones: List<Ringtone>) {
        _ringtones = ringtones
        notifyDataSetChanged()
    }

    public interface OnClickListener {
        fun onRingtoneClick(position: Int, name: String, uri: String)
    }
}