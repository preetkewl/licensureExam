package com.crcexam.android.UI.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.utils.Utility;

public class InfoFragment extends Fragment implements View.OnClickListener {

    View rootView;
    Context mContext;
    public static String FACEBOOK_URL = "https://www.facebook.com/CRCExam";
    public static String FACEBOOK_PAGE_ID = "CRCExam";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_info, container, false);
        mContext = getActivity();
        setFontStyle();
        setListener();
        return rootView;
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
