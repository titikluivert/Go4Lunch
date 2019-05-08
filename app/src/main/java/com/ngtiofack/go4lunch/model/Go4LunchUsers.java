package com.ngtiofack.go4lunch.model;

import com.google.firebase.database.annotations.Nullable;

/**
 * Created by NG-TIOFACK on 2/16/2019.
 */
public class Go4LunchUsers {

    private String uid;
    private String username;
    private boolean isConnected;
    private YourLunch yourLunch;

    @Nullable
    private String urlPicture;

    public Go4LunchUsers() {
    }

    public Go4LunchUsers(String uid, String username, String urlPicture, boolean isConnected,YourLunch yourLunch) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.isConnected = isConnected;
        this.yourLunch = yourLunch;
    }

    // --- GETTERS ---
    public String getUid() {
        return uid;
    }

    public boolean getIsConnected() {
        return isConnected;
    }

    public String getUrlPicture() {
        return urlPicture;
    }

    public String getUsername() {
        return username;
    }


    public YourLunch getYourLunch() {
        return yourLunch;
    }



    // --- SETTERS ---
    public void setUsername(String username) {
        this.username = username;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public void setIsConnected(Boolean mentor) {
        isConnected = mentor;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setYourLunch(YourLunch yourLunch) {
        this.yourLunch = yourLunch;
    }
}
