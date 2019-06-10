package com.ngtiofack.go4lunch.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.Objects;

/**
 * Created by NG-TIOFACK on 2/13/2019.
 */

public class mainUtils {

    public static final float zoomLevel = 15; //This goes up to 21
    public static final String url = "https://maps.googleapis.com/maps/";
    public static final int PROXIMITY_RADIUS = 1000;
    public static final String TYPE = "restaurant";
    public static  final String RESTAURANT_LIKE = "Restaurants";
    public static  final String LIKE = "like";
    public static final String API_GOOGLE_PATH = "api/place/nearbysearch/json?sensor=true&key=";

    public static final String RESTAURANT_IS_NOT_SELECTED = "No restaurant is selected";
    public static final String COLLECTION_NAME = "messages";
    public static final String RESTAURANT_SELECTED = "restaurantSelected";

    public static final int SET_INTERVAL = 120000;

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

    public static  String convertStringToHex(String str){

        char[] chars = str.toCharArray();

        StringBuilder hex = new StringBuilder();
        for (char aChar : chars) {
            hex.append(Integer.toHexString((int) aChar));
        }

        return hex.toString();
    }

    public static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, Objects.requireNonNull(vectorDrawable).getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }



}

