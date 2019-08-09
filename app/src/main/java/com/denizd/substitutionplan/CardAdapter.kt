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

class CardAdapter(private var mSubst: List<Subst>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var colour = 0
    private var colorCheck = ""

    class CardViewHolder (view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var mImageView: ImageView = itemView.findViewById(R.id.iconView)
        var mGroup: TextView = itemView.findViewById(R.id.group)
        var mDate: TextView = itemView.findViewById(R.id.date)
        var mTime: TextView = itemView.findViewById(R.id.time)
        var mCourse: TextView = itemView.findViewById(R.id.course)
        var mRoom: TextView = itemView.findViewById(R.id.room)
        var mAdditional: TextView = itemView.findViewById(R.id.additional)
        var mCard = itemView.findViewById<MaterialCardView>(R.id.planCard)
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

        holder.mImageView.setImageResource(currentItem.icon)
        holder.mGroup.setText(strings[0], TextView.BufferType.SPANNABLE)
        holder.mTime.setText(strings[1], TextView.BufferType.SPANNABLE)
        holder.mCourse.setText(strings[2], TextView.BufferType.SPANNABLE)
        holder.mRoom.setText(strings[3], TextView.BufferType.SPANNABLE)
        holder.mAdditional.text = currentItem.additional

        if (currentItem.date.isNotEmpty() && currentItem.date.substring(0, 3) == "psa") {
            psa = true
            holder.mDate.visibility = View.GONE
            holder.mDate.text = currentItem.date
            holder.mImageView.setImageResource(R.drawable.ic_idea)
            holder.mCard.setCardBackgroundColor(ContextCompat.getColor(holder.mImageView.context, R.color.colorAccent))
        } else {
            holder.mDate.visibility = View.VISIBLE
            holder.mDate.text = currentItem.date
        }

        if (!psa) {
            try {
                colorCheck = holder.mCourse.text.toString().toLowerCase().substring(0, 3)
                colour = when (colorCheck) {
                    "deu", "dep", "daz", "fda" -> prefs.getInt("bgGerman", 0)
                    "mat", "map" -> prefs.getInt("bgMaths", 0)
                    "eng", "enp", "ena" -> prefs.getInt("bgEnglish", 0)
                    "spo", "spp", "spth" -> prefs.getInt("bgPhysEd", 0)
                    "pol", "pop" -> prefs.getInt("bgPolitics", 0)
                    "dar", "dap" -> prefs.getInt("bgTheatre", 0)
                    "phy", "php" -> prefs.getInt("bgPhysics", 0)
                    "bio", "bip", "nw1", "nw2", "nw3", "nw4" -> prefs.getInt("bgBiology", 0)
                    "che", "chp" -> prefs.getInt("bgChemistry", 0)
                    "phi", "psp" -> prefs.getInt("bgPhilosophy", 0)
                    "laa", "laf", "lat" -> prefs.getInt("bgLatin", 0)
                    "spa", "spf" -> prefs.getInt("bgSpanish", 0)
                    "fra", "frf", "frz" -> prefs.getInt("bgFrench", 0)
                    "inf" -> prefs.getInt("bgCompsci", 0)
                    "ges" -> prefs.getInt("bgHistory", 0)
                    "rel" -> prefs.getInt("bgReligion", 0)
                    "geg" -> prefs.getInt("bgGeography", 0)
                    "kun" -> prefs.getInt("bgArts", 0)
                    "mus" -> prefs.getInt("bgMusic", 0)
                    "tue" -> prefs.getInt("bgTurkish", 0)
                    "chi" -> prefs.getInt("bgChinese", 0)
                    "gll" -> prefs.getInt("bgGLL", 0)
                    "wat" -> prefs.getInt("bgWAT", 0)
                    "för" -> prefs.getInt("bgForder", 0)
                    "met", "wpb" -> prefs.getInt("bgWP", 0)
                    else -> 0
                }
            } catch (e: StringIndexOutOfBoundsException) {
                try {
                    colorCheck = holder.mCourse.text.toString().toLowerCase().substring(0, 2)
                    colour = when (colorCheck) {
                        "nw" -> prefs.getInt("bgBiology", 0)
                        "wp" -> prefs.getInt("bgWP", 0)
                        else -> 0
                    }
                } catch (e2: StringIndexOutOfBoundsException) {
                    colour = 0
                }
            }
            if (colour != 0) {
                holder.mCard.setCardBackgroundColor(ContextCompat.getColor(holder.mCourse.context, colour))
            } else {
                holder.mCard.setCardBackgroundColor(ContextCompat.getColor(holder.mCourse.context, R.color.colorBackgroundLight))
            }
        }

        val textColor = if (psa) {
            R.color.colorBackground
        } else {
            when (colour) {
                R.color.bgPureWhite -> R.color.colorTextDark
                R.color.bgPureBlack -> R.color.colorTextLight
                else -> R.color.colorText
            }
        }
        holder.mImageView.setColorFilter(ContextCompat.getColor(holder.mImageView.context, textColor))
        holder.mGroup.setTextColor(ContextCompat.getColor(holder.mImageView.context, textColor))
        holder.mDate.setTextColor(ContextCompat.getColor(holder.mImageView.context, textColor))
        holder.mTime.setTextColor(ContextCompat.getColor(holder.mImageView.context, textColor))
        holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.context, textColor))
        holder.mRoom.setTextColor(ContextCompat.getColor(holder.mImageView.context, textColor))
        holder.mAdditional.setTextColor(ContextCompat.getColor(holder.mImageView.context, textColor))
    }

    override fun getItemCount(): Int = mSubst.size

    fun setSubst(subst: List<Subst>) {
        mSubst = subst
        notifyDataSetChanged()
    }

}