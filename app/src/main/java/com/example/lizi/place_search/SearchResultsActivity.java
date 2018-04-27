package com.example.lizi.place_search;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchResultsActivity extends AppCompatActivity implements PlaceAdapter.OnItemClickListener{
    final String NEXT_PAGE_URL = "http://place-search-lizi0829.us-east-2.elasticbeanstalk.com/results/nextpage";
    private final static String TAG = "results";
    private RecyclerView resultsRecyclerView;
    private ArrayList<PlaceItem> resultsList;
    private PlaceAdapter placeAdapter;
    private String nextPageToken;
    private ArrayList<ArrayList<PlaceItem>> pages;

    private int currentPageNum = 1;
    private int maxPageNum = 1;

    private Toolbar mToolbar;
    private Button nextPageBtn;
    private Button prevPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        resultsList = new ArrayList<>();
        Intent mIntent = getIntent();
        String resultsJSON = mIntent.getStringExtra("resultsJSON");
        Log.d("results ", "onCreate: received results JSON: " + resultsJSON);


        mToolbar =  (Toolbar) findViewById(R.id.toolbarResults);
        setSupportActionBar(mToolbar);
        addListenerOnNavigation();
        addListenerOnButtonNextPage();
        addListenerOnButtonPrevPage();

        resultsRecyclerView = findViewById(R.id.results_RecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        pages = new ArrayList<>();

        try {
            JSONObject resultsJsonObj = new JSONObject(resultsJSON);
            parseJSON(resultsJsonObj);
        } catch (JSONException e) {
            Log.e("results", "could not parse JSON");
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (placeAdapter != null) {
            placeAdapter.notifyDataSetChanged();
        }
    }

    private void parseJSON(JSONObject jsonObj) throws JSONException{
        JSONArray resultsJsonArray =  jsonObj.getJSONArray("results");

        if (resultsJsonArray.length() == 0) {
            findViewById(R.id.no_result).setVisibility(View.VISIBLE);
            findViewById(R.id.paginationButtons).setVisibility(View.GONE);
            return;
        }


        if(jsonObj.has("next_page_token")) {
            nextPageToken = jsonObj.getString("next_page_token");
        } else {
            nextPageBtn.setEnabled(false);
            maxPageNum = currentPageNum;
        }

        resultsList.clear();

        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject place = resultsJsonArray.getJSONObject(i);

            String iconUrl = place.getString("icon");
            String placeName = place.getString("name");
            String address = place.getString("vicinity");
            String placeId = place.getString("place_id");

            resultsList.add(new PlaceItem(iconUrl, placeName, address, placeId));
        }

        Log.d("results", "resultsList length: " + resultsList.size());
        placeAdapter = new PlaceAdapter(this, resultsList, PlaceAdapter.RESULTS_LIST);
        placeAdapter.setOnItemClickListener(SearchResultsActivity.this);
//        addRecyclerViewOnClickListener();
        Log.d("results", "current page number: " + currentPageNum);
        resultsRecyclerView.setAdapter(placeAdapter);

        ArrayList<PlaceItem> copy = new ArrayList<>(resultsList);
        if(currentPageNum > pages.size()) {
            pages.add(copy);
        }
    }

    private void addListenerOnNavigation() {
        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addListenerOnButtonNextPage() {
        nextPageBtn = findViewById(R.id.buttonNextPage);
        nextPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNextPageResults();
            }
        });
    }

    private void addListenerOnButtonPrevPage() {
        prevPageBtn = findViewById(R.id.buttonPrevPage);
        prevPageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentPageNum--;
                Log.d("prev", "current page number: " + currentPageNum);
                placeAdapter = new PlaceAdapter(SearchResultsActivity.this, pages.get(currentPageNum - 1), PlaceAdapter.RESULTS_LIST);
//                addRecyclerViewOnClickListener();
                placeAdapter.setOnItemClickListener(SearchResultsActivity.this);
                resultsRecyclerView.setAdapter(placeAdapter);
                nextPageBtn.setEnabled(true);
                if(currentPageNum == 1) {
                    prevPageBtn.setEnabled(false);
                }
            }
        });
    }


    private void getNextPageResults() {
        currentPageNum++;
        prevPageBtn.setEnabled(true);

        Log.d("Next", "current page number: " + currentPageNum);

        if(currentPageNum <= pages.size()) {
            placeAdapter = new PlaceAdapter(SearchResultsActivity.this,pages.get(currentPageNum - 1), PlaceAdapter.RESULTS_LIST);
//            addRecyclerViewOnClickListener();
            placeAdapter.setOnItemClickListener(SearchResultsActivity.this);
            resultsRecyclerView.setAdapter(placeAdapter);
            if(currentPageNum == maxPageNum) {
                nextPageBtn.setEnabled(false);
            }
        } else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Fetching next page");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.show();
            progressDialog.setCancelable(false);

            RequestParams params = new RequestParams();
            params.put("pagetoken", nextPageToken);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(NEXT_PAGE_URL, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    Log.d("Next page", "Success! JSON: " + response.toString());
                    progressDialog.dismiss();
                    try {
                        parseJSON(response);
                    } catch (JSONException e) {
                        Log.e("Next page", "could not parse JSON");
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                    Log.d("Next page", "Fail " + e.toString());
                    Log.d("Next page", "Status code " + statusCode);
                }
            });
        }
    }

    @Override
    public void onItemClick(int position) {
        ConnectivityManager cm =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(this, "Not network connection, try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent detailsIntent = new Intent(this, DetailsActivity.class);
        PlaceItem placeItem = resultsList.get(position);
        String placeId = placeItem.getPlaceId();
        Log.d("results", "no." + position + " place is clicked!");
        Log.d("results", "place_id: " + placeId);
        Gson gson = new Gson();
        String json = gson.toJson(placeItem);
        detailsIntent.putExtra("place_id", placeId);
        detailsIntent.putExtra("place_name", placeItem.getName());
        detailsIntent.putExtra("place_item", json);
        startActivity(detailsIntent);
    }

}
