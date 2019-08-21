package com.denizd.substitutionplan

import android.content.ActivityNotFoundException
import android.net.Uri
import android.preference.PreferenceManager
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
import com.google.android.material.card.MaterialCardView
import java.util.*

class CardAdapter(private var mSubst: List<Subst>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var colour = 0
    private var colourString = ""
    private var colorCheck = ""

    class CardViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var mImageView: ImageView = itemView.findViewById(R.id.iconView)
        var mGroup: TextView = itemView.findViewById(R.id.group)
        var mDate: TextView = itemView.findViewById(R.id.date)
        var mTime: TextView = itemView.findViewById(R.id.time)
        var mCourse: TextView = itemView.findViewById(R.id.course)
        var mRoom: TextView = itemView.findViewById(R.id.room)
        var mAdditional: TextView = itemView.findViewById(R.id.additional)
        var mCard: MaterialCardView = itemView.findViewById(R.id.planCard)
        init { view.setOnClickListener(this) }
        override fun onClick(v: View?) {
            if (mDate.text.toString().length > 7 && mDate.text.toString().substring(3, 7) == "http") {
                try {
                    CustomTabsIntent.Builder().build().launchUrl(mCard.context,
                            Uri.parse(mDate.text.toString().substring(3)))
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(mCard.context, mCard.context.getString(R.string.chromeCompatibleNotFound), Toast.LENGTH_LONG).show()
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
        val prefs = PreferenceManager.getDefaultSharedPreferences(holder.mImageView.context)
        var psa = false
        val strings = arrayOf(SpannableString(currentItem.group), SpannableString(currentItem.time),
                SpannableString(currentItem.course), SpannableString(currentItem.room))
        for (item in strings) {
            val qmark = item.indexOf("?")
            if (qmark != -1) {
                item.setSpan(StrikethroughSpan(), 0, qmark, 0)
            }
        }
        with (currentItem.additional.toLowerCase(Locale.ROOT)) {
            if (contains("eigenverantwortliches arbeiten") || contains("entfall")) {
                strings[2].setSpan(StrikethroughSpan(), 0, strings[2].length, 0)
                strings[3].setSpan(StrikethroughSpan(), 0, strings[3].length, 0)
            }
        }

        holder.mImageView.setImageResource(MiscData.getIconForCourse(currentItem.course))
        holder.mGroup.setText(strings[0], TextView.BufferType.SPANNABLE)
        holder.mTime.setText(strings[1], TextView.BufferType.SPANNABLE)
        holder.mCourse.setText(strings[2], TextView.BufferType.SPANNABLE)
        holder.mRoom.setText(strings[3], TextView.BufferType.SPANNABLE)
        holder.mAdditional.text = currentItem.additional

        if (currentItem.date.isNotEmpty() && currentItem.date.substring(0, 3) == "psa") {
            psa = true
            holder.mDate.visibility = View.GONE
            holder.mDate.text = currentItem.date
            holder.mTime.text = " "
            holder.mImageView.setImageResource(R.drawable.ic_idea)
            holder.mCard.setCardBackgroundColor(ContextCompat.getColor(holder.mImageView.context, R.color.colorAccent))
        } else {
            holder.mDate.visibility = View.VISIBLE
            holder.mDate.text = currentItem.date
        }

        if (!psa) {
            colourString = getColourString(holder.mCourse.text.toString())
            val colourPrefsInt = if (colourString.isNotEmpty()) {
                prefs.getString("card$colourString", "") ?: ""
            } else {
                ""
            }
            colour = MiscData.getColourForString(colourPrefsInt)
            if (colour != 0) {
                holder.mCard.setCardBackgroundColor(ContextCompat.getColor(holder.mCourse.context, colour))
            } else {
                holder.mCard.setCardBackgroundColor(ContextCompat.getColor(holder.mCourse.context, R.color.colorBackgroundLight))
            }
        }

        val textColor = ContextCompat.getColor(holder.mImageView.context, if (psa) {
            R.color.colorBackground
        } else {
            when (colour) {
                R.color.bgPureWhite -> R.color.colorTextDark
                R.color.bgPureBlack -> R.color.colorTextLight
                else -> R.color.colorText
            }
        })
        holder.mImageView.setColorFilter(textColor)
        holder.mGroup.setTextColor(textColor)
        holder.mDate.setTextColor(textColor)
        holder.mTime.setTextColor(textColor)
        holder.mCourse.setTextColor(textColor)
        holder.mRoom.setTextColor(textColor)
        holder.mAdditional.setTextColor(textColor)
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
                "inf" -> "Compsci"
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

}