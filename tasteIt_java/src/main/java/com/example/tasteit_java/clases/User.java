package com.example.tasteit_java.clases;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable  {

    private String username;
    private String biography;
    private String imgProfile;
    private ArrayList<Recipe> userRecipes;
    private HashMap<String, String> userComments;


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

    public ArrayList<Recipe> getUserRecipes() {
        return userRecipes;
    }

    public void setUserRecipes(ArrayList<Recipe> userRecipes) {
        this.userRecipes = userRecipes;
    }

    public HashMap<String, String> getUserComments() {
        return userComments;
    }

    public void setUserComments(HashMap<String, String> userComments) {
        this.userComments = userComments;
    }
}
