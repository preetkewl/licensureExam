package com.crcexam.android.UI;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;

import com.crcexam.android.R;
import com.crcexam.android.UI.auth.LoginActivity;
import com.crcexam.android.UI.dashboard.DashboardActivity;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.utils.PreferenceClass;

public class SplashScreenActivity extends Activity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        initializeViews();
    }




    private void initializeViews() {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app ic_logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                if (PreferenceClass.getBooleanPreferences(getApplicationContext(), Constant.IS_LOGIN)) {
                    startActivity(new Intent(SplashScreenActivity.this, DashboardActivity.class));
                    finish();
                } else {
                    startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 2000);

    }

}
