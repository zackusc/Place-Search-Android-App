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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import cz.msebera.android.httpclient.Header;

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
        yelpReviews = new ArrayList<>();

        String details = getArguments().getString("details_json");
        try {
            detailsJson = new JSONObject(details);
            fetchYelpReviews();
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

    private void fetchYelpReviews() throws JSONException {
        Map<String, String> addressComponentsMap = new HashMap<>();
        JSONArray addressComponentsJsonArray = detailsJson.getJSONArray("address_components");
        for(int i = 0; i < addressComponentsJsonArray.length(); i++) {
            JSONObject addressComponent = addressComponentsJsonArray.getJSONObject(i);
            String name = addressComponent.getString("short_name");
            String type = addressComponent.getJSONArray("types").getString(0);
            addressComponentsMap.put(type, name);
        }

        String name = detailsJson.getString("name");
        String city = addressComponentsMap.get("locality");
        String state = addressComponentsMap.get("administrative_area_level_1");
        String address1="";
        String streetNum = addressComponentsMap.get("street_number");
        String route = addressComponentsMap.get("route");
        if(streetNum != null && route != null) {
            address1 = streetNum + " " + route;
        }
        String address2 = city + ", " + state + " " + addressComponentsMap.get("postal_code");
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("address1", address1);
        params.put("address2", address2);
        params.put("city", city);
        params.put("state", state);
        params.put("country", addressComponentsMap.get("country"));

        requestYelpReviewsFromServer(params);
    }


    private void requestYelpReviewsFromServer(RequestParams params) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(YELP_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Yelp", "Success! JSON: " + response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("Yelp", "Fail " + e.toString());
                Log.d("Yelp", "Status code " + statusCode);
            }
        });
    }

}
