package com.ngtiofack.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ngtiofack.go4lunch.BuildConfig;
import com.ngtiofack.go4lunch.R;
import com.ngtiofack.go4lunch.model.YourLunch;

import static com.ngtiofack.go4lunch.utils.mainUtils.RESTAURANT_IS_NOT_SELECTED;

public class RestaurantsUtils {

    public static int getDistanceToRestaurants(double latA, double longA, double latB, double longB) {
        final double a = 111.16;
        double temp = (Math.abs(Math.sqrt((longA - longB) * (longA - longB) + (latA - latB) * (latA - latB)) * a)) * 1000;
        return (int) temp;
    }

    public static String getPhotoUrl(String photoReferenceUrl, int photoHeight, int photoWidth) {

        return "https://maps.googleapis.com/maps/api/place/photo?photoreference="
                + photoReferenceUrl
                + "&sensor=false&maxheight="
                + photoHeight + "&maxwidth="
                + photoWidth + "&key="
                + BuildConfig.ApiKeyGoogleID;
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

    public static void saveYourLunch(Context ctx, String name, String vicinity, int photoHeight, int photoWidth, String photoRef, int rating) {

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

        return new YourLunch(sharedPreferences.getString(ctx.getString(R.string.save_restaurantName_key), RESTAURANT_IS_NOT_SELECTED),
                sharedPreferences.getString(ctx.getString(R.string.save_vicinity_key), ""),
                sharedPreferences.getInt(ctx.getString(R.string.save_photoHeight_key), 0),
                sharedPreferences.getInt(ctx.getString(R.string.save_photoWidth_key), 0),
                sharedPreferences.getString(ctx.getString(R.string.save_photoRef_key), ""),
                sharedPreferences.getInt(ctx.getString(R.string.save_number_of_stars_key), 0));
    }
}
