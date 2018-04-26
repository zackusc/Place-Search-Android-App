package com.example.lizi.place_search;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;

public class FavoritesManager {
    private final static String PREFERENCE_FILE_KEY = "com.example.lizi.place_search.favorites_preference_file";
    private Gson mGson;
    public FavoritesManager() {
        mGson = new Gson();
    }

    public void onResultsFavoriteButtonClick(Context context, PlaceItem place, ImageView favoBtn) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String placeId = place.getPlaceId();
        if (sharedPref.contains(placeId)) {
            editor.remove(placeId);
            favoBtn.setImageResource(R.drawable.heart_outline_black);
            String text = place.getName() + "was removed from favorites";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        } else {
            FavoritePlaceItem favoritePlace = new FavoritePlaceItem(place);
            String json = mGson.toJson(favoritePlace);
            editor.putString(placeId, json);
            favoBtn.setImageResource(R.drawable.heart_fill_red);
            String text = place.getName() + "was added to favorites";
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }
        editor.commit();
    }

    public void removeFromFavorites(Context context, PlaceItem placeItem) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(placeItem.getPlaceId());
        editor.commit();
        String text = placeItem.getName() + " was removed from favorites";
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


    public void addToFavorites(Context context, PlaceItem place) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String placeId = place.getPlaceId();
        FavoritePlaceItem favoritePlace = new FavoritePlaceItem(place);
        String json = mGson.toJson(favoritePlace);
        editor.putString(placeId, json);
        editor.commit();
        String text = place.getName() + " was added to favorites";
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }


    public boolean isFavorited(Context context, String placeId) {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        return sharedPref.contains(placeId);
    }


    public ArrayList<FavoritePlaceItem> getAllFavoritePlaces(Context context) {
        ArrayList<FavoritePlaceItem> favorites = new ArrayList<>();
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String,?> entry : allEntries.entrySet()) {
            String json = entry.getValue().toString();
            FavoritePlaceItem favorite = mGson.fromJson(json, FavoritePlaceItem.class);
            favorites.add(favorite);
        }

        Collections.sort(favorites, new Comparator<FavoritePlaceItem>() {
            @Override
            public int compare(FavoritePlaceItem o1, FavoritePlaceItem o2) {
                return (int) (o1.getTimestamp() - o2.getTimestamp());
            }
        });



        return favorites;

    }


}
