package com.crcexam.android.UI.auth;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.UI.activities.ForgotPasswordActivity;
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
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD;
import static android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private Context mContext;
    private ConnectionDetector cd;
    private ProgressHUD progressHUD;
    private TabLayout tabLayout;
    public static ViewPager viewPager;
    private SignUpTabAdapter adapter;
    private CallbackManager callbackManager;
    RelativeLayout bt_facebook;
    private static final String EMAIL = "email";
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;
    ProfileTracker profileTracker;
    EditText et_username_email;

    LoginButton loginButton;
    String email="";
    String id="";
    String username= "";
    Activity activity= this;

    int click=-1;
    ImageView eyepass;
    EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        mContext = this;

        eyepass = (ImageView) findViewById(R.id.iv_eye);
        passwordET = (EditText) findViewById(R.id.et_password);

        findViewById(R.id.bt_loginme).setOnClickListener(this);
        findViewById(R.id.bt_register_me).setOnClickListener(this);
        findViewById(R.id.tv_forgot_password).setOnClickListener(this);
        findViewById(R.id.iv_eye).setOnClickListener(this);
//        FacebookSdk.sdkInitialize(this.getApplicationContext());

//        bt_facebook = findViewById(R.id.bt_facebook);
//        bt_facebook.setOnClickListener(this);

        callbackManager = CallbackManager.Factory.create();



//        accessTokenTracker = new AccessTokenTracker() {
//            @Override
//            protected void onCurrentAccessTokenChanged(
//                    AccessToken oldAccessToken,
//                    AccessToken currentAccessToken) {
//                // Set the access token using
//                // currentAccessToken when it's loaded or set.
//            }
//        };
//        // If the access token is available already assign it.
//        accessToken = AccessToken.getCurrentAccessToken();


//        profileTracker= new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(
//                    Profile oldProfile,
//                    Profile currentProfile) {
//                // App code
//            }
//        };

        cd = new ConnectionDetector(mContext);
        setFontStyle();
        setListener();
//        tabLayout=(TabLayout)findViewById(R.id.sliding_tabs);
//        viewPager = (ViewPager) findViewById(R.id.loginviewpager);


        adapter = new SignUpTabAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignUpFragment(), "Sign In");
        adapter.addFragment(new SignInFragment(), "Sign Up");

//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        viewPager.setAdapter(adapter);
//        tabLayout.setupWithViewPager(viewPager);




        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");

        loginButton.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);



        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest.newMeRequest(
                        loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    // handle error
                                } else {
                                    // get email and id of the user
                                    email = me.optString("email");
                                    id = me.optString("id");
                                    if(email.equals("")) {
                                        username = me.optString("name");
                                        email= username;
                                    }

//                                    String user_email =response.getJSONObject().optString("email");


                                    startActivity(new Intent(LoginActivity.this, RegisterActivity.class).putExtra(Constant.EMAIL, email));

                                }
                            }
                        }).executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        setStatusBarColor();
        hideKeyboard(activity);



        findViewById(R.id.iv_eye).setOnClickListener(new View.OnClickListener() {
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
    }


    private void setStatusBarColor() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }

    public int getItem(int i) {
        return viewPager.getCurrentItem() + 1;
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

//    private void togglePassVisability() {
//        boolean isPasswordVisible=false;
//        if (isPasswordVisible) {
//            String pass = pa.getText().toString();
//            firstEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
//            firstEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            firstEditText.setText(pass);
//            firstEditText.setSelection(pass.length());
//        } else {
//            String pass = firstEditText.getText().toString();
//            firstEditText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
//            firstEditText.setInputType(InputType.TYPE_CLASS_TEXT);
//            firstEditText.setText(pass);
//            firstEditText.setSelection(pass.length());
//        }
//        isPasswordVisible = !isPasswordVisible;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_loginme:
                validation();
                break;
            case R.id.bt_register_me:
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));

                break;

            case R.id.tv_forgot_password:
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));

                break;
        }
    }


    private void validation() {
        if (((EditText) findViewById(R.id.et_username_email)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Email_Alert), mContext);
        }
//        else if (!Utility.checkEmail(((EditText) findViewById(R.id.edtEmail)).getText().toString().trim())) {
//            Utility.toastHelper(mContext.getString(R.string.Email_Validation_Alert), mContext);
//        }
        else if (((EditText) findViewById(R.id.et_password)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Password_Alert), mContext);
        } else if (((EditText) findViewById(R.id.et_password)).getText().toString().trim().length() < 8) {
            Utility.toastHelper(mContext.getString(R.string.Password_length), mContext);
        } else {
            if (cd.isConnectingToInternet()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("apikey", Constant.API_KEY);
                jsonObject.addProperty("siteid", Constant.SITE_ID);
                jsonObject.addProperty("username", ((EditText) findViewById(R.id.et_username_email)).getText().toString().trim());
                jsonObject.addProperty("password", ((EditText) findViewById(R.id.et_password)).getText().toString().trim());
                progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                    }
                });
                login(Constant.API_KEY, Constant.SITE_ID, ((EditText) findViewById(R.id.et_username_email)).getText().toString().trim(), ((EditText) findViewById(R.id.et_password)).getText().toString().trim());
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
                        Utility.toastHelper(t.getLocalizedMessage(), mContext);
                        t.getLocalizedMessage();
                    }
                });
            }
        } catch (Exception e) {
            if (progressHUD != null && progressHUD.isShowing()) {
                progressHUD.dismiss();
            }
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager!=null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(accessTokenTracker!=null) {
            accessTokenTracker.stopTracking();
        }
    }


    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            String email = object.getString("email");
                            String id = object.getString("id");
                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

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
}
