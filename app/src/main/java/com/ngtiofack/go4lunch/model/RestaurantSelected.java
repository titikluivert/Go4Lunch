package com.ngtiofack.go4lunch.model;

import android.widget.LinearLayout;

public class RestaurantSelected {

    private String userName;
    private String urlPicture;




    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public RestaurantSelected() {
    }

    public RestaurantSelected(String userName, String urlPicture) {
        this.userName = userName;
        this.urlPicture = urlPicture;


    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
