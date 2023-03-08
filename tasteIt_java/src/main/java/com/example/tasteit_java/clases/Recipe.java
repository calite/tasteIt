package com.example.tasteit_java.clases;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {


    private String name;
    private String description;
    private ArrayList<String> steps;
    private String dateCreated;
    private int difficulty;
    private String creator;
    private String rating;
    private String image;
    private String country;



    private int img;

    public Recipe(String name, String description, int img) {
        this.name = name;
        this.description = description;
        this.img = img;
    }

    public Recipe(String name, String description, String creator, String rating, int img, ArrayList<String> steps, int difficulty) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.rating = rating;
        this.img = img;
        this.steps = steps;
        this.difficulty = difficulty;
    }

    //constructor de new recipe para neo
    public Recipe(String name,String description, ArrayList<String> steps,String image, String dateCreated,String country,String creator){
        this.name = name;
        this.description = description;
        this.steps = steps;
        this.image = image;
        this.dateCreated = dateCreated;
        this.country = country;
        this.creator = creator;
    }



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<String> steps) {
        this.steps = steps;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
