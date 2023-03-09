package com.example.tasteit_java.clases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Base64;

public class User implements Serializable {

    private String username;
    private String biography;
    private String imgProfile;


    public User(String username, String biography, String imgProfile) {
        this.username = username;
        this.biography = biography;
        this.imgProfile = imgProfile;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getImgProfile() {
        return imgProfile;
    }

    public void setImgProfile(String imgProfile) {
        this.imgProfile = imgProfile;
    }
}
