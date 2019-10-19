package com.denizd.substitutionplan.adapters

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.denizd.substitutionplan.data.SubstUtil
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.models.Substitution
import com.google.android.material.card.MaterialCardView
import java.util.*

/**
 * Adapter class for the Recycler View used to display the substitution plan in
 * PlanFragment.kt and its subclasses
 *
 * @param substitutions a list of all substitutions of data type Substitution
 * @param prefs         a reference to the app's Shared Preferences used to get user-set colours
 */
internal class SubstitutionAdapter(private var substitutions: List<Substitution>, private val prefs: SharedPreferences) : RecyclerView.Adapter<SubstitutionAdapter.CardViewHolder>() {

    /**
     * The ViewHolder class used by SubstitutionAdapter.kt to resolve references to views in
     * course_card.xml that will be inflated and populated with data in
     * SubstitutionAdapter#onBindViewHolder
     *
     * @param view      a reference to the xml that is to be inflated
     */
    internal class CardViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var iconView: ImageView = view.findViewById(R.id.iconView)
        var group: TextView = view.findViewById(R.id.group)
        var date: TextView = view.findViewById(R.id.date)
        var time: TextView = view.findViewById(R.id.time)
        var course: TextView = view.findViewById(R.id.course)
        var room: TextView = view.findViewById(R.id.room)
        var additional: TextView = view.findViewById(R.id.additional)
        var teacher: TextView = view.findViewById(R.id.teacher)
        var spacer: TextView = view.findViewById(R.id.spacer)
        var card: MaterialCardView = view.findViewById(R.id.planCard)
        val context: Context = card.context

        init { view.setOnClickListener(this) }

        override fun onClick(v: View?) {
            if (date.text.toString().length > 7 && date.text.toString().substring(3, 7) == "http") {
                try {
                    CustomTabsIntent.Builder().build().launchUrl(context,
                            Uri.parse(date.text.substring(3)))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(context, context.getString(R.string.chrome_not_found), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.course_card, parent, false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = substitutions[position]
        var psa = false
        val strings = arrayOf(SpannableString(currentItem.group), SpannableString(currentItem.time),
                SpannableString(currentItem.course), SpannableString(currentItem.room),
            SpannableString(currentItem.teacher))
        val cardBackgroundColour: Int
        var colour = 0

        for (string in strings) {
            val questionMarkIndex = string.indexOf("?")
            if (questionMarkIndex != -1) {
                string.setSpan(StrikethroughSpan(), 0, questionMarkIndex, 0)
            }
        }

        val add = currentItem.additional
        val type = currentItem.type.toLowerCase(Locale.ROOT)
        if (add.isNotEmpty()) {
            if (SubstUtil.checkStringForArray(add, SubstUtil.cancellations, true)) {
                strings.strikeThrough()
            }
        } else {
            if (SubstUtil.checkStringForArray(type, SubstUtil.cancellations, true)) {
                strings.strikeThrough()
            }
        }

        var icon = SubstUtil.getIconForCourse(currentItem.course)
        holder.group.setText(strings[0], TextView.BufferType.SPANNABLE)
        holder.time.setText(strings[1], TextView.BufferType.SPANNABLE)
        holder.course.setText(strings[2], TextView.BufferType.SPANNABLE)
        holder.room.setText(strings[3], TextView.BufferType.SPANNABLE)
        holder.teacher.setText(strings[4], TextView.BufferType.SPANNABLE)
        holder.additional.text = if (currentItem.additional.isNotEmpty()) currentItem.additional else currentItem.type

        holder.date.visibility = if (currentItem.date.isNotEmpty() && currentItem.date.substring(0, 3) == "psa") {
            psa = true
            holder.time.text = "   " // creating a margin on the right side
            icon = R.drawable.ic_idea
            cardBackgroundColour = R.color.colorAccent
            View.GONE
        } else {
            val colourString = SubstUtil.getColourString(holder.course.text.toString())
            val colourPrefsInt = if (colourString.isNotEmpty()) {
                prefs.getString("card$colourString", "") ?: ""
            } else {
                ""
            }
            colour = SubstUtil.getColourForString(colourPrefsInt, holder.context)
            cardBackgroundColour = if (colour != 0) {
                colour
            } else {
                R.color.colorBackgroundLight
            }
            View.VISIBLE
        }
        holder.date.text = currentItem.date
        holder.spacer.visibility = if (strings[2].isEmpty() || strings[4].isEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        val textColor = ContextCompat.getColor(holder.iconView.context, if (psa) {
            R.color.colorBackground
        } else {
            when (colour) {
                R.color.bgPureWhite -> R.color.colorTextDark
                R.color.bgPureBlack -> R.color.colorTextLight
                else -> R.color.colorText
            }
        })

        holder.iconView.visibility = if (icon == R.drawable.ic_empty) {
            View.GONE
        } else {
            View.VISIBLE
        }

        holder.card.setCardBackgroundColor(ContextCompat.getColor(holder.iconView.context, cardBackgroundColour))
        holder.iconView.setImageResource(icon)
        holder.iconView.setColorFilter(textColor)
        holder.group.setTextColor(textColor)
        holder.date.setTextColor(textColor)
        holder.time.setTextColor(textColor)
        holder.course.setTextColor(textColor)
        holder.room.setTextColor(textColor)
        holder.additional.setTextColor(textColor)
        holder.spacer.setTextColor(textColor)
        holder.teacher.setTextColor(textColor)
    }

    override fun getItemCount(): Int = substitutions.size

    fun setSubst(substitutions: List<Substitution>) {
        this.substitutions = substitutions
        notifyDataSetChanged()
    }

    /// Strikes through the strings for course, room and teacher
    private fun Array<SpannableString>.strikeThrough() {
        for (i in 2..4) { this[i].setSpan(StrikethroughSpan(), 0, this[i].length, 0) }
    }
}