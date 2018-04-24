package com.example.lizi.place_search;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    private final static String TAG = "map";
    private final static String API_KEY = "AIzaSyDNQKni2HJlvjQLriS9ac75bvVWGmIvH_w";
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );


    private LatLng mLatLng;
    private String placeName;
    private LatLng startLatLng;
    private String startPointName;

    private Spinner travelModesSpinner;
    private AutoCompleteTextView addressAutocomplete;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    protected GeoDataClient mGeoDataClient;
    private GoogleMap mGoogleMap;
    private String travelMode;


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

        travelModesSpinner = rootView.findViewById(R.id.travel_modes_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.travel_mode_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        travelModesSpinner.setAdapter(adapter);
        travelModesSpinner.setOnItemSelectedListener(spinnerOnItemSelectedListener);

        mGeoDataClient = Places.getGeoDataClient(getActivity());
        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getActivity(), mGeoDataClient, LAT_LNG_BOUNDS, null);
        addressAutocomplete = rootView.findViewById(R.id.map_input_address);
        addressAutocomplete.setAdapter(mPlaceAutocompleteAdapter);
        addressAutocomplete.setOnItemClickListener(mAutocompleteClickListener);



        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        googleMap.addMarker(new MarkerOptions().position(mLatLng).title(placeName));
        googleMap.moveCamera(CameraUpdateFactory.zoomTo(13));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(mLatLng));

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(position);
            final String placeId = item.getPlaceId();
            Log.d(TAG, "retrieve place id from autocomplete: " + placeId);

            mGeoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
                @Override
                public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
                    if (task.isSuccessful()) {
                        PlaceBufferResponse places = task.getResult();
                        Place myPlace = places.get(0);
                        Log.d(TAG, "Place found: " + myPlace.getName());
                        startLatLng = myPlace.getLatLng();
                        startPointName = myPlace.getName().toString();
                        places.release();
                        getDirection();
                    } else {
                        Log.d(TAG, "Place not found.");
                    }
                }
            });

        }
    };

    private AdapterView.OnItemSelectedListener spinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            travelMode = parent.getItemAtPosition(position).toString().toLowerCase();
            Log.d(TAG, "selected travel mode: " + travelMode);
            if(startLatLng != null) {
                getDirection();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.d(TAG, "Nothing selected");
        }
    };


    private void getDirection() {
        GoogleDirection.withServerKey(API_KEY)
                .from(startLatLng)
                .to(mLatLng)
                .transportMode(travelMode)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        if(direction.isOK()) {
                            showRouteOnMap(direction);
                        } else {
                            Log.e(TAG, "direction status: " + direction.getStatus());
                        }
                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        Log.e(TAG, "onDirectionFailure");
                    }
                });
    }

    private void showRouteOnMap(Direction direction) {
        mGoogleMap.clear();
        mGoogleMap.addMarker(new MarkerOptions().position(mLatLng).title(placeName));
        mGoogleMap.addMarker(new MarkerOptions()
                .position(startLatLng).title(startPointName));
        Route route = direction.getRouteList().get(0);
        setCameraWithCoordinationBounds(route);
        Leg leg = route.getLegList().get(0);

        ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
        PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.BLUE);
        mGoogleMap.addPolyline(polylineOptions);
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }


}
