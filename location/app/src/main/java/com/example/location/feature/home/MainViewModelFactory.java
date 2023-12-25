package com.example.location.feature.home;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.location.network.RetrofitRepo;

public class MainViewModelFactory implements ViewModelProvider.Factory {
    private final RetrofitRepo repo;
    private final Context context;

    public MainViewModelFactory(RetrofitRepo repo, Context context) {
        this.repo = repo;
        this.context = context;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {

        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(repo,context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");

    }
}
