package com.crcexam.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceClass {
    public static Context appContext;
    private static String HOME2HOTEL_PREFERENCE="crcExam_preference";

    public static void setStringPreference(Context context, String name, String value) {
        appContext = context;
        SharedPreferences settings = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static void setTimePreference(Context context, String name, long value) {
        appContext = context;
        SharedPreferences settings = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(name, value);
        editor.commit();
    }

    public static void setIntegerPreference(Context context, String name, int value) {
        appContext = context;
        SharedPreferences settings = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public static void setBooleanPreference(Context context, String name, boolean value) {
        appContext = context;
        SharedPreferences settings = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(name, value);
        editor.commit();

    }

    public static long getTimePreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return settings.getLong(name, (long) 0.0);
    }


    public static String getStringPreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return settings.getString(name, "");
    }

    public static int getIntegerPreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return settings.getInt(name, 0);
    }

    public static boolean getBooleanPreferences(Context context, String name) {
        SharedPreferences settings = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return settings.getBoolean(name, false);
    }

    public static void clearPreference(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}
