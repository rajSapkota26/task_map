package com.example.location.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import timber.log.Timber;

public class NetworkModule {

    private static NetworkModule instance;
    private static final String API_BASE_URL = "https://maps.googleapis.com/";
    private static final String HTTP_DIR_CACHE = "mapper_cache";
    private static final int CACHE_SIZE = 200 * 1024 * 1024;

    public static synchronized NetworkModule getInstance() {
        if (instance == null) {
            instance = new NetworkModule();
        }
        return instance;
    }

    private Cache provideCache(Context context) {
        return new Cache(new File(context.getCacheDir(), HTTP_DIR_CACHE), CACHE_SIZE);
    }

    private HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(message -> Timber.tag("MESSAGE").d(message));
        httpLoggingInterceptor.level(HttpLoggingInterceptor.Level.BASIC);
        return httpLoggingInterceptor;
    }

    private OkHttpClient provideOkHttpClient(Context context, HttpLoggingInterceptor httpLoggingInterceptor, Cache cache) {
        return new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request original = chain.request();
                    Request request;
                    request = original.newBuilder()
                            .method(original.method(), original.body())
                            .build();

                    return chain.proceed(request);
                })
                .addInterceptor(httpLoggingInterceptor)
                .readTimeout(3, TimeUnit.MINUTES)
                .connectTimeout(3, TimeUnit.MINUTES)
                .writeTimeout(3, TimeUnit.MINUTES)
                .cache(cache)
                .build();
    }

    private Gson provideGson() {
        return new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    private GsonConverterFactory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    private Retrofit provideRetrofit(OkHttpClient okHttpClient, GsonConverterFactory gsonConverterFactory) {
        return new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .client(okHttpClient)
                .build();
    }

    public RetrofitService provideApiService(Context context) {
        GsonConverterFactory gsonConverterFactory = provideGsonConverterFactory(provideGson());
        OkHttpClient okHttpClient = provideOkHttpClient(context, provideHttpLoggingInterceptor(), provideCache(context));
        Retrofit retrofit = provideRetrofit(okHttpClient, gsonConverterFactory);
        return retrofit.create(RetrofitService.class);
    }
}

