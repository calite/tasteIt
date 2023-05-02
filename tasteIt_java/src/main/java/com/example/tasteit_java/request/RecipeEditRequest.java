package com.example.tasteit_java.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeEditRequest {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("difficulty")
    @Expose
    private int difficulty;

    @SerializedName("ingredients")
    @Expose
    private String ingredients;

    @SerializedName("steps")
    @Expose
    private String steps;

    @SerializedName("tags")
    @Expose
    private String tags;

    @SerializedName("rid")
    @Expose
    private int rid;

    public RecipeEditRequest(int rid, String name, String description, String country, String image, int difficulty, String ingredients, String steps, String tags) {
        this.rid = rid;
        this.name = name;
        this.description = description;
        this.country = country;
        this.image = image;
        this.difficulty = difficulty;
        this.ingredients = ingredients;
        this.steps = steps;
        this.tags = tags;
    }

    public RecipeEditRequest() {
    }

    public int getRecipeId() {
        return rid;
    }

    public void setRecipeId(int rid) {
        this.rid = rid;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}