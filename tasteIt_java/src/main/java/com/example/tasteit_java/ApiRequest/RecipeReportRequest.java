package com.example.tasteit_java.ApiRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeReportRequest {

    @SerializedName("rid")
    @Expose
    private String rid;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("comment")
    @Expose
    private String comment;

    public RecipeReportRequest(String rid, String token, String comment) {
        this.rid = rid;
        this.token = token;
        this.comment = comment;
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
