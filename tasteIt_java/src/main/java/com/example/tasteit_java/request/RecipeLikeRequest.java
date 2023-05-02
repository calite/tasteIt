package com.example.tasteit_java.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecipeLikeRequest {

    @SerializedName("rid")
    @Expose
    private String rid;

    @SerializedName("token")
    @Expose
    private String token;

    public RecipeLikeRequest(String rid, String token) {
        this.rid = rid;
        this.token = token;
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
