package com.ngtiofack.go4lunch.utils;

/**
 * Created by NG-TIOFACK on 2/13/2019.
 */


public class Utils {

    public static final float zoomLevel = 15; //This goes up to 21
    public static final String url = "https://maps.googleapis.com/maps/";
    public static final int PROXIMITY_RADIUS = 1000;
    public static final String TYPE = "restaurant";


    public static int getDistanceToRestaurants(double latA, double longA, double latB, double longB) {
        final double a = 111.16;
        double temp = (Math.abs(Math.sqrt((longA - longB) * (longA - longB) + (latA - latB) * (latA - latB)) * a)) * 1000;
        return (int)temp;
    }
}

