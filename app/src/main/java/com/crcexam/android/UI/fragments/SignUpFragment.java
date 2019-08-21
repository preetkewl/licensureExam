package com.crcexam.android.UI.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.UI.auth.LoginActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private View rootview;
    private ProgressHUD progressHUD;
    private ConnectionDetector cd;


    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mContext = getContext();
        cd = new ConnectionDetector(mContext);
        setFontStyle();
        setClickListener();
        return rootview;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signinbtn:
                validation();
                break;
            case R.id.txtSignup:
                //startActivity(new Intent(mContext, RegisterActivity.class));

                //LoginActivity.viewPager.setCurrentItem(LoginActivity.viewPager.getCurrentItem() + 1, true);
                break;
        }
    }


    private void setFontStyle() {
//        ((TextView) rootview.findViewById(R.id.txtApp)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
        //      ((TextView) rootview.findViewById(R.id.txtTitle)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
        //    ((TextView) rootview.findViewById(R.id.txtSignup)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
        ((EditText) rootview.findViewById(R.id.edtEmail)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootview.findViewById(R.id.edtPassword)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((Button) rootview.findViewById(R.id.signinbtn)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        String fourth = "Sign Up";

        TextView textView = rootview.findViewById(R.id.txtSignup);

        singleTextView(textView, fourth);

    }

    private void singleTextView(TextView textView, final String strSignIn) {

        SpannableStringBuilder spanText = new SpannableStringBuilder();
        spanText.append("Don't Have An Account? ");
        spanText.append(strSignIn);
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                LoginActivity.viewPager.setCurrentItem(LoginActivity.viewPager.getCurrentItem() + 1, true);

            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(textPaint.linkColor);    // you can use custom color
                textPaint.setUnderlineText(true);    // this remove the underline
            }
        }, spanText.length() - strSignIn.length(), spanText.length(), 0);

        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // On Click Action

            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(textPaint.linkColor);    // you can use custom color
                textPaint.setUnderlineText(true);    // this remove the underline
            }
        }, spanText.length() - strSignIn.length(), spanText.length(), 0);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(spanText, TextView.BufferType.SPANNABLE);

    }


    private void setClickListener() {
        rootview.findViewById(R.id.signinbtn).setOnClickListener(this);
        //rootview.findViewById(R.id.txtSignup).setOnClickListener(this);

    }

    private void validation() {
        if (((EditText) rootview.findViewById(R.id.edtEmail)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Email_Alert), mContext);
        } else if (!Utility.checkEmail(((EditText) rootview.findViewById(R.id.edtEmail)).getText().toString().trim())) {
            Utility.toastHelper(mContext.getString(R.string.Email_Validation_Alert), mContext);
        } else if (((EditText) rootview.findViewById(R.id.edtPassword)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Password_Alert), mContext);
        } else if (((EditText) rootview.findViewById(R.id.edtPassword)).getText().toString().trim().length() < 8) {
            Utility.toastHelper(mContext.getString(R.string.Password_length), mContext);
        } else {
            if (cd.isConnectingToInternet()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("apikey", Constant.API_KEY);
                jsonObject.addProperty("siteid", Constant.SITE_ID);
                jsonObject.addProperty("username", ((EditText) rootview.findViewById(R.id.edtEmail)).getText().toString().trim());
                jsonObject.addProperty("password", ((EditText) rootview.findViewById(R.id.edtPassword)).getText().toString().trim());
                progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                    }
                });
                login(Constant.API_KEY, Constant.SITE_ID, ((EditText) rootview.findViewById(R.id.edtEmail)).getText().toString().trim(), ((EditText) rootview.findViewById(R.id.edtPassword)).getText().toString().trim());
            } else {
                Utility.toastHelper("No internet connection!", mContext);
            }
        }

    }

    private void login(String API_KEY, String SITE_ID, String email, String password) {
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
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
                                    startActivity(new Intent(mContext, DashboardActivity.class));
                                    getActivity().finish();
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
                        t.getLocalizedMessage();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
