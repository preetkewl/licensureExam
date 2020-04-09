package com.crcexam.android.UI.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.utils.Utility;

import java.util.Objects;

public class InfoFragment extends Fragment implements View.OnClickListener {

    View rootView;
    Context mContext;
    public static String FACEBOOK_URL = "https://www.facebook.com/CRCExam";
    public static String FACEBOOK_PAGE_ID = "CRCExam";

    Toolbar mToolbar;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        mContext = getActivity();
        setFontStyle();
        setListener();

        hideKeyboard(getActivity());
        setActionBar();
        return rootView;
    }

    private void setActionBar() {
        try {
            mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_dash);
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setVisibility(View.VISIBLE);
            ((ImageView) mToolbar.findViewById(R.id.imgHome)).setVisibility(View.GONE);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setOnClickListener(this);
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setText("Result");
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setVisibility(View.VISIBLE);
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.OpenSans_Bold));
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void setFontStyle() {
        ((TextView) rootView.findViewById(R.id.txtDirectTitle)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Bold));
        ((TextView) rootView.findViewById(R.id.txtDirectMsg)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));

    }

    private void setListener() {

        ((ImageView) rootView.findViewById(R.id.imv_facbook)).setOnClickListener(this);
        ((ImageView) rootView.findViewById(R.id.imv_linkedin)).setOnClickListener(this);
        ((ImageView) rootView.findViewById(R.id.imv_twitter)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_linkedin:
                openExternalBowser("https://www.linkedin.com/company/licensure-exams-inc-");
                break;
            case R.id.imv_facbook:
                openExternalBowser("https://www.facebook.com/CRCExam");
                break;
            case R.id.imv_twitter:
                openExternalBowser("https://twitter.com/LicensureExams");
                break;
            case R.id.imgback:
                try {
                    ((ImageView) mToolbar.findViewById(R.id.imgback)).setVisibility(View.GONE);
                    ((ImageView) mToolbar.findViewById(R.id.imgHome)).setVisibility(View.VISIBLE);
                    ((ImageView) mToolbar.findViewById(R.id.imgback)).setOnClickListener(this);
                    ((TextView) mToolbar.findViewById(R.id.tv_title)).setVisibility(View.GONE);
                    Objects.requireNonNull(getActivity()).onBackPressed();
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
        }
    }

    private void openExternalBowser(String url) {

        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public String getFacebookPageURL(Context context, String pkg) {
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo(pkg, 0).versionCode;
           /* if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app*/
            return "fb://page/" + FACEBOOK_PAGE_ID;
            //}
        } catch (PackageManager.NameNotFoundException e) {
            return FACEBOOK_URL; //normal web url
        }
    }
}
