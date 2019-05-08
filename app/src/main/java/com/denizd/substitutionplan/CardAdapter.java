package com.denizd.substitutionplan;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<Subst> mSubst;
    private static int colour;
    private static String colorCheck;

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mGroup, mDate, mTime, mCourse, mRoom, mAdditional;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.iconView);
            mGroup = itemView.findViewById(R.id.group);
            mDate = itemView.findViewById(R.id.date);
            mTime = itemView.findViewById(R.id.time);
            mCourse = itemView.findViewById(R.id.course);
            mRoom = itemView.findViewById(R.id.room);
            mAdditional = itemView.findViewById(R.id.additional);
        }
    }

    public CardAdapter(ArrayList<Subst> subst) {
        mSubst = subst;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_card, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    public Subst getSubstAt(int i) {
        return mSubst.get(i);
    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        Subst currentItem = mSubst.get(position);
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(holder.mImageView.getContext());


        holder.mImageView.setImageResource(currentItem.getIcon());
        holder.mGroup.setText(currentItem.getGroup());
        holder.mDate.setText(currentItem.getDate());
        holder.mTime.setText(currentItem.getTime());
        holder.mCourse.setText(currentItem.getCourse());
        holder.mRoom.setText(currentItem.getRoom());
        holder.mAdditional.setText(currentItem.getAdditional());

        colour = 0;
        try {
            colorCheck = holder.mCourse.getText().toString().toLowerCase().substring(0, 3);
            switch (colorCheck) {
                case "deu": case "dep": case "daz": case "fda": {
                    colour = prefs.getInt("colGerman", 0);
                    break;
                }
                case "mat": case "map": {
                    colour = prefs.getInt("colMaths", 0);
                    break;
                }
                case "eng": case "enp": case "ena": {
                    colour = prefs.getInt("colEnglish", 0);
                    break;
                }
                case "spo": case "spp": case "spth": {
                    colour = prefs.getInt("colPhysEd", 0);
                    break;
                }
                case "pol": case "pop": {
                    colour = prefs.getInt("colPolitics", 0);
                    break;
                }
                case "dar": case "dap": {
                    colour = prefs.getInt("colTheatre", 0);
                    break;
                }
                case "phy": case "php": {
                    colour = prefs.getInt("colPhysics", 0);
                    break;
                }
                case "bio": case "bip": {
                    colour = prefs.getInt("colBiology", 0);
                    break;
                }
                case "che": case "chp": {
                    colour = prefs.getInt("colChemistry", 0);
                    break;
                }
                case "phi": case "psp": {
                    colour = prefs.getInt("colPhilosophy", 0);
                    break;
                }
                case "laa": case "laf": case "lat": {
                    colour = prefs.getInt("colLatin", 0);
                    break;
                }
                case "spa": case "spf": {
                    colour = prefs.getInt("colSpanish", 0);
                    break;
                }
                case "fra": case "frf": case "frz": {
                    colour = prefs.getInt("colFrench", 0);
                    break;
                }
                case "inf": {
                    colour = prefs.getInt("colCompsci", 0);
                    break;
                }
                case "ges": {
                    colour = prefs.getInt("colHistory", 0);
                    break;
                }
                case "rel": {
                    colour = prefs.getInt("colReligion", 0);
                    break;
                }
                case "geg": case "wuk": {
                    colour = prefs.getInt("colGeography", 0);
                    break;
                }
                case "kun": {
                    colour = prefs.getInt("colArts", 0);
                    break;
                }
                case "mus": {
                    colour = prefs.getInt("colMusic", 0);
                    break;
                }
                case "tue": {
                    colour = prefs.getInt("colTurkish", 0);
                    break;
                }
                case "chi": {
                    colour = prefs.getInt("colChinese", 0);
                    break;
                }
                case "gll": {
                    colour = prefs.getInt("colGLL", 0);
                    break;
                }
                case "wat": {
                    colour = prefs.getInt("colWAT", 0);
                    break;
                }
                case "för": {
                    colour = prefs.getInt("colForder", 0);
                    break;
                }
                case "met": {
                    colour = prefs.getInt("colWP", 0);
                    break;
                }
                default: {
                    colour = 0;
                    break;
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
            try {
                String colorCheck2 = holder.mCourse.getText().toString().toLowerCase().substring(0, 2);
                switch (colorCheck2) {
                    case "nw": {
                        colour = prefs.getInt("colBiology", 0);
                        break;
                    }
                    case "wp": {
                        colour = prefs.getInt("colWP", 0);
                        break;
                    }
                    default: {
                        colour = 0;
                        break;
                    }
                }
            } catch (StringIndexOutOfBoundsException e1) {
                colour = 0;
            }
        } finally {
            if (colour == 0) {
                if (prefs.getInt("themeInt", 0) == 1) {
                    colour = R.color.lightgrey;
                } else {
                    colour = R.color.lessdark;
                }
            }
            holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), colour));
            holder.mCourse.setTextColor(ContextCompat.getColor(holder.mCourse.getContext(), colour));
        }
    }

    @Override
    public int getItemCount() {
        return mSubst.size();
    }

    public void setSubst(List<Subst> subst) {
        mSubst = subst;
        notifyDataSetChanged();
    }
}
