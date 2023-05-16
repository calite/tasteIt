package com.example.tasteit_java.ApiRequest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserFollowRequest {

    @SerializedName("senderToken")
    @Expose
    private String senderToken;

    @SerializedName("receiverToken")
    @Expose
    private String receiverToken;


    public UserFollowRequest(String senderToken, String receiverToken) {
        this.senderToken = senderToken;
        this.receiverToken = receiverToken;
    }

    public String getSenderToken() {
        return senderToken;
    }

    public void setSenderToken(String senderToken) {
        this.senderToken = senderToken;
    }

    public String getReceiverToken() {
        return receiverToken;
    }

    public void setReceiverToken(String receiverToken) {
        this.receiverToken = receiverToken;
    }
}
