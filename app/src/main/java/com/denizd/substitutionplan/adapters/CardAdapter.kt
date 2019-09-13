package com.denizd.substitutionplan.adapters

import android.content.ActivityNotFoundException
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
import com.denizd.substitutionplan.data.HelperFunctions
import com.denizd.substitutionplan.R
import com.denizd.substitutionplan.models.Subst
import com.google.android.material.card.MaterialCardView
import java.util.*

internal class CardAdapter(private var mSubst: List<Subst>, private val prefs: SharedPreferences) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var colour = 0
    private var colourString = ""
    private var colorCheck = ""
    private val cancellations = arrayOf("eigenverantwortliches arbeiten", "entfall", "entfällt", "fällt aus", "freisetzung", "vtr. ohne lehrer")

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

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
        init { view.setOnClickListener(this) }
        override fun onClick(v: View?) {
            if (date.text.toString().length > 7 && date.text.toString().substring(3, 7) == "http") {
                try {
                    CustomTabsIntent.Builder().build().launchUrl(card.context,
                            Uri.parse(date.text.toString().substring(3)))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(card.context, card.context.getString(R.string.chromeCompatibleNotFound), Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.course_card, parent, false)
        return CardViewHolder(v)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = mSubst[position]
        var psa = false
        val strings = arrayOf(SpannableString(currentItem.group), SpannableString(currentItem.time),
                SpannableString(currentItem.course), SpannableString(currentItem.room),
            SpannableString(currentItem.teacher))
        var cardBackgroundColour = 0

        for (string in strings) {
            val questionMarkIndex = string.indexOf("?")
            if (questionMarkIndex != -1) {
                string.setSpan(StrikethroughSpan(), 0, questionMarkIndex, 0)
            }
        }

        val add = currentItem.additional.toLowerCase(Locale.ROOT)
        val type = currentItem.type.toLowerCase(Locale.ROOT)
        if (add.isNotEmpty()) {
            if (HelperFunctions.checkStringForArray(add, cancellations)) {
                strikeThrough(strings)
            }
        } else {
            if (HelperFunctions.checkStringForArray(type, cancellations)) {
                strikeThrough(strings)
            }
        }

        var icon = HelperFunctions.getIconForCourse(currentItem.course)
        holder.group.setText(strings[0], TextView.BufferType.SPANNABLE)
        holder.time.setText(strings[1], TextView.BufferType.SPANNABLE)
        holder.course.setText(strings[2], TextView.BufferType.SPANNABLE)
        holder.room.setText(strings[3], TextView.BufferType.SPANNABLE)
        holder.teacher.setText(strings[4], TextView.BufferType.SPANNABLE)
        holder.additional.text = if (currentItem.additional.isNotEmpty()) currentItem.additional else currentItem.type

        holder.date.visibility = if (currentItem.date.isNotEmpty() && currentItem.date.substring(0, 3) == "psa") {
            psa = true
            holder.time.text = " "
            icon = R.drawable.ic_idea
            cardBackgroundColour = R.color.colorAccent
            View.GONE
        } else {
            colourString = getColourString(holder.course.text.toString())
            val colourPrefsInt = if (colourString.isNotEmpty()) {
                prefs.getString("card$colourString", "") ?: ""
            } else {
                ""
            }
            colour = HelperFunctions.getColourForString(colourPrefsInt)
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

    override fun getItemCount(): Int = mSubst.size

    fun setSubst(subst: List<Subst>) {
        mSubst = subst
        notifyDataSetChanged()
    }

    private fun getColourString(course: String): String {
        return try {
            colorCheck = course.toLowerCase(Locale.ROOT).substring(0, 3)
            when (colorCheck) {
                "deu", "dep", "daz", "fda" -> "German"
                "mat", "map" -> "Maths"
                "eng", "enp", "ena" -> "English"
                "spo", "spp", "spth" -> "PhysEd"
                "pol", "pop" -> "Politics"
                "dar", "dap" -> "Theatre"
                "phy", "php" -> "Physics"
                "bio", "bip", "nw1", "nw2", "nw3", "nw4" -> "Biology"
                "che", "chp" -> "Chemistry"
                "phi", "psp" -> "Philosophy"
                "laa", "laf", "lat" -> "Latin"
                "spa", "spf" -> "Spanish"
                "fra", "frf", "frz" -> "French"
                "inf" -> "CompSci"
                "ges" -> "History"
                "rel" -> "Religion"
                "geg" -> "Geography"
                "kun" -> "Arts"
                "mus" -> "Music"
                "tue" -> "Turkish"
                "chi" -> "Chinese"
                "gll" -> "GLL"
                "wat" -> "WAT"
                "för" -> "Forder"
                "met", "wpb" -> "WP"
                else -> ""
            }
        } catch (e: StringIndexOutOfBoundsException) {
            try {
                colorCheck = course.toLowerCase(Locale.ROOT).substring(0, 2)
                when (colorCheck) {
                    "nw" -> "Biology"
                    "wp" -> "WP"
                    else -> ""
                }
            } catch (e2: StringIndexOutOfBoundsException) {
                ""
            }
        }
    }

    private fun strikeThrough(strings: Array<SpannableString>) {
        for (i in 2..4) { strings[i].setSpan(StrikethroughSpan(), 0, strings[i].length, 0) }
    }
}