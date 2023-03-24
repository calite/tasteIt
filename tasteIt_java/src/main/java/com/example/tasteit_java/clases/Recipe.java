package com.example.tasteit_java.clases;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Parcelable {
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
    private int id;
    //temp
    private int img;

    protected Recipe(Parcel in) {
        name = in.readString();
        description = in.readString();
        steps = in.createStringArrayList();
        dateCreated = in.readString();
        difficulty = in.readInt();
        creator = in.readString();
        rating = in.readString();
        image = in.readString();
        country = in.readString();
        tags = in.createStringArrayList();
        ingredients = in.createStringArrayList();
        id = in.readInt();
        img = in.readInt();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    public int getImg() {
        return img;
    }
    public Recipe(String name, String description, int recipe_demo) {
        this.name = name;
        this.description = description;
        this.img = recipe_demo;
    }

    //constructor de new recipe para neo
    public Recipe(String name, String description, ArrayList<String> steps, String dateCreated, int difficulty, String creator, String image, String country, ArrayList<String> tags, ArrayList<String> ingredients, int id) {
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
        this.id = id;
    }

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

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(description);
        parcel.writeStringList(steps);
        parcel.writeString(dateCreated);
        parcel.writeInt(difficulty);
        parcel.writeString(creator);
        parcel.writeString(rating);
        parcel.writeString(image);
        parcel.writeString(country);
        parcel.writeStringList(tags);
        parcel.writeStringList(ingredients);
        parcel.writeInt(id);
        parcel.writeInt(img);
    }
}
