package com.crcexam.android.UI.auth;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.crcexam.android.R;
import com.crcexam.android.UI.dashboard.DashboardActivity;
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

import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ConnectionDetector cd;
    private ProgressHUD progressHUD;


    String email="";
    EditText userNameET;
    EditText emailET;
    EditText passwordET;
    ImageView backET;
    Toolbar toolbar;

    Activity activity= this;

    int click=-1;
    ImageView eyepass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        cd = new ConnectionDetector(mContext);




        setData();

        getEmail();

        setStatusBarColor();

        hideKeyboard(activity);
//        setFontStyle();
//        setListener();





    }


    private void setStatusBarColor() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }
    private void setData() {
        userNameET = (EditText) findViewById(R.id.et_username);
        emailET = (EditText) findViewById(R.id.et_email_or_add);
        passwordET = (EditText) findViewById(R.id.et_password);

        eyepass = (ImageView) findViewById(R.id.iv_eye);
        findViewById(R.id.bt_register_me).setOnClickListener(this);
        findViewById(R.id.imgMenu).setOnClickListener(this);
        findViewById(R.id.iv_eye).setOnClickListener(this);




        eyepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if (click == -1) {
                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT |
                            TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyepass.setImageResource(R.drawable.showpassword);
                    click++;


                }else {
                    click--;

                    passwordET.setInputType(InputType.TYPE_CLASS_TEXT |
                            TYPE_TEXT_VARIATION_PASSWORD);
                    eyepass.setImageResource(R.drawable.hidepassword);


                }


            }
        });



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            toolbar = (Toolbar) findViewById(R.id.toolbar);
//            setSupportActionBar(toolbar);
//
//            setSupportActionBar(toolbar);
//
//
//            backET = (ImageView) toolbar.findViewById(R.id.imgMenu);
//            setSupportActionBar(toolbar);
//
//        }
    }

    private void getEmail() {
        Intent intent = getIntent();
        if(intent!=null){
            if(intent.getStringExtra(Constant.EMAIL)!=null && !intent.getStringExtra(Constant.EMAIL).isEmpty()) {
                email = intent.getStringExtra(Constant.EMAIL);

                emailET.setText(email);
            }
        }
    }

//    private void setFontStyle() {
//        ((TextView) findViewById(R.id.txtApp)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
//        ((TextView) findViewById(R.id.txtTitle)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
//        ((TextView) findViewById(R.id.txtLogin)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
//        ((EditText) findViewById(R.id.edtname)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
//        ((EditText) findViewById(R.id.edtEmail)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
//        ((EditText) findViewById(R.id.edtPassword)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
//        ((Button) findViewById(R.id.btnRegister)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
//
//    }

