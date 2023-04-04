package com.example.tasteit_java.ApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://admiring-boyd.212-227-50-151.plesk.page/";
    private static ApiClient INSTANCE;
    private final ApiRequests service;

    public ApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiRequests.class);
    }

    public static synchronized ApiClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ApiClient();
        }
        return INSTANCE;
    }

    public ApiRequests getService() {
        return service;
    }
}




