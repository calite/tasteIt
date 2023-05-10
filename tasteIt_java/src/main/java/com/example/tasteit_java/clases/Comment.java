package com.example.tasteit_java.clases;

public class Comment {
    private String comment;
    private int id;
    private String dateCreated;
    private float rating;
    private String tokenUser;
    private User user;

    public Comment(String comment, String dateCreated, float rating, User user) { //comentario en receta
        this.comment = comment;
        this.dateCreated = dateCreated;
        this.rating = rating;
        this.user = user;
    }

    public Comment(String comment, String dateCreated) { //comentario en usuario
        this.comment = comment;
        this.dateCreated = dateCreated;
    }

    public Comment(String comment, String dateCreated, User user) { //comentario en usuario
        this.comment = comment;
        this.dateCreated = dateCreated;
        this.user = user;
        this.tokenUser = user.getUid();
    }

    public Comment(String comment, String dateCreated, String tokenUser, int id) { //comentario en usuario
        this.comment = comment;
        this.dateCreated = dateCreated;
        this.tokenUser = tokenUser;
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
