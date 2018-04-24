package com.example.lizi.place_search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    final static String TAG = "map";


    private LatLng mLatLng;
    private String placeName;



    public MapFragment() {
    }

    public static MapFragment newInstance(LatLng latLng, String name) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable("Lat_Lng", latLng);
        args.putString("place_name", name);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mLatLng = getArguments().getParcelable("Lat_Lng");
        placeName = getArguments().getString("place_name");
        Log.d(TAG, "place name: " + placeName);
        Log.d(TAG, "OnCreateView  LatLng:" + mLatLng.toString());

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.addMarker(new MarkerOptions().position(mLatLng).title(placeName));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));

    }
}
