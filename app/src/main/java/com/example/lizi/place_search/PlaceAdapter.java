package com.example.lizi.place_search;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    public final static int RESULTS_LIST = 0;
    public final static int FAVORITES_LIST = 1;
    private ArrayList<PlaceItem> placeList;
    private int placeListType;
    private OnItemClickListener mListener;
    private Context mContext;
    private FavoritesManager mFavoritesManager;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public PlaceAdapter(Context context, ArrayList<PlaceItem> list, int placeListType) {
        mContext = context;
        placeList = list;
        this.placeListType = placeListType;
        mFavoritesManager = new FavoritesManager();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_item, parent, false);
        PlaceViewHolder holder = new PlaceViewHolder(v);


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

        if (placeListType == FAVORITES_LIST) {
            holder.favoriteBtn.setImageResource(R.drawable.heart_fill_red);
        } else {
            if (mFavoritesManager.isFavorited(mContext, currentItem.getPlaceId())) {
                holder.favoriteBtn.setImageResource(R.drawable.heart_fill_red);
            } else {
                holder.favoriteBtn.setImageResource(R.drawable.heart_outline_black);
            }
        }

    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }



    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        public ImageView placeIconView;
        public TextView nameTextView;
        public TextView addressTextView;
        public ImageView favoriteBtn;
        public ConstraintLayout textAndImage;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            placeIconView = itemView.findViewById(R.id.categoryIcon);
            nameTextView = itemView.findViewById(R.id.place_name);
            addressTextView = itemView.findViewById(R.id.place_address);
            favoriteBtn = itemView.findViewById(R.id.favorite_button);
            textAndImage = itemView.findViewById(R.id.place_text_image);

            textAndImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });

            View.OnClickListener clickListener;
            if (placeListType == RESULTS_LIST) {
                clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            PlaceItem placeItem = placeList.get(position);
                            mFavoritesManager.onResultsFavoriteButtonClick(mContext, placeItem, favoriteBtn);
                        }
                    }
                };
            } else {
                clickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            PlaceItem placeItem = placeList.get(position);
                            mFavoritesManager.removeFromFavorites(mContext, placeItem);
                            placeList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemMoved(position, placeList.size());

                        }
                    }
                };
            }

            favoriteBtn.setOnClickListener(clickListener);

        }
    }
}
