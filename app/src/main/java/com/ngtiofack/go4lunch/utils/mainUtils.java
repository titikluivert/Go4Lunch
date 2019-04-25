package com.ngtiofack.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.YourLunch;

/**
 * Created by NG-TIOFACK on 2/13/2019.
 */


public class mainUtils {

    public static final float zoomLevel = 15; //This goes up to 21
    public static final String url = "https://maps.googleapis.com/maps/";
    public static final int PROXIMITY_RADIUS = 1000;
    public static final String TYPE = "restaurant";

    public static final String RESTAURANTISNOTSELECTED = "No restaurant is selected";
    public static final String COLLECTION_NAME = "messages";


    public static int getDistanceToRestaurants(double latA, double longA, double latB, double longB) {
        final double a = 111.16;
        double temp = (Math.abs(Math.sqrt((longA - longB) * (longA - longB) + (latA - latB) * (latA - latB)) * a)) * 1000;
        return (int) temp;
    }

    public static String getPhotoUrl(Context ctx, String photoReferenceUrl, int photoHeight, int photoWidth) {

        return "https://maps.googleapis.com/maps/api/place/photo?photoreference="
                + photoReferenceUrl
                + "&sensor=false&maxheight="
                + photoHeight + "&maxwidth="
                + photoWidth + "&key="
                + ctx.getString(R.string.google_maps_key);
    }

    public static int getNumOfStars(Double rating) {
        int retValue;
        if (rating <= 4) {
            retValue = 0;
        } else {
            if (rating < 4.4) {
                retValue = 1;
            } else {
                if (rating < 4.8) {
                    retValue = 2;
                } else {
                    retValue = 3;
                }
            }
        }
        return retValue;
    }

    public static void saveYourLunch(Context ctx, String name, String photoRef,int photoHeight, int photoWidth, String vicinity, int rating) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ctx.getString(R.string.save_restaurantName_key), name);
        editor.putString(ctx.getString(R.string.save_vicinity_key), vicinity);
        editor.putInt(ctx.getString(R.string.save_photoHeight_key), photoHeight);
        editor.putInt(ctx.getString(R.string.save_photoWidth_key), photoWidth);
        editor.putString(ctx.getString(R.string.save_photoRef_key), photoRef);
        editor.putInt(ctx.getString(R.string.save_number_of_stars_key), rating);
        editor.apply();
    }

    public static YourLunch getYourLunch(Context ctx) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);

        return new YourLunch(sharedPreferences.getString(ctx.getString(R.string.save_restaurantName_key), RESTAURANTISNOTSELECTED),
                sharedPreferences.getString(ctx.getString(R.string.save_vicinity_key), ""),
                sharedPreferences.getInt(ctx.getString(R.string.save_photoHeight_key), 0),
                sharedPreferences.getInt(ctx.getString(R.string.save_photoWidth_key), 0),
                sharedPreferences.getString(ctx.getString(R.string.save_photoRef_key), ""),
                sharedPreferences.getInt(ctx.getString(R.string.save_number_of_stars_key), 0));
    }

    public static  void saveUserId (Context context, String uid) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.save_uid_key), uid);
        editor.apply();
    }

    public static String getUserId(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getString(ctx.getString(R.string.save_uid_key), "");
    }

    public static  void saveRestaurantSelected (Context context, String restaurantSelected ) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.save_restaurantSelected_key), restaurantSelected);
        editor.apply();
    }

    public static String getRestaurantSelected(Context ctx) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return sharedPreferences.getString(ctx.getString(R.string.save_restaurantSelected_key), "");
    }

    public static long convertHexToDecimal(String hexReceiverId, String hexSenderId){

        int decimal2 = 0,  decimal = 0;

        String hex = convertStringToHex(hexReceiverId);
        String hex2 = convertStringToHex(hexSenderId);
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
             int temp = Integer.parseInt(output, 16);
            //convert the decimal to character
             decimal = decimal + temp;
        }

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex2.length()-1; i+=2 ){

            //grab the hex in pairs
            String output = hex2.substring(i, (i + 2));
            //convert hex to decimal
             int temp = Integer.parseInt(output, 16);
            //convert the decimal to character
            decimal2 = decimal2 + temp;
        }

        return (long)(decimal + decimal2);
    }

    private static  String convertStringToHex(String str){

        char[] chars = str.toCharArray();

        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString((int) aChar));
        }

        return hex.toString();
    }
}

