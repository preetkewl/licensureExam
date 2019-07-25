package com.crcexam.android.UI.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.UI.dashboard.DashboardActivity;
import com.crcexam.android.UI.fragments.SignInFragment;
import com.crcexam.android.UI.fragments.SignUpFragment;
import com.crcexam.android.adapters.SignUpTabAdapter;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.ConnectionDetector;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.ProgressHUD;
import com.crcexam.android.utils.Utility;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ConnectionDetector cd;
    private ProgressHUD progressHUD;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    private SignUpTabAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        cd = new ConnectionDetector(mContext);
        setFontStyle();
        setListener();
        tabLayout=(TabLayout)findViewById(R.id.sliding_tabs);
        viewPager=(ViewPager)findViewById(R.id.loginviewpager);



        adapter = new SignUpTabAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignUpFragment(), "Sign Up");
        adapter.addFragment(new SignInFragment(), "Sign In");

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public int getItem(int i){
        return viewPager.getCurrentItem()+1;
    }



    private void setFontStyle() {
       /* ((TextView) findViewById(R.id.txtApp)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
        ((TextView) findViewById(R.id.txtTitle)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
        ((TextView) findViewById(R.id.txtSignup)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
        ((EditText) findViewById(R.id.edtEmail)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) findViewById(R.id.edtPassword)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((Button) findViewById(R.id.btnLogin)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));*/

    }

    private void setListener() {
       /* ((Button) findViewById(R.id.btnLogin)).setOnClickListener(this);
        ((TextView) findViewById(R.id.txtSignup)).setOnClickListener(this);*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.btnLogin:
                validation();
                break;
            case R.id.txtSignup:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

                break;*/
        }
    }

    private void validation() {
        if (((EditText) findViewById(R.id.edtEmail)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Email_Alert), mContext);
        } else if (!Utility.checkEmail(((EditText) findViewById(R.id.edtEmail)).getText().toString().trim())) {
            Utility.toastHelper(mContext.getString(R.string.Email_Validation_Alert), mContext);
        } else if (((EditText) findViewById(R.id.edtPassword)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Password_Alert), mContext);
        } else if (((EditText) findViewById(R.id.edtPassword)).getText().toString().trim().length() < 8) {
            Utility.toastHelper(mContext.getString(R.string.Password_length), mContext);
        } else {
            if (cd.isConnectingToInternet()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("apikey", Constant.API_KEY);
                jsonObject.addProperty("siteid", Constant.SITE_ID);
                jsonObject.addProperty("username", ((EditText) findViewById(R.id.edtEmail)).getText().toString().trim());
                jsonObject.addProperty("password", ((EditText) findViewById(R.id.edtPassword)).getText().toString().trim());
                progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                    }
                });
                login(Constant.API_KEY, Constant.SITE_ID, ((EditText) findViewById(R.id.edtEmail)).getText().toString().trim(), ((EditText) findViewById(R.id.edtPassword)).getText().toString().trim());
            } else {
                Utility.toastHelper("No internet connection!", mContext);
            }
        }

    }

    private void login(String API_KEY, String SITE_ID, String email, String password) {
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(LoginActivity.this, false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.login(API_KEY, SITE_ID, email, password).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (progressHUD != null && progressHUD.isShowing()) {
                                progressHUD.dismiss();
                            }
                            // hideLoader(indicatorView);

                            Log.e("res code ", response.code() + "");
                            if (response.code() == 200) {
                                String res = response.body().string();
                                Log.e("log object ", res + "");
                                JSONObject object = new JSONObject(res);
                                Log.e("log obj ", object + "");
                                if (object.getString("response").equalsIgnoreCase("success") && object.getInt("responsecode") == 0) {
                                    PreferenceClass.setStringPreference(mContext, Constant.UserData.AUTH_TOKEN, object.getString("authtoken"));
                                    PreferenceClass.setBooleanPreference(mContext, Constant.IS_LOGIN, true);
                                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                                    finish();
                                } else if (object.getInt("responsecode") == 201) {
                                    Utility.toastHelper(object.getString("response"), mContext);
                                }else  {
                                    Utility.toastHelper(object.getString("response"), mContext);
                                }
                            } else {
                                String error = response.errorBody().string();
                                Log.e("errorr ", error);
                                JSONObject object = new JSONObject(error);
                                if (object.has("response")) {
                                    Utility.toastHelper(object.getString("response"), mContext);
                                } else {
                                    Utility.toastHelper(response.message(), mContext);
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.getLocalizedMessage();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
