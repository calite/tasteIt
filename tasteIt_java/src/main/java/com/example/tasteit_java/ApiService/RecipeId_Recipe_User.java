package com.example.tasteit_java.ApiService;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeId_Recipe_User {
    @SerializedName("recipeId")
    private int recipeId;

    @SerializedName("recipe")
    private RecipeDetails recipeDetails;

    @SerializedName("user")
    private User user;

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public RecipeDetails getRecipeDetails() {
        return recipeDetails;
    }

    public void setRecipeDetails(RecipeDetails recipeDetails) {
        this.recipeDetails = recipeDetails;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public class RecipeDetails {
        @SerializedName("name")
        private String name;

        @SerializedName("description")
        private String description;

        @SerializedName("difficulty")
        private int difficulty;

        @SerializedName("rating")
        private float rating;

        @SerializedName("image")
        private String image;

        @SerializedName("dateCreated")
        private String dateCreated;

        @SerializedName("country")
        private String country;

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

    public class User {
        @SerializedName("token")
        private String token;

        @SerializedName("username")
        private String username;

        @SerializedName("imgProfile")
        private String imgProfile;

        @SerializedName("biography")
        private String biography;

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
}