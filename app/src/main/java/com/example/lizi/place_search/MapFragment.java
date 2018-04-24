package com.example.lizi.place_search;

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

import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    private final static String TAG = "map";
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136)
    );


    private LatLng mLatLng;
    private String placeName;

    private Spinner travelModesSpinner;
    private AutoCompleteTextView addressAutocomplete;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    protected GeoDataClient mGeoDataClient;


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
        }
    };

    private AdapterView.OnItemSelectedListener spinnerOnItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "spinner item selected：" + parent.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.d(TAG, "Nothing selected");
        }
    };


}
