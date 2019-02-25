package com.saveethataskdoor.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    static SharedPreferences preferences;

    public static String getUId(Context context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return preferences.getString("uId", "");
    }

    public static void setUId(String uId, Context context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        preferences.edit().putString("uId", uId).commit();
    }

    public static String getEmail(Context context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return preferences.getString("email", "");
    }

    public static void setEmail(String email, Context context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        preferences.edit().putString("email", email).commit();
    }

    public static String getProfileType(Context context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return preferences.getString("profile_type", "");
    }

    public static void setProfileType(String profile_type, Context context) {
        preferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        preferences.edit().putString("profile_type", profile_type).commit();
    }
}
