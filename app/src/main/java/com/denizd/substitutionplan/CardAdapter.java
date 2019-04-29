package com.denizd.substitutionplan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
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


        holder.mImageView.setImageResource(currentItem.getIcon());
        holder.mGroup.setText(currentItem.getGroup());
        holder.mDate.setText(currentItem.getDate());
        holder.mTime.setText(currentItem.getTime());
        holder.mCourse.setText(currentItem.getCourse());
        holder.mRoom.setText(currentItem.getRoom());
        holder.mAdditional.setText(currentItem.getAdditional());
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
