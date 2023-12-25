package com.example.location.network;

import org.json.JSONObject;

import java.io.IOException;
import java.net.SocketTimeoutException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import timber.log.Timber;

public class RetroHelper<T> {

    public void enqueue(Call<T> call, final RetroCallback<T> retroCallback) {

        retroCallback.onLoading();
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
//                Timber.v("error code" + response.code());
                if (response.isSuccessful()) {
                    retroCallback.onSuccess(response.code(), response.body());
                } else if(response.code()>=400 && response.code() <=499) {

                    if (response.code() == 401){
                        retroCallback.OnTokenExpired("Your token has been expired. Please login again");
                    }
//                    retroCallback.onError(response.code(), response.message());
                    ResponseBody responseBody = response.errorBody();
                    try {
                        if (responseBody != null){
                            String res = responseBody.string();
                            Timber.v(res);
                            JSONObject jsonObject = new JSONObject(res);
                            retroCallback.onError(response.code(),jsonObject.getString("message"));
                        }

                    } catch (Exception ex) {
                        retroCallback.onError(response.code(),response.message());
                    }
                }else{
                    Timber.v("500");
                    retroCallback.onError(response.code(),"Server Error");
                }

                retroCallback.onFinished();
            }

            @Override
            public void onFailure(Call<T> call, Throwable e) {
                // there is more than just a failing request (like: no internet connection)
                if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException) e).response().errorBody();
                    try {
                        String response = responseBody.string();
                        Timber.v(response);
                        JSONObject jsonObject = new JSONObject(response);
                        retroCallback.onHttpException(jsonObject.getString("message"), response);
                    } catch (Exception ex) {
                        Timber.v(ex.getMessage());
                    }
                } else if (e instanceof SocketTimeoutException) {
                    Timber.v("timeout error"+e.getMessage());
                    retroCallback.onSocketTimeoutException("Could not send data to server due to poor connection. Please try again !");
                } else if (e instanceof IOException) {
                    Timber.v("io error"+e.getMessage());
                    retroCallback.onIOException("Couldn't connect to server.Please try Again!");
                } else if (e instanceof Exception) {
                    Timber.v("********* ********* ");
                    Timber.v("********* " + e.getMessage() + " ********* ");
                    Timber.v("********* ********* ");
                }

                retroCallback.onFinished();

            }
        });
    }

}

