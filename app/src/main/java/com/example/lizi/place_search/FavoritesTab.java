package com.example.lizi.place_search;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

public class FavoritesTab extends Fragment implements PlaceAdapter.OnItemClickListener, PlaceAdapter.OnFavoriteButtonClickListener{
    private final static String TAG = "Favorites";

    private RecyclerView favoritesRecyclerView;
    private FavoritesManager mFavoritesManager;
    private PlaceAdapter mAdapter;
    private ArrayList<PlaceItem> favoritesList;
    private TextView noFavorites;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_tab, container, false);
        favoritesRecyclerView = view.findViewById(R.id.favorites_recyclerView);
        mFavoritesManager = new FavoritesManager();
        noFavorites = view.findViewById(R.id.no_favorites);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        favoritesList = new ArrayList<>();
        ArrayList<FavoritePlaceItem> favorites = mFavoritesManager.getAllFavoritePlaces(getActivity());
        if (favorites.size() == 0) {
            noFavorites.setVisibility(View.VISIBLE);
            Log.d(TAG, "noavorites set visible in onResume");
            return;
        }
        noFavorites.setVisibility(View.GONE);
        favoritesList.addAll(favorites);
        mAdapter = new PlaceAdapter(getActivity(), favoritesList, PlaceAdapter.FAVORITES_LIST);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnFavoriteButtonClickListener(this);
        favoritesRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        favoritesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        ConnectivityManager cm =
                (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (!isConnected) {
            Toast.makeText(getActivity(), "Not network connection, try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
        PlaceItem placeItem = favoritesList.get(position);
        String placeId = placeItem.getPlaceId();
        Log.d("results", "no." + position + " place is clicked!");
        Log.d("results", "place_id: " + placeId);
        detailsIntent.putExtra("place_id", placeId);
        detailsIntent.putExtra("place_name", placeItem.getName());
        Gson gson = new Gson();
        String json = gson.toJson(placeItem);
        detailsIntent.putExtra("place_item", json);
        startActivity(detailsIntent);
    }

    @Override
    public void onFavoriteButtonClick(int position) {
        PlaceItem place = favoritesList.get(position);
        Log.d(TAG, "placed removed:" + place);
        mFavoritesManager.removeFromFavorites(getActivity(), place);
        mAdapter.removeItemAt(position);
        if (mAdapter.getItemCount() == 0) {
//        if (favoritesList.size() == 0) { //this check is also doable
            noFavorites.setVisibility(View.VISIBLE);
            Log.d(TAG, "noFavorites set visible in onFavoriteButtonClick");
        }
    }


    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("Error", "IndexOutOfBoundsException in RecyclerView happens");
            }
        }
    }
}
