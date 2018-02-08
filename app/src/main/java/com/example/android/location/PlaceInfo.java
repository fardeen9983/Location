package com.example.android.location;

import android.net.Uri;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by LUCIFER on 18-01-2018.
 */

public class PlaceInfo {
    private String name;
    private String phoneNo;
    private String address;
    private Uri website;
    private float rating;
    private String attributions;
    private String id;
    private int priceLevel;
    private LatLngBounds viewPort;
    private LatLng latLng;

    public PlaceInfo(Place place) {
        name = place.getName().toString();
        address = place.getAddress().toString();
        phoneNo = place.getPhoneNumber().toString();
        website = place.getWebsiteUri();
        rating=place.getRating();
        id=place.getId();
        latLng=place.getLatLng();
        viewPort=place.getViewport();
        priceLevel=place.getPriceLevel();
    }

    public PlaceInfo(String name, String phoneNo, String address, Uri website, float rating,String id, int priceLevel, LatLngBounds viewPort, LatLng latLng) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.address = address;
        this.website = website;
        this.rating = rating;

        this.id = id;
        this.priceLevel = priceLevel;
        this.viewPort = viewPort;
        this.latLng = latLng;
    }
    public PlaceInfo(){}

    public void setName(String name) {
        this.name = name;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setWebsite(Uri website) {
        this.website = website;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPriceLevel(int priceLevel) {
        this.priceLevel = priceLevel;
    }

    public void setViewPort(LatLngBounds viewPort) {
        this.viewPort = viewPort;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public String getAddress() {
        return address;
    }

    public Uri getWebsite() {
        return website;
    }

    public float getRating() {
        return rating;
    }
    public String getId() {
        return id;
    }

    public int getPriceLevel() {
        return priceLevel;
    }

    public LatLngBounds getViewPort() {
        return viewPort;
    }

    public LatLng getLatLng() {
        return latLng;
    }
}
