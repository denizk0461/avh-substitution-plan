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
 * Adapter class used for displaying the courses as well as their associated colours in the
 * customisation dialog. Used in SettingsFragment.kt
 *
 * @param colours           a list of all courses and their associated colour
 * @param onClickListener   a reference to an OnClickListener
 */
internal class CourseColourAdapter(private var colours: List<Colour>, private val onClickListener: OnCourseColourClickListener) : RecyclerView.Adapter<CourseColourAdapter.CourseColourViewHolder>() {

    internal class CourseColourViewHolder(view: View, private val clickListener: OnCourseColourClickListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val title: TextView = view.findViewById(R.id.item_text)
        var image: ImageView = view.findViewById(R.id.item_image)
        var titleNoLang: String = ""
        val cardView: MaterialCardView = view.findViewById(R.id.cardView)
        init { view.setOnClickListener(this) }

        override fun onClick(v: View?) { clickListener.onCourseClick(adapterPosition, title.text.toString(), titleNoLang) }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseColourViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.course_colour_picker_item, parent, false)
        return CourseColourViewHolder(v, onClickListener)
    }

    override fun onBindViewHolder(holder: CourseColourViewHolder, position: Int) {
        val currentItem = colours[position]

        holder.title.text = currentItem.title
        holder.titleNoLang = currentItem.titleNoLang
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

    override fun getItemCount(): Int = colours.size

    internal interface OnCourseColourClickListener {
        fun onCourseClick(position: Int, title: String, titleNoLang: String)
    }
}