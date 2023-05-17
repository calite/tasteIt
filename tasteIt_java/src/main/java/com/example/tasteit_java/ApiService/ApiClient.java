package com.example.tasteit_java.ApiService;

import java.io.IOException;

import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://great-dhawan.212-227-50-151.plesk.page/";
    private static ApiClient INSTANCE;
    private final ApiRequests service;

    private ApiClient(String accessToken) {
        // Agrega el interceptor
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(accessToken))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        service = retrofit.create(ApiRequests.class);
    }

    public static synchronized ApiClient getInstance(String accessToken) {
        if (INSTANCE == null) {
            INSTANCE = new ApiClient(accessToken);
        }
        return INSTANCE;
    }

    public ApiRequests getService() {
        return service;
    }

    private static class AuthInterceptor implements Interceptor {
        private final String accessToken;

        public AuthInterceptor(String authToken) {
            this.accessToken = authToken;
        }

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken);
            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        }
    }
}




