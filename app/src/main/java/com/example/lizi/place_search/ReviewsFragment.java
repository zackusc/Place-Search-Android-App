package com.example.lizi.place_search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import cz.msebera.android.httpclient.Header;

public class ReviewsFragment extends Fragment {
    public final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private final static String TAG = "reviews";
    private JSONObject detailsJson;
    private ArrayList<ReviewItem> googleReviews;
    private ArrayList<ReviewItem> yelpReviews;

    public ReviewsFragment() {
    }

    public static ReviewsFragment newInstance(String details, String yelpReviews) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        args.putString("details_json", details);
        args.putString("yelp_reviews", yelpReviews);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reviews, container, false);

        googleReviews = new ArrayList<>();
        yelpReviews = new ArrayList<>();

        String details = getArguments().getString("details_json");
        String yelpReviewsJson = getArguments().getString("yelp_reviews");
        try {
            detailsJson = new JSONObject(details);
            parseGoogleReviews();
            parseYelpReviews(yelpReviewsJson);
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
                String imageUrl = review.getString("profile_photo_url");
                ReviewItem reviewItem = new ReviewItem(authorName, rating, date, text, url, imageUrl);
                Log.d(TAG, "parseGoogleReviews:\n" + reviewItem);
                googleReviews.add(reviewItem);
            }
        } else {
            Log.e(TAG, "no google reviews");
        }
    }

    private void parseYelpReviews(String json) throws JSONException {
        JSONObject yelpReviewsJsonObj = new JSONObject(json);
        JSONArray yelpReviewsJsonArray = yelpReviewsJsonObj.getJSONArray("reviews");
        int length = yelpReviewsJsonArray.length();
        if(length == 0) {
            Log.e(TAG, "no yelp reviews");
        } else {
            for (int i = 0; i < length; i++) {
                JSONObject review = yelpReviewsJsonArray.getJSONObject(i);
                String authorName = review.getJSONObject("user").getString("name");
                float rating = review.getInt("rating");
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                Date date = null;
                try {
                    date = dateFormat.parse(review.getString("time_created"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String text = review.getString("text");
                String url = review.getString("url");
                String imageUrl = review.getJSONObject("user").getString("image_url");
                ReviewItem reviewItem = new ReviewItem(authorName, rating, date, text, url, imageUrl);
                Log.d(TAG, "parseYelpReviews:\n" + reviewItem);
                yelpReviews.add(reviewItem);
            }
        }

    }

}
