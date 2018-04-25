package com.example.lizi.place_search;

import java.util.Date;

public class FavoritePlaceItem extends PlaceItem{
    private long timestamp;

    public FavoritePlaceItem(String imageUrl, String placeName, String addr, String placeID) {
        super(imageUrl, placeName, addr, placeID);
        timestamp = new Date().getTime();
    }

    public FavoritePlaceItem(PlaceItem place) {
        super(place.getIconUrl(), place.getName(), place.getAddress(), place.getPlaceId());
        timestamp = new Date().getTime();
    }

    @Override
    public String toString() {
        return "FavoritePlaceItem{" +
                "iconUrl='" + getIconUrl() + '\'' +
                ", name='" + getName() + '\'' +
                ", address='" + getAddress() + '\'' +
                ", placeId='" + getPlaceId() + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
