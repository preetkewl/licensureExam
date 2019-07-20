package com.crcexam.android.UI.splash;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.crcexam.android.R;
import com.crcexam.android.UI.auth.LoginActivity;
import com.crcexam.android.UI.dashboard.DashboardActivity;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.utils.PreferenceClass;


public class SplashActivity extends AppCompatActivity {

    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        mContext = this;
        setFontsStyle();
        initializeViews();
    }

    private void setFontsStyle() {
        ImageView imageView = (ImageView) findViewById(R.id.spinnerImageView);
        AnimationDrawable spinner = (AnimationDrawable) imageView.getBackground();
        spinner.start();
    }

    private void initializeViews() {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (PreferenceClass.getBooleanPreferences(mContext, Constant.IS_LOGIN)){
                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 2000);

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.e("onWindowFocusChanged ","fgc");

    }

}
