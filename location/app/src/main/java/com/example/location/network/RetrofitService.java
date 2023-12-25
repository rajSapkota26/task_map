package com.example.location.network;


import com.example.location.dto.PredictionResponse;
import com.example.location.feature.home.DirectionResponses;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitService {

    @GET("maps/api/directions/json")
    Call<DirectionResponses> getDirection(@Query("origin") String origin,
                                          @Query("destination") String destination,
                                          @Query("key") String apiKey);

    @GET("api/place/autocomplete/json?types=address&key=AIzaSyCVpZGK86mu2V1-dwVDnT3K-KzFJ75Z3IA")
    Call<PredictionResponse> loadPredictions(@Query("input") String address);
}
