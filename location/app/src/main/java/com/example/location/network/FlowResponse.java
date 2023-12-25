package com.example.location.network;

public class FlowResponse <T> {
    private String error = "";
    private Boolean isInternetAvailable= false;
    private Boolean isTokenExpired= false;
    private Boolean isLoading = true;
    private String message = "";
    private T ldData;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getInternetAvailable() {
        return isInternetAvailable;
    }

    public void setInternetAvailable(Boolean internetAvailable) {
        isInternetAvailable = internetAvailable;
    }

    public Boolean getTokenExpired() {
        return isTokenExpired;
    }

    public void setTokenExpired(Boolean tokenExpired) {
        isTokenExpired = tokenExpired;
    }

    public Boolean getLoading() {
        return isLoading;
    }

    public void setLoading(Boolean loading) {
        isLoading = loading;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getLdData() {
        return ldData;
    }

    public void setLdData(T ldData) {
        this.ldData = ldData;
    }
}
