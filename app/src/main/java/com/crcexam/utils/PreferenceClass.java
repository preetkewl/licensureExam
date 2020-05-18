package com.crcexam.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

import static com.crcexam.constants.Constant.UserData.EXAM_ID;
import static com.crcexam.constants.Constant.UserData.FLIP_COUNT;
import static com.crcexam.constants.Constant.UserData.LAST_DATE;
import static com.crcexam.constants.Constant.UserData.MULTIPLE_COUNT;
import static com.crcexam.constants.Constant.UserData.SOLVE_EXAM_ID;
import static com.crcexam.constants.Constant.UserData.USER_NAME;

public class PreferenceClass {
    public static Context appContext;
    private static String HOME2HOTEL_PREFERENCE="crcExam_preference";



    public static void setUserName(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USER_NAME, userName);
        editor.commit();
        editor.apply();
    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_NAME, "");
    }

    public static void setDate(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(LAST_DATE, userName);
        editor.apply();
    }

    public static String getDate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(LAST_DATE, "");
    }

    public static void setExamId(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("ExamId", userName);
        editor.apply();
    }

    public static String getExamId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getString("ExamId", "");
    }

    public static void setExams(Context context, Set<String> id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(EXAM_ID, id);
        editor.apply();
    }

    public static Set<String> getExams(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(EXAM_ID, new HashSet<String>());
    }

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



    public static void setFlip(Context context, int c) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(FLIP_COUNT, c);
        editor.apply();
    }

    public static int getFlip(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(FLIP_COUNT, 1);
    }

    public static void setMultiple(Context context, int c) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(MULTIPLE_COUNT, c);
        editor.apply();
    }

    public static int getMultiple(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(MULTIPLE_COUNT, 1);
    }




    public static void setIds(Context context, Set<String> id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(SOLVE_EXAM_ID, id);
        editor.apply();
    }

    public static Set<String> getIds(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(HOME2HOTEL_PREFERENCE, Context.MODE_PRIVATE);
        return sharedPreferences.getStringSet(SOLVE_EXAM_ID, new HashSet<String>());
    }


}
