package com.example.lizi.place_search;

public class PlaceItem {
    private String iconUrl;
    private String name;
    private String address;
    private String placeId;

    public PlaceItem(String imageUrl, String placeName, String addr, String placeID) {
        iconUrl = imageUrl;
        name = placeName;
        address = addr;
        placeId = placeID;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPlaceId() {
        return placeId;
    }

    @Override
    public String toString() {
        return "PlaceItem{" +
                "iconUrl='" + iconUrl + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", placeId='" + placeId + '\'' +
                '}';
    }
}
