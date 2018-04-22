package com.example.lizi.place_search;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {
    private RecyclerView resultsRecyclerView;
    private ArrayList<PlaceItem> resultsList;
    private PlaceAdapter placeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Toolbar mToolbar =  (Toolbar) findViewById(R.id.toolbarResults);
        setSupportActionBar(mToolbar);

        ActionBar ab = getSupportActionBar();

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        resultsList = new ArrayList<>();
        resultsRecyclerView = findViewById(R.id.results_RecyclerView);
        resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent mIntent = getIntent();
        String resultsJSON = mIntent.getStringExtra("resultsJSON");
        Log.d("results", "received results JSON: " + resultsJSON);
        try {
            parseJSON(resultsJSON);
        } catch (JSONException e) {
            Log.e("results", "could nit parse JSON");
            e.printStackTrace();
        }

    }

    private void parseJSON(String resultsJson) throws JSONException{
        JSONObject resultsJsonObj = new JSONObject(resultsJson);
        JSONArray resultsJsonArray =  resultsJsonObj.getJSONArray("results");

        for (int i = 0; i < resultsJsonArray.length(); i++) {
            JSONObject place = resultsJsonArray.getJSONObject(i);

            String iconUrl = place.getString("icon");
            String placeName = place.getString("name");
            String address = place.getString("vicinity");
            String placeId = place.getString("place_id");

            resultsList.add(new PlaceItem(iconUrl, placeName, address, placeId));
            placeAdapter = new PlaceAdapter(resultsList);
            resultsRecyclerView.setAdapter(placeAdapter);
        }
    }



}
