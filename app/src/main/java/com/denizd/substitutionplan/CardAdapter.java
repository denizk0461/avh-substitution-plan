package com.denizd.substitutionplan;

import android.content.SharedPreferences;
import android.graphics.PorterDuff;
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

        String colorChecker = holder.mCourse.getText().toString().toLowerCase();
        if (colorChecker.contains("deu") || colorChecker.contains("dep") || colorChecker.contains("daz")) {
            if (prefs.getInt("colGerman", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colGerman", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colGerman", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("mat") || colorChecker.contains("map")) {
            if (prefs.getInt("colMaths", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colMaths", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colMaths", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("eng") || colorChecker.contains("enp") || colorChecker.contains("ena")) {
            if (prefs.getInt("colEnglish", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colEnglish", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colEnglish", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("spo") || colorChecker.contains("spp") || colorChecker.contains("spth")) {
            if (prefs.getInt("colPhysEd", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colPhysEd", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colPhysEd", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("pol") || colorChecker.contains("pop")) {
            if (prefs.getInt("colPolitics", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colPolitics", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colPolitics", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("dar") || colorChecker.contains("dap")) {
            if (prefs.getInt("colTheatre", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colTheatre", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colTheatre", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("phy") || colorChecker.contains("php")) {
            if (prefs.getInt("colPhysics", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colPhysics", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colPhysics", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("bio") || colorChecker.contains("bip") || colorChecker.contains("nw")) {
            if (prefs.getInt("colBiology", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colBiology", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colBiology", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("che") || colorChecker.contains("chp")) {
            if (prefs.getInt("colChemistry", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colChemistry", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colChemistry", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("phi") || colorChecker.contains("psp")) {
            if (prefs.getInt("colPhilosophy", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colPhilosophy", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colPhilosophy", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("laa") || colorChecker.contains("laf") || colorChecker.contains("lat")) {
            if (prefs.getInt("colLatin", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colLatin", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colLatin", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("spa") || colorChecker.contains("spf")) {
            if (prefs.getInt("colSpanish", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colSpanish", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colSpanish", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("fra") || colorChecker.contains("frf") || colorChecker.contains("frz")) {
            if (prefs.getInt("colFrench", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colFrench", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colFrench", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("inf")) {
            if (prefs.getInt("colCompSci", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colCompSci", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colCompSci", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("ges")) {
            if (prefs.getInt("colHistory", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colHistory", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colHistory", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("rel")) {
            if (prefs.getInt("colReligion", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colReligion", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colReligion", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("geg") || colorChecker.contains("wuk")) {
            if (prefs.getInt("colGeography", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colGeography", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colGeography", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("kun")) {
            if (prefs.getInt("colArts", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colArts", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colArts", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("mus")) {
            if (prefs.getInt("colMusic", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colMusic", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colMusic", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("tue")) {
            if (prefs.getInt("colTurkish", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colTurkish", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colTurkish", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("chi")) {
            if (prefs.getInt("colChinese", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colChinese", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colChinese", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("gll")) {
            if (prefs.getInt("colGLL", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colGLL", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colGLL", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("wat")) {
            if (prefs.getInt("colWAT", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colWAT", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colWAT", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("för")) {
            if (prefs.getInt("colForder", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colForder", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colForder", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else if (colorChecker.contains("wp") || colorChecker.contains("met")) {
            if (prefs.getInt("colWP", 0) != 0) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colWP", 0)));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), prefs.getInt("colWP", 0)));
            } else {
                if (prefs.getInt("themeInt", 0) == 1) {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                } else {
                    holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                    holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                }
            }
        } else {
            if (prefs.getInt("themeInt", 0) == 1) {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lightgrey));
            } else {
                holder.mImageView.getDrawable().setTint(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
                holder.mCourse.setTextColor(ContextCompat.getColor(holder.mImageView.getContext(), R.color.lessdark));
            }
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
