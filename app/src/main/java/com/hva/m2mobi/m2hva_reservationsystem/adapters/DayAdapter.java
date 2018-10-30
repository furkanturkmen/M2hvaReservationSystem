package com.hva.m2mobi.m2hva_reservationsystem.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.constraint.ConstraintLayout;

import com.hva.m2mobi.m2hva_reservationsystem.R;

import java.util.ArrayList;
import java.util.List;


public class DayAdapter extends RecyclerView.Adapter<DayAdapter.DayViewHolder> {
    public List<Day> mListDays;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public DayAdapter(List<Day> mListDays) {
        this.mListDays = mListDays;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_cell_day, parent, false);
        return new DayViewHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Day day = mListDays.get(position);
        holder.number.setText(Integer.toString(day.getNumberInMonth()));
        holder.day.setText(day.getDayInWeek());
        Context context = holder.linearLayout.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        // Add the new row before the add field button.
        for(int i = 0; i < day.getNumberOfBookings(); i++){
            View rowView = inflater.inflate(R.layout.line, null);
            holder.linearLayout.addView(rowView, holder.linearLayout.getChildCount() - 1);
        }
    }

    @Override
    public int getItemCount() {
        return mListDays.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView day;
        private TextView number;
        private LinearLayout linearLayout;

        public DayViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linearLayout_day);
            day = itemView.findViewById(R.id.dayTextView);
            number = itemView.findViewById(R.id.numberTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

        }
    }


}
