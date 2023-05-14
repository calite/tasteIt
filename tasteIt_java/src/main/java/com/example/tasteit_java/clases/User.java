package com.example.tasteit_java.clases;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class User implements Serializable, Comparable<User>  {

    private String username;
    private String biography;
    private String imgProfile;
    private ArrayList<Recipe> userRecipes;
    private ArrayList<Comment> userComments;
    private String uid;

    public User(String username, String biography, String imgProfile) {
        this.username = username;
        this.biography = biography;
        this.imgProfile = imgProfile;
    }

    public User(String username, String biography, String imgProfile, String uid) {
        this.username = username;
        this.biography = biography;
        this.imgProfile = imgProfile;
        this.uid = uid;
    }

    public User(String username, String uid, String biography, String imgProfile, ArrayList<Recipe> userRecipes, ArrayList<Comment> userComments) {
        this.username = username;
        this.biography = biography;
        this.imgProfile = imgProfile;
        this.userRecipes = userRecipes;
        this.userComments = userComments;
        this.uid = uid;
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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<Comment> getUserComments() {
        return userComments;
    }

    public void setUserComments(ArrayList<Comment> userComments) {
        this.userComments = userComments;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof User) {
            return getUid() == ((User) obj).getUid();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public int compareTo(User r){
        if(r.getUsername().equals(this.username)){
            return 0;
        }else {
            return 1;
        }
    }
}
