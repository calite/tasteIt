package com.example.tasteit_java.clases;

public class Comment {

    private String comment;
    private String dateCreated;
    private float rating;
    private String tokenUser;

    public Comment(String comment, String dateCreated, float rating, String tokenUser) { //comentario en receta
        this.comment = comment;
        this.dateCreated = dateCreated;
        this.rating = rating;
        this.tokenUser = tokenUser;
    }

    public Comment(String comment, String dateCreated) { //comentario en usuario
        this.comment = comment;
        this.dateCreated = dateCreated;
    }

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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getTokenUser() { return tokenUser; }

    public void setTokenUser(String tokenUser) { this.tokenUser = tokenUser; }
}
