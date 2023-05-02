package com.example.tasteit_java.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeCommentRequest {

    @SerializedName("rid")
    @Expose
    private String rid;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("rating")
    @Expose
    private String rating;

    public RecipeCommentRequest(String rid, String token, String comment, String rating) {
        this.rid = rid;
        this.token = token;
        this.comment = comment;
        this.rating = rating;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }
}
