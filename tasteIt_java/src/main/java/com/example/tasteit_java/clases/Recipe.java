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
    private ArrayList<String> tags;
    private ArrayList<String> ingredients;
    //temp
    private int img;
    public int getImg() {
        return img;
    }
    public Recipe(String name, String description, int recipe_demo) {
        this.name = name;
        this.description = description;
        this.img = recipe_demo;
    }

    //constructor de new recipe para neo
    public Recipe(String name, String description, ArrayList<String> steps, String dateCreated, int difficulty, String creator, String image, String country, ArrayList<String> tags, ArrayList<String> ingredients) {
        this.name = name;
        this.description = description;
        this.steps = steps;
        this.dateCreated = dateCreated;
        this.difficulty = difficulty;
        this.creator = creator;
        this.image = image;
        this.country = country;
        this.tags = tags;
        this.ingredients = ingredients;
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

    public ArrayList<String> getTags() { return tags; }

    public void setTags(ArrayList<String> tags) { this.tags = tags; }

    public ArrayList<String> getIngredients() { return ingredients; }

    public void setIngredients(ArrayList<String> ingredients) { this.ingredients = ingredients; }


}
