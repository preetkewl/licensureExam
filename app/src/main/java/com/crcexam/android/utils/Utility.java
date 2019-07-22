package com.crcexam.android.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.crcexam.android.R;
import com.crcexam.android.constants.Constant;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class Utility {
    public static String twoDecimal(String rate) {
        DecimalFormat df = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.ENGLISH));


        return df.format(Float.parseFloat(rate));


    }

    public static String getCurrentDate(long date) {
        try {
            return new SimpleDateFormat("yyyy-dd-mm HH:mm:ss a", Locale.getDefault()).format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void clearBackStack(Context context) {
        FragmentManager fm = ((AppCompatActivity) context).getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public static void loadFragment(Fragment fragment, Context context, boolean addToBackStack) {
        FragmentTransaction transaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getName());
        }
        transaction.commit();
    }

    public static void toastHelper(String msg, Context context) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static boolean checkEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static Typeface setFontStyle(Context mContext, String style) {
        Typeface tfFontUI = null;
        if (style.equals(Constant.FontStyle.OpenSans_Bold)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "OpenSans_Bold.ttf");
            return tfFontUI;
        }

        if (style.equals(Constant.FontStyle.OpenSans_Regular)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "OpenSans_Regular.ttf");
            return tfFontUI;
        }
        if (style.equals(Constant.FontStyle.OpenSans_Semibold)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "OpenSans_Semibold.ttf");
            return tfFontUI;
        }
        if (style.equals(Constant.FontStyle.OpenSans_BoldItalic)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "OpenSans_BoldItalic.ttf");
            return tfFontUI;
        }
        if (style.equals(Constant.FontStyle.Roboto_Regular)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "Roboto_Regular.ttf");
            return tfFontUI;
        }
        if (style.equals(Constant.FontStyle.Roboto_Light)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "Roboto_Light.ttf");
            return tfFontUI;
        }
        if (style.equals(Constant.FontStyle.Roboto_Italic)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "Roboto_Italic.ttf");
            return tfFontUI;
        }
        if (style.equals(Constant.FontStyle.Roboto_Bold)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "Roboto_Bold.ttf");
            return tfFontUI;
        }
       /* if (style.equals(Constant.FontStyle.ROBOT_LIGHT)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "Roboto_Light.ttf");
            return tfFontUI;
        }if (style.equals(Constant.FontStyle.ROBOT_MEDIUM)) {
            tfFontUI = Typeface.createFromAsset(mContext.getAssets(), "Roboto_Medium.ttf");
            return tfFontUI;
        }*/

        return tfFontUI;
    }

    public static String parseTime(String time, String fromPattern,
                                   String toPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(fromPattern,
                Locale.getDefault());
        try {
            Date d = sdf.parse(time);
            sdf = new SimpleDateFormat(toPattern, Locale.getDefault());
            return sdf.format(d);
        } catch (Exception e) {
            Log.i("parseTime", "" + e.toString());
        }

        return "";
    }

    public static Date parseTimetoDefault(long time) {

        try {
            Calendar cal = Calendar.getInstance(TimeZone.getDefault());
            cal.setTimeInMillis(time);
            Date d = cal.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            //sdf.setTimeZone(Calendar.getInstance().getTimeZone());
            return sdf.parse(sdf.format(d));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Date();
    }

    public static String currentToServerType(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm",
                Locale.getDefault());
        try {
            Date d = sdf.parse(time);
            sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault());
            return sdf.format(d);
        } catch (Exception e) {
            Log.i("parseTime", "" + e.getMessage());
        }

        return "";
    }

    public static String getCurrentDate() {
        try {
            Calendar cal = Calendar.getInstance();
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).format(cal.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
