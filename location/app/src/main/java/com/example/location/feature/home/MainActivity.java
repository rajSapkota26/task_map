package com.example.location.feature.home;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.location.R;
import com.example.location.databinding.ActivityMainBinding;
import com.example.location.dto.PredictionResponse;
import com.example.location.dto.RoutesItem;
import com.example.location.feature.location.FusedLocationLiveData;
import com.example.location.network.FlowResponse;
import com.example.location.network.NetworkModule;
import com.example.location.network.RetrofitRepo;
import com.example.location.utils.PermissionUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchByTextRequest;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    ActivityMainBinding binding;
    GoogleMap googleMap;
    private List<AddedPlace> googleSearchesPlaces;
    private PlaceRecyclerViewAdapter adapter;
    Dialog dialog;
    PlacesClient placesClient;
    private LatLng startD = new LatLng(27.689406, 85.322668);
    private LatLng endD = new LatLng(27.734601776266842, 85.30114471996836);

    Boolean isFirstLocation = false;
    Boolean isSecondLocation = false;
    MainViewModel viewModel;
    FusedLocationLiveData liveData;
    LatLng currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        if (PermissionUtil.isLocationPermissionGranted(this)) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
            assert mapFragment != null;
            mapFragment.getMapAsync(this);
            Places.initializeWithNewPlacesApiEnabled(this, getResources().getString(R.string.map_key));
            placesClient = Places.createClient(this);
            RetrofitRepo retrofitRepo = new RetrofitRepo(NetworkModule.getInstance().provideApiService(this), this);
            viewModel = new MainViewModelFactory(retrofitRepo, this).create(MainViewModel.class);
        }
        binding.phoneBox.setOnClickListener(view -> {
            takeInputFromUser();
        });

    }


    private void takeInputFromUser() {
        dialog = new Dialog(this, R.style.mydialog);
        dialog.setContentView(R.layout.custom_place_pick_dialogue);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.show();
        RecyclerView recyclerView = dialog.findViewById(R.id.placeRecyclerView);
        EditText startLocation = dialog.findViewById(R.id.startLocation);
        EditText endLocation = dialog.findViewById(R.id.endLocation);
        TextView cancelTxt = dialog.findViewById(R.id.cancel);
        TextView doneTxt = dialog.findViewById(R.id.done);

        startLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isFirstLocation = true;
                isSecondLocation = false;
                googleSearchesPlaces = fetchPlaceSearch(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        endLocation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                isFirstLocation = false;
                isSecondLocation = true;

                googleSearchesPlaces = fetchPlaceSearch(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (googleSearchesPlaces != null) {
            adapter = new PlaceRecyclerViewAdapter(this, googleSearchesPlaces, (position, place) -> {
                if (isFirstLocation) {
                    startD = place.getStartDestination();

                }
                if (isSecondLocation) {

                    endD = place.getStartDestination();
                }
            });
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        cancelTxt.setOnClickListener(view -> {
            dialog.dismiss();
        });
        doneTxt.setOnClickListener(view -> {
            dialog.dismiss();
        });


    }


    private List<AddedPlace> fetchPlaceSearch(String placeId) {
        Timber.v("Click Here");
        List<AddedPlace> googleSearchesPlaces = new ArrayList<>();
        List<Place.Field> placeFields = getPlaceFields();
        // Define latitude and longitude coordinates of the search area.
        LatLng southWest = new LatLng(37.38816277477739, -122.08813770258874);
        LatLng northEast = new LatLng(37.39580487866437, -122.07702325966572);
        // Use the builder to create a SearchByTextRequest object.
        final SearchByTextRequest searchByTextRequest = SearchByTextRequest.builder(placeId, placeFields)
                .setMaxResultCount(10)
                .setLocationRestriction(RectangularBounds.newInstance(southWest, northEast)).build();

        // Call PlacesClient.searchByText() to perform the search.
        // Define a response handler to process the returned List of Place objects.
        placesClient.searchByText(searchByTextRequest)
                .addOnSuccessListener(response -> {
                    ArrayList<String> items = new ArrayList<String>();
                    List<Place> places = response.getPlaces();
                    for (Place p : places) {
                        Timber.v("place in res " + p.getName());
                        items.add(p.getName() + p.getAddress());
                        AddedPlace addedPlace = new AddedPlace(p.getLatLng(), p.getName(), p.getIconUrl());
                        googleSearchesPlaces.add(addedPlace);
                    }
                }).addOnFailureListener(failure -> {
                    Timber.v("place in res " + failure.toString());
                });

        return googleSearchesPlaces;
    }


    private List<Place.Field> getPlaceFields() {
        return new ArrayList<>(Arrays.asList(
                Place.Field.ADDRESS,
                Place.Field.BUSINESS_STATUS,
                Place.Field.CURRENT_OPENING_HOURS,
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.OPENING_HOURS,
                Place.Field.UTC_OFFSET
        ));

    }


    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        liveData = new FusedLocationLiveData(this);
        liveData.observe(this, location -> {
            //here set user location into map
            if (location != null) {
                currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

            }
        });
        googleMap.setMyLocationEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);


        MarkerOptions markerTh = new MarkerOptions()
                .position(startD)
                .title("Thapathali");

        MarkerOptions markerBlj = new MarkerOptions()
                .position(endD)
                .title("Balaju");
        map.addMarker(markerTh);
        map.addMarker(markerBlj);
        if (currentLocation != null) {
            MarkerOptions markerCurrent = new MarkerOptions()
                    .position(currentLocation)
                    .title("You are Here");
            map.addMarker(markerCurrent);

        }else {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(startD, 11.6f));
        }




        String fromTH = startD.latitude + "," + startD.longitude;
        String toBlj = endD.latitude + "," + endD.longitude;
        viewModel.sendDirectionListRequest(fromTH, toBlj, getString(R.string.map_key)).observe(this, response -> {
            if (response.getLdData() != null) {
                drawPolyline(response.getLdData());
            }
        });

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }


    private void drawPolyline(@NonNull DirectionResponses response) {
        if (response != null) {
            List<RoutesItem> routes = response.getRoutes();
            for (RoutesItem item : routes) {
                String shape = item.getOverviewPolyline().getPoints();
                PolylineOptions polyline = new PolylineOptions()
                        .addAll(PolyUtil.decode(shape))
                        .width(8f)
                        .color(Color.RED);
                googleMap.addPolyline(polyline);
            }

        }
    }


}