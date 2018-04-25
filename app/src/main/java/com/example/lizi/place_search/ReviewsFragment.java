package com.example.lizi.place_search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import cz.msebera.android.httpclient.Header;

public class ReviewsFragment extends Fragment {
    public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final static String TAG = "reviews";
    private JSONObject detailsJson;
    private ArrayList<ReviewItem> reviewsOnDisplay;
    private ArrayList<ReviewItem> googleReviews;
    private ArrayList<ReviewItem> yelpReviews;



    private String[] reviewTypes = {"Google reviews", "Yelp reviews"};
    private String[] orderTypes = {
            "Default order",
            "Highest rating",
            "Lowest rating",
            "Most recent",
            "Least recent"
    };


    private Spinner reviewsTypeSpinner;
    private Spinner reviewsOrderSpinner;
    private RecyclerView mRecyclerView;
    private ReviewAdapter mReviewAdapter;

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
        reviewsOnDisplay = new ArrayList<>();
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

        reviewsTypeSpinner = rootView.findViewById(R.id.reviews_type_spinner);
        reviewsOrderSpinner = rootView.findViewById(R.id.reviews_order_spinner);
        ArrayAdapter<String> typesAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, reviewTypes);
        typesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reviewsTypeSpinner.setAdapter(typesAdapter);

        ArrayAdapter<String> ordersAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, orderTypes);
        ordersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reviewsOrderSpinner.setAdapter(ordersAdapter);
        addOnItemSelectedListenerOnSpinners();

        mRecyclerView = rootView.findViewById(R.id.reviews_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mReviewAdapter = new ReviewAdapter(googleReviews);
        Log.d(TAG, "google review size: " + googleReviews.size());
        mRecyclerView.setAdapter(mReviewAdapter);

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
                Date date = null;
                try {
                    date = DATE_FORMAT.parse(review.getString("time_created"));
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

    private void addOnItemSelectedListenerOnSpinners() {
        AdapterView.OnItemSelectedListener mListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!googleReviews.isEmpty()) {
                    updateRecyclerView();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        reviewsOrderSpinner.setOnItemSelectedListener(mListener);
        reviewsTypeSpinner.setOnItemSelectedListener(mListener);

    }

    private void updateRecyclerView() {
        int reviewTypeNum = reviewsTypeSpinner.getSelectedItemPosition();
        int orderTypeNum = reviewsOrderSpinner.getSelectedItemPosition();
        if(reviewTypeNum == 0) {
            reviewsOnDisplay = new ArrayList<>(googleReviews);
        } else {
            reviewsOnDisplay = new ArrayList<>(yelpReviews);
        }
        switch (orderTypeNum) {
            case 0:
                break;
            case 1:
                Collections.sort(reviewsOnDisplay, ReviewItem.ReviewRatingComparator);
                Collections.reverse(reviewsOnDisplay);
                break;
            case 2:
                Collections.sort(reviewsOnDisplay, ReviewItem.ReviewRatingComparator);
                break;
            case 3:
                Collections.sort(reviewsOnDisplay, ReviewItem.ReviewDateComparator);
                Collections.reverse(reviewsOnDisplay);
                break;
            case 4:
                Collections.sort(reviewsOnDisplay, ReviewItem.ReviewDateComparator);
                break;
        }
        mReviewAdapter = new ReviewAdapter(reviewsOnDisplay);
        mRecyclerView.setAdapter(mReviewAdapter);
    }

}
