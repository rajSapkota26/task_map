package com.example.location.network;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;

import com.example.location.dto.PredictionResponse;
import com.example.location.feature.home.DirectionResponses;
import com.example.location.utils.NetworkUtils;

import retrofit2.Call;

public class RetrofitRepo {
    private final RetrofitService retrofitService;
    private final Context context;

    public RetrofitRepo(RetrofitService retrofitService, Context context) {
        this.retrofitService = retrofitService;
        this.context = context;
    }

    public void sendDirectionListRequest(MutableLiveData<FlowResponse<DirectionResponses>> liveData,String start,String end,String key) {
        RetroHelper<DirectionResponses> retroHelper = new RetroHelper<>();
        Call<DirectionResponses> call = retrofitService.getDirection(start,end,key);
        FlowResponse<DirectionResponses> flowResponse = new FlowResponse<>();

        if (!NetworkUtils.hasInternetConnection(context)) {
            flowResponse.setInternetAvailable(true);
            liveData.postValue(flowResponse);
        }

        retroHelper.enqueue(call, new RetroCallback() {
            @Override
            public void onLoading() {
                flowResponse.setLoading(true);
                liveData.postValue(flowResponse);

            }

            @Override
            public void onFinished() {
                flowResponse.setLoading(false);
                liveData.postValue(flowResponse);
            }

            @Override
            public void onSuccess(int code, Object response) {
                flowResponse.setLdData((DirectionResponses) response);
                liveData.postValue(flowResponse);


            }

            @Override
            public void onError(int code, String message) {
                flowResponse.setError(message);
                liveData.postValue(flowResponse);

            }

            @Override
            public void onHttpException(String error, String errorBody) {
                flowResponse.setError(error);
                liveData.postValue(flowResponse);
            }

            @Override
            public void onSocketTimeoutException(String error) {
                flowResponse.setError(error);
                liveData.postValue(flowResponse);
            }

            @Override
            public void onIOException(String error) {
                flowResponse.setError(error);
                liveData.postValue(flowResponse);
            }

            @Override
            public void OnTokenExpired(String error) {
                flowResponse.setTokenExpired(true);
                flowResponse.setMessage(error);
                liveData.postValue(flowResponse);
            }
        });

    }

    public void loadPredictions(MutableLiveData<FlowResponse<PredictionResponse>> liveData,String start) {
        RetroHelper<PredictionResponse> retroHelper = new RetroHelper<>();
        Call<PredictionResponse> call = retrofitService.loadPredictions(start);
        FlowResponse<PredictionResponse> flowResponse = new FlowResponse<>();

        if (!NetworkUtils.hasInternetConnection(context)) {
            flowResponse.setInternetAvailable(true);
            liveData.postValue(flowResponse);
        }

        retroHelper.enqueue(call, new RetroCallback() {
            @Override
            public void onLoading() {
                flowResponse.setLoading(true);
                liveData.postValue(flowResponse);

            }

            @Override
            public void onFinished() {
                flowResponse.setLoading(false);
                liveData.postValue(flowResponse);
            }

            @Override
            public void onSuccess(int code, Object response) {
                flowResponse.setLdData((PredictionResponse) response);
                liveData.postValue(flowResponse);


            }

            @Override
            public void onError(int code, String message) {
                flowResponse.setError(message);
                liveData.postValue(flowResponse);

            }

            @Override
            public void onHttpException(String error, String errorBody) {
                flowResponse.setError(error);
                liveData.postValue(flowResponse);
            }

            @Override
            public void onSocketTimeoutException(String error) {
                flowResponse.setError(error);
                liveData.postValue(flowResponse);
            }

            @Override
            public void onIOException(String error) {
                flowResponse.setError(error);
                liveData.postValue(flowResponse);
            }

            @Override
            public void OnTokenExpired(String error) {
                flowResponse.setTokenExpired(true);
                flowResponse.setMessage(error);
                liveData.postValue(flowResponse);
            }
        });

    }

}
