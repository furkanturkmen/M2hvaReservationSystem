package com.hva.m2mobi.m2hva_reservationsystem.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hva.m2mobi.m2hva_reservationsystem.R;

import java.util.ArrayList;

public class ReservationsOverviewAdapter extends RecyclerView.Adapter<ReservationsOverviewAdapter.ReservationsViewHolder> {
    public ArrayList<Reservation> mResevationsList;
    private OnItemClickListener mListener;


    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }


    public static class ReservationsViewHolder extends RecyclerView.ViewHolder {
        private TextView description;
        private ImageView mDeleteImage;


        public ReservationsViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            description = itemView.findViewById(R.id.reservation_description);
            mDeleteImage = itemView.findViewById(R.id.image_delete);

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

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }


    }

    public ReservationsOverviewAdapter(ArrayList<Reservation> reservationsList) {
        mResevationsList = reservationsList;
    }

    @NonNull
    @Override
    public ReservationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_cell_reservationsoverview, parent, false);
        ReservationsViewHolder rvh = new ReservationsViewHolder(view, mListener);
        return rvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationsViewHolder holder, int position) {
        Reservation currentItem = mResevationsList.get(position);
        holder.description.setText(currentItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return mResevationsList.size();
    }


}
