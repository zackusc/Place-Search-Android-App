package com.example.lizi.place_search;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FavoritesTab extends Fragment implements PlaceAdapter.OnItemClickListener{
    private RecyclerView favoritesRecyclerView;
    private FavoritesManager mFavoritesManager;
    private PlaceAdapter mAdapter;
    private ArrayList<PlaceItem> favoritesList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites_tab, container, false);
        favoritesRecyclerView = view.findViewById(R.id.favorites_recyclerView);
        mFavoritesManager = new FavoritesManager();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        favoritesList = new ArrayList<>();
        ArrayList<FavoritePlaceItem> favorites = mFavoritesManager.getAllFavoritePlaces(getActivity());
        favoritesList.addAll(favorites);
        mAdapter = new PlaceAdapter(getActivity(), favoritesList, PlaceAdapter.FAVORITES_LIST);
        mAdapter.setOnItemClickListener(this);
        favoritesRecyclerView.setLayoutManager(new WrapContentLinearLayoutManager(getActivity()));
        favoritesRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(int position) {
        Intent detailsIntent = new Intent(getActivity(), DetailsActivity.class);
        PlaceItem placeItem = favoritesList.get(position);
        String placeId = placeItem.getPlaceId();
        Log.d("results", "no." + position + " place is clicked!");
        Log.d("results", "place_id: " + placeId);
        detailsIntent.putExtra("place_id", placeId);
        detailsIntent.putExtra("place_name", placeItem.getName());
        startActivity(detailsIntent);
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
