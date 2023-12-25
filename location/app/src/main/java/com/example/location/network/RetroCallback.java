package com.example.location.network;

public interface RetroCallback<T> {

    void onLoading();

    void onFinished();

    //    200...299
    void onSuccess(int code,T response);

    //400...599
    void onError(int code, String message);

    void onHttpException(String error,String errorBody);

    void onSocketTimeoutException(String error);

    void onIOException(String error);

    void OnTokenExpired(String error);

}
