package com.denizd.substitutionplan

import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class CardAdapter(private var mSubst: List<Subst>) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    var colour = 0
    var colorCheck = ""

    class CardViewHolder (view: View) : RecyclerView.ViewHolder(view) {
        var mImageView: ImageView = itemView.findViewById(R.id.iconView)
        var mGroup: TextView = itemView.findViewById(R.id.group)
        var mDate: TextView = itemView.findViewById(R.id.date)
        var mTime: TextView = itemView.findViewById(R.id.time)
        var mCourse: TextView = itemView.findViewById(R.id.course)
        var mRoom: TextView = itemView.findViewById(R.id.room)
        var mAdditional: TextView = itemView.findViewById(R.id.additional)
    }

    fun CardAdapter(subst: ArrayList<Subst>) {
        mSubst = subst
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.course_card, parent, false)
        return CardViewHolder(v)
    }

    fun getSubstAt(i: Int): Subst {
        return mSubst[i]
    }

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
                "deu", "dep", "daz", "fda" -> prefs.getInt("colGerman", 0)
                "mat", "map" -> prefs.getInt("colMaths", 0)
                "eng", "enp", "ena" -> prefs.getInt("colEnglish", 0)
                "spo", "spp", "spth" -> prefs.getInt("colPhysEd", 0)
                "pol", "pop" -> prefs.getInt("colPolitics", 0)
                "dar", "dap" -> prefs.getInt("colTheatre", 0)
                "phy", "php" -> prefs.getInt("colPhysics", 0)
                "bio", "bip" -> prefs.getInt("colBiology", 0)
                "che", "chp" -> prefs.getInt("colChemistry", 0)
                "phi", "psp" -> prefs.getInt("colPhilosophy", 0)
                "laa", "laf", "lat" -> prefs.getInt("colLatin", 0)
                "spa", "spf" -> prefs.getInt("colSpanish", 0)
                "fra", "frf", "frz" -> prefs.getInt("colFrench", 0)
                "inf" -> prefs.getInt("colCompsci", 0)
                "ges" -> prefs.getInt("colHistory", 0)
                "rel" -> prefs.getInt("colReligion", 0)
                "geg" -> prefs.getInt("colGeography", 0)
                "kun" -> prefs.getInt("colArts", 0)
                "mus" -> prefs.getInt("colMusic", 0)
                "tue" -> prefs.getInt("colTurkish", 0)
                "chi" -> prefs.getInt("colChinese", 0)
                "gll" -> prefs.getInt("colGLL", 0)
                "wat" -> prefs.getInt("colWAT", 0)
                "för" -> prefs.getInt("colForder", 0)
                "met" -> prefs.getInt("colWP", 0)
                else -> 0
            }
        } catch (e: StringIndexOutOfBoundsException) {
            try {
                colorCheck = holder.mCourse.text.toString().toLowerCase().substring(0, 2)
                colour = when (colorCheck) {
                    "nw" -> prefs.getInt("colBiology", 0)
                    "wp" -> prefs.getInt("colWP", 0)
                    else -> 0
                }
            } catch (e2: StringIndexOutOfBoundsException) {
                colour = 0
            }
        } finally {
            if (colour == 0) {
                colour = R.color.textcolor
            }
        }

        holder.mImageView.drawable.setTint(ContextCompat.getColor(holder.mImageView.context, colour))
        holder.mCourse.setTextColor(ContextCompat.getColor(holder.mCourse.context, colour))
    }

    override fun getItemCount(): Int = mSubst.size

    fun setSubst(subst: List<Subst>) {
        mSubst = subst
        notifyDataSetChanged()
    }

}