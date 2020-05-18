package com.crcexam.UI;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.crcexam.R;
import com.crcexam.UI.auth.LoginActivity;
import com.crcexam.UI.dashboard.DashboardActivity;
import com.crcexam.constants.Constant;
import com.crcexam.utils.PreferenceClass;

public class SplashScreenActivity extends Activity implements View.OnClickListener {

   private static TextView termsPolicy;
   private static Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        termsPolicy = (TextView)findViewById(R.id.TermsService);
        startBtn = (Button) findViewById(R.id.startbtn);
        startBtn.setOnClickListener(this);

        if (PreferenceClass.getBooleanPreferences(getApplicationContext(), Constant.IS_FIRST)) {
           termsPolicy.setVisibility(View.GONE);
           startBtn.setVisibility(View.GONE);
        } else {
            termsPolicy.setVisibility(View.VISIBLE);
            startBtn.setVisibility(View.VISIBLE);

        }


//        initializeViews();
        customTextView(termsPolicy);

        setStatusBarColor();
    }

    private void setStatusBarColor() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    private void customTextView(TextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "Tap, I agree to accept the ");
        spanTxt.append("terms & policy.");
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
            }
        }, spanTxt.length() - "terms & policy.".length(), spanTxt.length(), 0);
        spanTxt.setSpan(new ForegroundColorSpan(Color.GRAY),
                spanTxt.length() - "terms & policy.".length(), spanTxt.length(), 0);
        spanTxt.setSpan(
                new UnderlineSpan(),
                spanTxt.length() - "terms & policy.".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);

        startDashboard();
    }

    public void startDashboard(){
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
                }
            }
        }, 2000);
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

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.startbtn:
                startBtn.setVisibility(View.GONE);
                PreferenceClass.setBooleanPreference(getApplicationContext(), Constant.IS_FIRST, true);
                termsPolicy.setText("Loading....");
                initializeViews();
                break;
        }
    }
}
