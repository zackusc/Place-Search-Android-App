package com.example.lizi.place_search;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.placeViewHolder> {
    private ArrayList<PlaceItem> placeList;
//    private Context mContext;

    public PlaceAdapter(ArrayList<PlaceItem> list) {
        placeList = list;
    }

    @NonNull
    @Override
    public placeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        return new placeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull placeViewHolder holder, int position) {
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

    public class placeViewHolder extends RecyclerView.ViewHolder {
        public ImageView placeIconView;
        public TextView nameTextView;
        public TextView addressTextView;

        public placeViewHolder(View itemView) {
            super(itemView);
            placeIconView = itemView.findViewById(R.id.categoryIcon);
            nameTextView = itemView.findViewById(R.id.place_name);
            addressTextView = itemView.findViewById(R.id.place_address);


        }
    }
}
