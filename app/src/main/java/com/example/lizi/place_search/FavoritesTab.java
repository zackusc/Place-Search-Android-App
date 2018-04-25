package com.example.lizi.place_search;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class FavoritesTab extends Fragment {
    private RecyclerView favoritesRecyclerView;
    private FavoritesManager mFavoritesManager;
    private PlaceAdapter mAdapter;

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
        ArrayList<PlaceItem> places = new ArrayList<>();
        ArrayList<FavoritePlaceItem> favorites = mFavoritesManager.getAllFavoritePlaces(getActivity());
        places.addAll(favorites);
        mAdapter = new PlaceAdapter(getActivity(), places, PlaceAdapter.FAVORITES_LIST);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        favoritesRecyclerView.setAdapter(mAdapter);
    }
}
