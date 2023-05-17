package com.example.tasteit_java.ApiService;

import com.google.gson.annotations.SerializedName;
import com.google.type.DateTime;

import org.neo4j.driver.internal.value.DateTimeValue;

import java.time.OffsetDateTime;
import java.util.List;

public class RecipeApiComment {

    @SerializedName("c")
    private Comment c;

    @SerializedName("user")
    private UserApi user;

    @SerializedName("recipeId")
    private int recipeId;

    @SerializedName("recipe")
    private RecipeDetails recipeDetails;

    public Comment getComment() {
        return c;
    }

    public void setComment(Comment comment) {
        this.c = comment;
    }

    public UserApi getUser() {
        return user;
    }

    public void setUser(UserApi user) {
        this.user = user;
    }

    public RecipeDetails getRecipeDetails() {
        return recipeDetails;
    }

    public void setRecipeDetails(RecipeDetails recipeDetails) {
        this.recipeDetails = recipeDetails;
    }

    public class Comment {

        @SerializedName("comment")
        private String comment;

        @SerializedName("dateCreated")
        private String dateCreated;

        @SerializedName("rating")
        private Float rating;

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        public Float getRating() {
            return rating;
        }

        public void setRating(Float rating) {
            this.rating = rating;
        }
    }

    public class RecipeDetails {
        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("difficulty")
        private int difficulty;

        @SerializedName("image")
        private String image;

        @SerializedName("dateCreated")
        private String dateCreated;

        @SerializedName("country")
        private String country;

        @SerializedName("rating")
        private float rating;

        @SerializedName("ingredients")
        private List<String> ingredients;

        @SerializedName("tags")
        private List<String> tags;

        @SerializedName("steps")
        private List<String> steps;

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

        public int getDifficulty() {
            return difficulty;
        }

        public void setDifficulty(int difficulty) {
            this.difficulty = difficulty;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDateCreated() {
            return dateCreated;
        }

        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public List<String> getIngredients() {
            return ingredients;
        }

        public void setIngredients(List<String> ingredients) {
            this.ingredients = ingredients;
        }

        public List<String> getTags() {
            return tags;
        }

        public void setTags(List<String> tags) {
            this.tags = tags;
        }

        public List<String> getSteps() {
            return steps;
        }

        public void setSteps(List<String> steps) {
            this.steps = steps;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }
    }

}
