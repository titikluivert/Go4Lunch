package com.ngtiofack.go4lunch.model;


import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Message {


    private String message;
    private Date dateCreated;
    private Go4LunchUsers userSender;
    private String urlImage;
    private String uIdBetween2Chats;

    public Message() { }


    public Message(String message, Go4LunchUsers userSender,String uIdBetween2Chats) {
        this.message = message;
        this.userSender = userSender;
        this.uIdBetween2Chats = uIdBetween2Chats;

    }

    public Message(String message, String urlImage, Go4LunchUsers userSender) {
        this.message = message;
        this.urlImage = urlImage;
        this.userSender = userSender;

    }


    // --- GETTERS ---
    public String getMessage() { return message; }
    @ServerTimestamp
    public Date getDateCreated() { return dateCreated; }
    public Go4LunchUsers getUserSender() { return userSender; }
    public String getUrlImage() { return urlImage; }
    public String getuIdBetween2Chats() {
        return uIdBetween2Chats;
    }

    // --- SETTERS ---
    public void setMessage(String message) { this.message = message; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
    public void setUserSender(Go4LunchUsers userSender) { this.userSender = userSender; }
    public void setUrlImage(String urlImage) { this.urlImage = urlImage; }
    public void setuIdBetween2Chats(String uIdBetween2Chats) {
        this.uIdBetween2Chats = uIdBetween2Chats;
    }

}
