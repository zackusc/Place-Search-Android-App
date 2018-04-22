package com.example.lizi.place_search;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private ArrayList<PlaceItem> placeList;
    private View.OnClickListener mClickListener;

    public PlaceAdapter(ArrayList<PlaceItem> list) {
        placeList = list;
    }

    public void setOnItemClickListener(View.OnClickListener callback) {
        mClickListener = callback;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        PlaceViewHolder holder = new PlaceViewHolder(v);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.onClick(v);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        PlaceItem currentItem = placeList.get(position);
        String iconUrl = currentItem.getIconUrl();
        String placeName = currentItem.getName();
        String address = currentItem.getAddress();

        holder.nameTextView.setText(placeName);
        holder.addressTextView.setText(address);
        Picasso.get().load(iconUrl).into(holder.placeIconView);

    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        public ImageView placeIconView;
        public TextView nameTextView;
        public TextView addressTextView;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            placeIconView = itemView.findViewById(R.id.categoryIcon);
            nameTextView = itemView.findViewById(R.id.place_name);
            addressTextView = itemView.findViewById(R.id.place_address);


        }
    }
}
