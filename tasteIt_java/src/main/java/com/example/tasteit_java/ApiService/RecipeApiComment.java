package com.example.tasteit_java.ApiService;

import com.google.gson.annotations.SerializedName;

public class RecipeApiComment {

    @SerializedName("comment")
    private Comment comment;

    @SerializedName("user")
    private UserApi user;

    @SerializedName("recipeId")
    private int recipeId;

    @SerializedName("recipe")
    private RecipeId_Recipe_User.RecipeDetails recipeDetails;

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public UserApi getUser() {
        return user;
    }

    public void setUser(UserApi user) {
        this.user = user;
    }

    public class Comment {

        @SerializedName("comment")
        private String comment;

        @SerializedName("dateCreated")
        private String dateCreated;

        @SerializedName("rating")
        private String rating;

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

        public String getRating() {
            return rating;
        }

        public void setRating(String rating) {
            this.rating = rating;
        }

    }


}
