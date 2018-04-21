package com.example.lizi.place_search;

import android.Manifest;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class SearchTab extends Fragment {
    final int REQUEST_CODE = 123;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_tab, container, false);

        Spinner spinnerCategories = (Spinner) view.findViewById(R.id.spinnerCategories);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCategories.setAdapter(adapter);

        getCurrentLocation();

        Button searchBtn = (Button) view.findViewById(R.id.buttonSearch);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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
}
