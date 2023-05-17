package com.example.tasteit_java.ApiRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDeleteRequest {
    @SerializedName("token")
    @Expose
    private String token;

    public UserDeleteRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