//    private void setListener() {
//        ((Button) findViewById(R.id.btnRegister)).setOnClickListener(this);
//        ((TextView) findViewById(R.id.txtLogin)).setOnClickListener(this);
//
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_register_me:
                validation();
                break;
            case R.id.imgMenu:
                finish();
                break;
        }
    }

    private void validation() {
        if (((EditText) findViewById(R.id.et_username)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Name_Alert), mContext);
        }
        else if (((EditText) findViewById(R.id.et_email_or_add)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Email_Alert), mContext);
        } else if (!Utility.checkEmail(((EditText) findViewById(R.id.et_email_or_add)).getText().toString().trim())) {
            Utility.toastHelper(mContext.getString(R.string.Email_Validation_Alert), mContext);
        } else if (((EditText) findViewById(R.id.et_password)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Password_Alert), mContext);
        } else if (((EditText) findViewById(R.id.et_password)).getText().toString().trim().length() < 8) {
            Utility.toastHelper(mContext.getString(R.string.Password_length), mContext);
        } else {
            if (cd.isConnectingToInternet()) {
                // startActivity(new Intent(LoginActivity.this, MainActivity.class));
                // finish();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("apikey", Constant.API_KEY);
                jsonObject.addProperty("siteid", Constant.SITE_ID);
                jsonObject.addProperty("username", ((EditText) findViewById(R.id.et_email_or_add)).getText().toString().trim());
                jsonObject.addProperty("password", ((EditText) findViewById(R.id.et_password)).getText().toString().trim());
                progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                    }
                });
                register(Constant.API_KEY, Constant.SITE_ID,
                        ((EditText) findViewById(R.id.et_email_or_add)).getText().toString().trim(),
                        ((EditText) findViewById(R.id.et_password)).getText().toString().trim(),
                        ((EditText) findViewById(R.id.et_username)).getText().toString().trim());
            } else {
                Utility.toastHelper("No internet connection!", mContext);
            }
        }

    }

    private void register(String API_KEY, String SITE_ID, String email, String password, String name) {
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(this, false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.register(API_KEY, SITE_ID, email, password, name).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            // hideLoader(indicatorView);
                            if (progressHUD != null && progressHUD.isShowing()) {
                                progressHUD.dismiss();
                            }
                            Log.e("res code ", response.code() + "");
                            if (response.code() == 200) {
                                String res = response.body().string();
                                Log.e("res object ", res + "");
                                JSONObject object = new JSONObject(res);
                                Log.e("res obj ", object + "");
                                if (object.getString("response").equalsIgnoreCase("success") && object.getInt("responsecode") == 0) {
                                    PreferenceClass.setStringPreference(mContext, Constant.UserData.AUTH_TOKEN, object.getString("authtoken"));
                                    PreferenceClass.setStringPreference(mContext, Constant.UserData.REFRESH_TOKEN, object.getString("refreshtoken"));
                                    PreferenceClass.setBooleanPreference(mContext, Constant.IS_LOGIN, true);
                                    startActivity(new Intent(mContext, DashboardActivity.class));
                                    //finish();
                                } else if (object.getInt("responsecode") == 201) {
                                    Utility.toastHelper(object.getString("response"), mContext);
                                } else {
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
                        if (progressHUD != null && progressHUD.isShowing()) {
                            progressHUD.dismiss();
                        }
                        t.getLocalizedMessage();
                    }
                });
            }
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

//    private void register(String API_KEY, String SITE_ID, String email, String password) {
//        try {
//            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
//            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(RegisterActivity.this, false);
//            RetrofitService home2hotel = null;
//            if (retrofit != null) {
//                home2hotel = retrofit.create(RetrofitService.class);
//            }
//            if (home2hotel != null) {
//                home2hotel.register(API_KEY, SITE_ID, email, password, ).enqueue(new Callback<ResponseBody>() {
//                    @Override
//                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                        try {
//                            // hideLoader(indicatorView);
//                            if (progressHUD != null && progressHUD.isShowing()) {
//                                progressHUD.dismiss();
//                            }
//                            Log.e("res code ", response.code() + "");
//                            if (response.code() == 200) {
//                                String res = response.body().string();
//                                Log.e("res object ", res + "");
//                                JSONObject object = new JSONObject(res);
//                                Log.e("res obj ", object + "");
//                                if (object.getString("response").equalsIgnoreCase("success") && object.getInt("responsecode") == 0) {
//                                    PreferenceClass.setStringPreference(mContext, Constant.UserData.AUTH_TOKEN, object.getString("authtoken"));
//                                    PreferenceClass.setStringPreference(mContext, Constant.UserData.REFRESH_TOKEN, object.getString("refreshtoken"));
//                                    PreferenceClass.setBooleanPreference(mContext, Constant.IS_LOGIN, true);
//                                    startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
//                                    finish();
//                                } else if (object.getInt("responsecode") == 201) {
//                                    Utility.toastHelper(object.getString("response"), mContext);
//                                } else {
//                                    Utility.toastHelper(object.getString("response"), mContext);
//                                }
//                            } else {
//                                String error = response.errorBody().string();
//                                Log.e("errorr ", error);
//                                JSONObject object = new JSONObject(error);
//                                if (object.has("response")) {
//                                    Utility.toastHelper(object.getString("response"), mContext);
//                                } else {
//                                    Utility.toastHelper(response.message(), mContext);
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<ResponseBody> call, Throwable t) {
//                        t.getLocalizedMessage();
//                    }
//                });
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
