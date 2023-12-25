package com.example.location.feature.location;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.location.utils.NetworkUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class FusedLocationLiveData extends LiveData<Location> {

    private Context context;
    private LocationManager locationManager;
    private boolean isNetWorkProviderEnabled = false;
    private boolean isGPSProviderEnabled = false;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;

    private final long UPDATE_INTERVAL = (10 * 1000);  /* 10 secs */
    private final long FASTEST_INTERVAL = (2 * 1000); /* 2 sec */

    private SettingsClient settingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private SettingsCallback settingsCallback;

    public interface SettingsCallback {
        void onSettingResolution(Exception exception);
    }

    public FusedLocationLiveData(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        settingsClient = LocationServices.getSettingsClient(context);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        checkProviderEnabled();
    }

    public boolean checkProviderEnabled() {
        isNetWorkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        isGPSProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return isNetWorkProviderEnabled || isGPSProviderEnabled;
    }

    private LocationCallback locationCallback;

    @SuppressLint("MissingPermission")
    @Override
    protected void onInactive() {
        super.onInactive();
        if (locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onActive() {
        super.onActive();
        refreshProvider();
    }

    @SuppressLint("MissingPermission")
    private void refreshProvider() {
        checkProviderEnabled();

        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            setValue(location);
                            break;
                        }
                    }
                }
            };
        }

        mLocationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(UPDATE_INTERVAL)
                .setMaxUpdateDelayMillis(FASTEST_INTERVAL)
                .build();


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        settingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        // Handle success
                    }
                })
                .addOnFailureListener(e -> {
                    if (settingsCallback != null) {
                        settingsCallback.onSettingResolution(e);
                    }
                });

        if (isNetWorkProviderEnabled && isGPSProviderEnabled) {
            if (NetworkUtils.hasInternetConnection(context)) {
                fusedLocationProviderClient.requestLocationUpdates(
                        mLocationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                );
            } else {
                fusedLocationProviderClient.requestLocationUpdates(
                        mLocationRequest,
                        locationCallback,
                        Looper.getMainLooper()
                );
            }
        } else if (isNetWorkProviderEnabled) {
            fusedLocationProviderClient.requestLocationUpdates(
                    mLocationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        } else if (isGPSProviderEnabled) {
            fusedLocationProviderClient.requestLocationUpdates(
                    mLocationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );
        } else {
            setValue(null);
        }
    }



















    public SettingsCallback getSettingsCallback() {
        return settingsCallback;
    }

    public void setSettingsCallback(SettingsCallback settingsCallback) {
        this.settingsCallback = settingsCallback;
    }


}
