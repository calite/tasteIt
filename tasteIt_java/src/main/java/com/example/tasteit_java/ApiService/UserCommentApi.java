package com.example.tasteit_java.ApiService;

import com.google.gson.annotations.SerializedName;

public class UserCommentApi {

    @SerializedName("comment")
    private String comment;

    @SerializedName("dateCreated")
    private String dateCreated;

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
}
