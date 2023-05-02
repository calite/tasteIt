package com.example.tasteit_java.ApiService;

import com.google.gson.annotations.SerializedName;

public class UserApi {
    @SerializedName("token")
    private String token;

    @SerializedName("username")
    private String username;

    @SerializedName("imgProfile")
    private String imgProfile;

    @SerializedName("biography")
    private String biography;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
