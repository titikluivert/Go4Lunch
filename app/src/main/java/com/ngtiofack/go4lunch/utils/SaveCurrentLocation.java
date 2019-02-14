package com.ngtiofack.go4lunch.utils;

/**
 * Created by <VOTRE-NOM> on <DATE-DU-JOUR>.
 */
public class SaveCurrentLocation {

    private float latitude;
    private float longitude;

    public SaveCurrentLocation(float latitude, float longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

}
