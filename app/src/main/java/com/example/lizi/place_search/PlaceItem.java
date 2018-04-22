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

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
