package com.example.tasteit_java.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserCommentRequest {

    @SerializedName("SenderId")
    @Expose
    private String SenderId;

    @SerializedName("ReceiverId")
    @Expose
    private String ReceiverId;

    @SerializedName("Comment")
    @Expose
    private String comment;

    public UserCommentRequest(String senderId, String receiverId, String comment) {
        SenderId = senderId;
        ReceiverId = receiverId;
        this.comment = comment;
    }

    public String getSenderId() {
        return SenderId;
    }

    public void setSenderId(String senderId) {
        SenderId = senderId;
    }

    public String getReceiverId() {
        return ReceiverId;
    }

    public void setReceiverId(String receiverId) {
        ReceiverId = receiverId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
