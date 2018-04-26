package com.example.lizi.place_search;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder> {
    private List<Bitmap> photoList;

    public PhotoAdapter(List<Bitmap> photoList) {
        this.photoList = photoList;
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.photo_item, parent, false);
        PhotoViewHolder holder = new PhotoViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder holder, int position) {
        Bitmap photo = photoList.get(position);
        holder.photoView.setImageBitmap(photo);
    }
    @Override
    public int getItemCount() {
        return photoList.size();
    }

    public class PhotoViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoView;

        public PhotoViewHolder(View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.place_photo);
        }
    }

}
