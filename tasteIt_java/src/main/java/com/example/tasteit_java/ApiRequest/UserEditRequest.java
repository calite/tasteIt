package com.example.tasteit_java.ApiRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserEditRequest {

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("imgProfile")
    @Expose
    private String imgProfile;

    @SerializedName("biography")
    @Expose
    private String biography;

    public UserEditRequest(String token, String username, String imgProfile, String biography) {
        this.token = token;
        this.username = username;
        this.imgProfile = imgProfile;
        this.biography = biography;
    }

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
