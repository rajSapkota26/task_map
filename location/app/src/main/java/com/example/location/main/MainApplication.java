package com.example.location.main;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import timber.log.Timber;

public class MainApplication extends Application {
    private static MainApplication instance;
    @Override
    public void onCreate() {
        initTimberForLog();
        super.onCreate();
        if (instance == null) {
            instance = this;
        }
    }
    public static MainApplication getInstance() {
        return instance;
    }

    public static boolean hasNetwork() {
        return instance.isNetworkConnected();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    private void initTimberForLog() {
//        if (BuildConfig.DEBUG) {
        // DebugTree has all usual logging functionality
        Timber.plant(new Timber.DebugTree() {
            @Nullable
            @Override
            protected String createStackElementTag(@NonNull StackTraceElement element) {
                return String.format("[L:%s] [%s:%s]",
                        element.getLineNumber(),
                        element.getClassName(),
                        element.getMethodName());

            }

            @Override
            protected void log(int priority, String tag, @NonNull String message, Throwable t) {

                super.log(priority, "test", message, t);
            }

        });

    }
}
