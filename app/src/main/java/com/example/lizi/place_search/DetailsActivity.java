package com.example.lizi.place_search;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class DetailsActivity extends AppCompatActivity {
    final static String TAG = "details";
    final String REQUEST_DETAILS_URL = "http://place-search-lizi0829.us-east-2.elasticbeanstalk.com/details";
    private final static String YELP_URL = "http://place-search-lizi0829.us-east-2.elasticbeanstalk.com/yelp";

    //    protected GeoDataClient mGeoDataClient;
    String placeName;
    private String placeId;
    private String detailsJson;
    private JSONObject detailsJsonObj;
    private String address;
    private String twitterUrl;
    private String yelpReviewsJson;

    private ProgressDialog progressDialog;
    Toolbar toolbar;
    private Menu menu;
    private FavoritesManager mFavoritesManager;
    private boolean isFavorited;
    private PlaceItem mPlaceItem;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        placeId = intent.getStringExtra("place_id");
        placeName = intent.getStringExtra("place_name");
        String json = intent.getStringExtra("place_item");
        mPlaceItem = new Gson().fromJson(json, PlaceItem.class);
        Log.d("details", "onCreate: placeId: " + placeId);

        getPlaceDetails();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(placeName);
        addListenerOnNavigation();

        mFavoritesManager = new FavoritesManager();
        isFavorited = mFavoritesManager.isFavorited(this, placeId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);

        if (isFavorited) {
            menu.getItem(1).setIcon(R.drawable.heart_fill_white);
        } else {
            menu.getItem(1).setIcon(R.drawable.heart_outline_white);
        }

        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            if(isFavorited) {
                mFavoritesManager.removeFromFavorites(this, mPlaceItem);
                item.setIcon(R.drawable.heart_outline_white);
            } else {
                mFavoritesManager.addToFavorites(this, mPlaceItem);
                item.setIcon(R.drawable.heart_fill_white);
            }
            isFavorited = !isFavorited;
            return true;
        }
        if (id == R.id.action_share) {
            Intent twitterIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(twitterUrl));
            if (twitterIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(twitterIntent);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    Log.d("details", "send details to info: " + detailsJson);
                    return InfoFragment.newInstance(detailsJson);
                case 1:
                    return PhotosFragment.newInstance(placeId);
                case 2:
                    try {
                        JSONObject location = detailsJsonObj.getJSONObject("geometry").getJSONObject("location");
                        double lat = location.getDouble("lat");
                        double lng = location.getDouble("lng");
                        LatLng latLng = new LatLng(lat, lng);
                        Log.d(TAG, "send latLng to map: " + latLng.toString());
                        return MapFragment.newInstance(latLng, placeName);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                case 3:
                    return ReviewsFragment.newInstance(detailsJsonObj.toString(), yelpReviewsJson);
            }
            return null;

        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }
    }

    private void getPlaceDetails() {
        showProgressDialog();
        Log.d("fetch details", "placeId: " + placeId);

        RequestParams params = new RequestParams();
        params.put("place_id", placeId);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(REQUEST_DETAILS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    detailsJsonObj = response.getJSONObject("result");
                    fetchYelpReviews();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                detailsJson = response.toString();
                Log.d("Fetch details", "Success! JSON: " + detailsJson);
                createTwitterContent();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("Fetch details", "Fail " + e.toString());
                Log.d("Fetch details", "Status code " + statusCode);
            }
        });

    }

    private void createTwitterContent() {
        try {
            address = detailsJsonObj.getString("formatted_address");
            String website;
            if (detailsJsonObj.has("website")) {
                website = detailsJsonObj.getString("website");
            } else {
                website = detailsJsonObj.getString("url");
            }

            twitterUrl = "https://twitter.com/intent/tweet?";
            String text = "Check out " + placeName + " located at " + address + "\nWebsite:";
            text = URLEncoder.encode(text, "UTF-8");
            website = URLEncoder.encode(website, "UTF-8");
            twitterUrl += "text=" + text + "&url=" + website + "&hashtags=TravelAndEntertainmentSearch";
            Log.d("details", "twitter url: " + twitterUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void addListenerOnNavigation() {
        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void fetchYelpReviews() throws JSONException {
        Map<String, String> addressComponentsMap = new HashMap<>();
        JSONArray addressComponentsJsonArray = detailsJsonObj.getJSONArray("address_components");
        for(int i = 0; i < addressComponentsJsonArray.length(); i++) {
            JSONObject addressComponent = addressComponentsJsonArray.getJSONObject(i);
            String name = addressComponent.getString("short_name");
            String type = addressComponent.getJSONArray("types").getString(0);
            addressComponentsMap.put(type, name);
        }

        String name = detailsJsonObj.getString("name");
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
                yelpReviewsJson = response.toString();
                updateUI();
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("Yelp", "Fail " + e.toString());
                Log.d("Yelp", "Status code " + statusCode);
            }
        });
    }

    private void showProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching details");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    private void updateUI() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

    }


}
