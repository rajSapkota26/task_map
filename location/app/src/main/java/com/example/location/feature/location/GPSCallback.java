package com.example.location.feature.location;

public interface GPSCallback {
    void isGPSEnabled(boolean status);
    void onGPSReady(double latReady, double lonReady);
}
