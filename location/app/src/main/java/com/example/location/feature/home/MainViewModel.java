package com.example.location.feature.home;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.location.dto.PredictionResponse;
import com.example.location.network.FlowResponse;
import com.example.location.network.RetrofitRepo;
import com.example.location.utils.NetworkUtils;

import timber.log.Timber;

public class MainViewModel extends ViewModel {
    private final RetrofitRepo repo;
    private final Context context;


    public MainViewModel(RetrofitRepo repo,  Context context) {
        this.repo = repo;
        this.context = context;
    }

    public MutableLiveData<FlowResponse<DirectionResponses>> sendDirectionListRequest(String fromTH, String toBlj, String string) {
        MutableLiveData<FlowResponse<DirectionResponses>> mutableLiveData = new MutableLiveData<>();
        FlowResponse<DirectionResponses> flowResponse = new FlowResponse<>();
        if (!NetworkUtils.hasInternetConnection(context)) {

            return mutableLiveData;
        }
        MutableLiveData<FlowResponse<DirectionResponses>> liveData = new MutableLiveData<>();
        repo.sendDirectionListRequest(mutableLiveData,fromTH,toBlj,string);
         liveData.observeForever(data->{
                if (data.getLoading()) {
                    Timber.v(" loading.");
                }
                if (data.getInternetAvailable()) {
                    Timber.v(" no internet.");
                }

                if (data.getTokenExpired()) {
                    Timber.v(" token is expired");
                }

                if (data.getError() != null && !data.getError().isEmpty()) {
                    Timber.v(" error" + data.getError());
                }

                if (data.getMessage() != null && !data.getMessage().isEmpty()) {
                    Timber.v(" message" + data.getMessage());
                }


                if (data.getLdData() != null) {
                    mutableLiveData.postValue(flowResponse);
                    Timber.v(" mutableData" + data);
                }
            });


        return mutableLiveData;
    }

    public MutableLiveData<FlowResponse<PredictionResponse>> sendPlaceListRequest(String query) {
        MutableLiveData<FlowResponse<PredictionResponse>> mutableLiveData = new MutableLiveData<>();
        FlowResponse<PredictionResponse> flowResponse = new FlowResponse<>();
        if (!NetworkUtils.hasInternetConnection(context)) {

            return mutableLiveData;
        }
        MutableLiveData<FlowResponse<PredictionResponse>> liveData = new MutableLiveData<>();
        repo.loadPredictions(mutableLiveData,query);
        liveData.observeForever(data->{
            if (data.getLoading()) {
                Timber.v(" loading.");
            }
            if (data.getInternetAvailable()) {
                Timber.v(" no internet.");
            }

            if (data.getTokenExpired()) {
                Timber.v(" token is expired");
            }

            if (data.getError() != null && !data.getError().isEmpty()) {
                Timber.v(" error" + data.getError());
            }

            if (data.getMessage() != null && !data.getMessage().isEmpty()) {
                Timber.v(" message" + data.getMessage());
            }


            if (data.getLdData() != null) {
                mutableLiveData.postValue(flowResponse);
                Timber.v(" mutableData" + data);
            }
        });


        return mutableLiveData;
    }


}
