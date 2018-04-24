package com.example.lizi.place_search;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SearchTab extends Fragment {
    final int REQUEST_CODE = 123;
    final String RESULTS_URL = "http://place-search-lizi0829.us-east-2.elasticbeanstalk.com/results";

    String[] categories = new String[]{
            "Default", "Airport", "Amusement Park", "Aquarium",
            "Art Gallery", "Bakery", "Bar", "Beauty Salon", "Bowling Alley",
            "Bus Station", "Cafe", "Campground", "Car Rental", "Casino", "Lodging",
            "Movie Theater", "Museum", "Night Club", "Park", "Parking",
            "Restaurant", "Shopping Mall", "Stadium", "Subway Station",
            "Taxi Stand", "Train Station", "Transit Station", "Travel Agency",
            "Zoo"
    };

    LocationManager locationManager;
    LocationListener locationListener;

    double currentLat;
    double currentLon;

    RadioGroup radioLocationGroup;
    EditText keywordET;
    Spinner spinnerCategories;
    EditText distanceET;
    AutoCompleteTextView addressET;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    protected GeoDataClient mGeoDataClient;

    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_tab, container, false);


        keywordET = (EditText) view.findViewById(R.id.keyword);
        distanceET = (EditText) view.findViewById(R.id.distance);
        addressET = (AutoCompleteTextView) view.findViewById(R.id.inputAddress);
        radioLocationGroup = (RadioGroup) view.findViewById(R.id.radioLocation);
        spinnerCategories = (Spinner) view.findViewById(R.id.spinnerCategories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);

        mGeoDataClient = Places.getGeoDataClient(getActivity());
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGeoDataClient, LAT_LNG_BOUNDS, null);
        addressET.setAdapter(mPlaceAutocompleteAdapter);

        getCurrentLocation();
        addListenerOnRadioButtons();
        addListenerOnSearchButton(view);

        return view;
    }

    private void getCurrentLocation() {
        Log.d("search", "getting current location");
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("search", "onLocationChanged");
                currentLat = location.getLatitude();
                currentLon = location.getLongitude();
                Log.d("search", "longitue is: " + currentLat);
                Log.d("search", "latitude is: " + currentLon);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        //check if got the permission to access fine location
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // call requestPermissions here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission.
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1000, locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("search", "onPermissionResult(): Permission granted!");
                getCurrentLocation();
            } else {
                Log.d("search", "onPermissionResult(): Permission denied!");
            }
        }
    }


    private void addListenerOnSearchButton(View view) {
        Button searchBtn = (Button) view.findViewById(R.id.buttonSearch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = keywordET.getText().toString();
                Log.d("search", "keyword: " + keyword);
                String category = String.valueOf(spinnerCategories.getSelectedItem());
                Log.d("search", "category: " + category);
                String distance = distanceET.getText().toString();
                distance = distance.isEmpty() ? "10" : distance;
                Log.d("search", "distance: " + distance);


                RequestParams params = new RequestParams();
                params.put("keyword", keyword);
                params.put("distance", distance);
                params.put("category", category);

                String address;
                if(radioLocationGroup.getCheckedRadioButtonId() == R.id.radioOtherLocation) {
                    address = addressET.getText().toString();
                    Log.d("search", "address: " + address);
                    params.put("address", address);
                } else {
                    params.put("lat", currentLat);
                    params.put("lng", currentLon);
                }
//                Log.d("search", "params: " + params.toString());
                getResultsAndSwitchView(params);

            }
        });
    }

    private void addListenerOnRadioButtons() {
        radioLocationGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId == R.id.radioCurrentLocation) {
                    Log.d("search", "current location clicked");
                    addressET.setEnabled(false);
                } else {
                    Log.d("search", "other location clicked");
                    addressET.setEnabled(true);
                }
            }
        });
    }

    private void getResultsAndSwitchView(RequestParams params) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Fetching results");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(RESULTS_URL, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Search", "Success! JSON: " + response.toString());
                progressDialog.dismiss();
                String resultsJSON = response.toString();
                Intent myIntent = new Intent(getActivity(), SearchResultsActivity.class);
                myIntent.putExtra("resultsJSON", resultsJSON);
                startActivity(myIntent);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject response) {
                Log.d("search", "Fail " + e.toString());
                Log.d("search", "Status code " + statusCode);
            }
        });

    }


}
