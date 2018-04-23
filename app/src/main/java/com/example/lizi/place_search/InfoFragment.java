package com.example.lizi.place_search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

public class InfoFragment extends Fragment {

    private String detailsJson;
    private TextView addressTextView;
    private TextView phoneNumTextView;
    private TextView priceTextView;
    private TextView googlePageTextView;
    private TextView webTextView;


    public InfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment InfoFragment.
     */
    public static InfoFragment newInstance(String detailsJson) {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString("details", detailsJson);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            detailsJson = getArguments().getString("details");
            Log.d("info", "onCreate details json: " + detailsJson);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_info, container, false);

        addressTextView = rootView.findViewById(R.id.info_address);
        phoneNumTextView = rootView.findViewById(R.id.info_phone_number);
        priceTextView = rootView.findViewById(R.id.info_price_level);
        googlePageTextView = rootView.findViewById(R.id.info_google_page);
        webTextView = rootView.findViewById(R.id.info_website);

        try {
            parseJSON();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void parseJSON() throws JSONException{
        JSONObject jsonObject = new JSONObject(detailsJson);
        JSONObject detailsJsonObj = jsonObject.getJSONObject("result");
        String address = detailsJsonObj.getString("formatted_address");
        addressTextView.setText(address);

        if (detailsJsonObj.has("formatted_phone_number")) {
            String phoneNum = detailsJsonObj.getString("formatted_phone_number");
            phoneNumTextView.setText(phoneNum);
        }
        if (detailsJsonObj.has("price_level")) {
            int priceLevel = detailsJsonObj.getInt("price_level");
            priceTextView.setText(getPriceLevelNotation(priceLevel));
        }
        if (detailsJsonObj.has("url")) {
            String googleUrl = detailsJsonObj.getString("url");
            googlePageTextView.setText(googleUrl);
        }
        if (detailsJsonObj.has("website")) {
            String website = detailsJsonObj.getString("website");
            webTextView.setText(website);
        }


    }

    private String getPriceLevelNotation(int n) {
        String dollarNotation = "";
        for (int i = 0; i < n; i++) {
            dollarNotation += "$";
        }
        return dollarNotation;
    }


}
