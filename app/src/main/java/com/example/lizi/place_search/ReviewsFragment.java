package com.example.lizi.place_search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class ReviewsFragment extends Fragment {
    private final static String TAG = "reviews";
    private final static String YELP_URL = "http://place-search-lizi0829.us-east-2.elasticbeanstalk.com/yelp";
    private JSONObject detailsJson;
    private ArrayList<ReviewItem> googleReviews;
    private ArrayList<ReviewItem> yelpReviews;

    public ReviewsFragment() {
    }

    public static ReviewsFragment newInstance(String json) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putString("details_json", json);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);

        googleReviews = new ArrayList<>();

        String details = getArguments().getString("details_json");
        try {
            detailsJson = new JSONObject(details);
            parseGoogleReviews();

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return rootView;
    }

    private void parseGoogleReviews() throws JSONException {
        JSONArray reviewsJsonArray;
        if (detailsJson.has("reviews")) {
            reviewsJsonArray = detailsJson.getJSONArray("reviews");
            for (int i = 0; i < reviewsJsonArray.length(); i++) {
                JSONObject review = reviewsJsonArray.getJSONObject(i);
                String authorName = review.getString("author_name");
                float rating = review.getInt("rating");
                Date date = new Date(review.getLong("time") * 1000);
                String text = review.getString("text");
                String url = review.getString("author_url");
                ReviewItem reviewItem = new ReviewItem(authorName, rating, date, text, url);
                Log.d(TAG, "parseGoogleReviews:\n" + reviewItem);
                googleReviews.add(reviewItem);
            }
        } else {
            Log.e(TAG, "no google reviews");
        }
    }

//    private void fetchYelpReviews() {
//        Map<String, String> addressComponents = new HashMap<>();
//
//    }


}
