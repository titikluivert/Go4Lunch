package com.ngtiofack.go4lunch.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ngtiofack.go4lunch.R;

public class UserIDUtils {

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
}
