package com.example.tasteit_java.clases;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {

    private String name;
    private String description;
    private ArrayList<String> steps;
    private String creator;
    private String rating;
    private int img;

    public Recipe(String name, String description, int img) {
        this.name = name;
        this.description = description;
        this.img = img;
    }

    public Recipe(String name, String description, String creator, String rating, int img, ArrayList<String> steps) {
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.rating = rating;
        this.img = img;
        this.steps = steps;
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

    public ArrayList<String> getSteps() { return steps; }

    public void setSteps(ArrayList<String> steps) {
        this.steps = steps;
    }
}
