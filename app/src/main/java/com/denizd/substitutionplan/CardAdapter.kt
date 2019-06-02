package com.denizd.substitutionplan

import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class CardAdapter(private var mSubst: List<Subst>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var colour = 0
    private var colorCheck = ""

    class CardViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        var mImageView: ImageView = itemView.findViewById(R.id.iconView)
        var mGroup: TextView = itemView.findViewById(R.id.group)
        var mDate: TextView = itemView.findViewById(R.id.date)
        var mTime: TextView = itemView.findViewById(R.id.time)
        var mCourse: TextView = itemView.findViewById(R.id.course)
        var mRoom: TextView = itemView.findViewById(R.id.room)
        var mAdditional: TextView = itemView.findViewById(R.id.additional)
        var mCard = itemView.findViewById<MaterialCardView>(R.id.planCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.course_card, parent, false)
        return CardViewHolder(v)
    }

    fun getSubstAt(i: Int): Subst {
        return mSubst[i]
    } // do I need this in Kotlin?

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val currentItem = mSubst[position]
        val prefs = PreferenceManager.getDefaultSharedPreferences(holder.mImageView.context)

        holder.mImageView.setImageResource(currentItem.icon)
        holder.mGroup.text = currentItem.group
        holder.mDate.text = currentItem.date
        holder.mTime.text = currentItem.time
        holder.mCourse.text = currentItem.course
        holder.mRoom.text = currentItem.room
        holder.mAdditional.text = currentItem.additional

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
            holder.mCard.setCardBackgroundColor(ContextCompat.getColor(holder.mCourse.context, R.color.lightbackground))
        }
    }

    override fun getItemCount(): Int = mSubst.size

    fun setSubst(subst: List<Subst>) {
        mSubst = subst
        notifyDataSetChanged()
    }

}