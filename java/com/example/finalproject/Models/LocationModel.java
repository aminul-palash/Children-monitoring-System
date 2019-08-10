package com.example.finalproject.Models;

public class LocationModel {
    private String longitude;
    private String latitude;

    public LocationModel(){}
    public LocationModel(String longitude, String latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }
}
