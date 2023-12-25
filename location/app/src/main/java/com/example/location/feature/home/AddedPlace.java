package com.example.location.feature.home;

import com.google.android.gms.maps.model.LatLng;

public class AddedPlace {
    private LatLng startDestination;
    private String name;
    private String image;

    public AddedPlace(LatLng startDestination, String name, String image) {
        this.startDestination = startDestination;
        this.name = name;
        this.image = image;
    }

    public AddedPlace() {
    }

    public LatLng getStartDestination() {
        return startDestination;
    }

    public void setStartDestination(LatLng startDestination) {
        this.startDestination = startDestination;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
