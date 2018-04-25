package com.example.lizi.place_search;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    private ArrayList<ReviewItem> reviewList;

    public ReviewAdapter(ArrayList<ReviewItem> reviewList) {
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewItem reviewItem = reviewList.get(position);
        holder.nameTextView.setText(reviewItem.getAuthorName());
        Date date = reviewItem.getDate();
        String formattedTime = ReviewsFragment.DATE_FORMAT.format(date);
        holder.timeTextView.setText(formattedTime);
        holder.mRatingBar.setRating(reviewItem.getRating());
        String imageUrl = reviewItem.getAuthorImageUrl();
        Picasso.get().load(imageUrl).into(holder.userImage);
        holder.reviewText.setText(reviewItem.getText());
    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        public ImageView userImage;
        public TextView nameTextView;
        public RatingBar mRatingBar;
        public TextView timeTextView;
        public TextView reviewText;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.review_author_image);
            nameTextView = itemView.findViewById(R.id.review_author_name);
            mRatingBar = itemView.findViewById(R.id.review_ratingbar);
            timeTextView = itemView.findViewById(R.id.review_time_created);
            reviewText = itemView.findViewById(R.id.review_text);
        }
    }
}
