package com.crcexam.android.UI.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class SignInFragment extends Fragment implements View.OnClickListener {

    private Context mContext;
    private View rootView;
    private ConnectionDetector cd;
    private ProgressHUD progressHUD;


    public SignInFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sign_in, container, false);
        mContext = getContext();
        cd = new ConnectionDetector(mContext);
        setFontStyle();
        setListener();
        return rootView;
    }

    private void setFontStyle() {
//        ((TextView) rootView.findViewById(R.id.txtApp)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
     //   ((TextView) rootView.findViewById(R.id.txtTitle)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
       // ((TextView) rootView.findViewById(R.id.txtLogin)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Italic));
        ((EditText) rootView.findViewById(R.id.edtName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtMail)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtPassword)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((Button) rootView.findViewById(R.id.signinbtn)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));

    }

    private void setListener() {
        ((Button) rootView.findViewById(R.id.signinbtn)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.textsignin)).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                validation();
                break;
            case R.id.textsignin:
                //finish();
                LoginActivity.viewPager.setCurrentItem(LoginActivity.viewPager.getCurrentItem()-1,true);
                break;
        }
    }


    private void validation() {
        if (((EditText) rootView.findViewById(R.id.edtEmail)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Email_Alert), mContext);
        } else if (!Utility.checkEmail(((EditText) rootView.findViewById(R.id.edtEmail)).getText().toString().trim())) {
            Utility.toastHelper(mContext.getString(R.string.Email_Validation_Alert), mContext);
        } else if (((EditText) rootView.findViewById(R.id.edtPassword)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Password_Alert), mContext);
        } else if (((EditText) rootView.findViewById(R.id.edtPassword)).getText().toString().trim().length() < 8) {
            Utility.toastHelper(mContext.getString(R.string.Password_length), mContext);
        } else {
            if (cd.isConnectingToInternet()) {
                // startActivity(new Intent(LoginActivity.this, MainActivity.class));
                // finish();
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("apikey", Constant.API_KEY);
                jsonObject.addProperty("siteid", Constant.SITE_ID);
                jsonObject.addProperty("username", ((EditText) rootView.findViewById(R.id.edtEmail)).getText().toString().trim());
                jsonObject.addProperty("password", ((EditText) rootView.findViewById(R.id.edtPassword)).getText().toString().trim());
                progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                    }
                });
                register(Constant.API_KEY, Constant.SITE_ID, ((EditText) rootView.findViewById(R.id.edtEmail)).getText().toString().trim(), ((EditText) rootView.findViewById(R.id.edtPassword)).getText().toString().trim());
            } else {
                Utility.toastHelper("No internet connection!", mContext);
            }
        }

    }

    private void register(String API_KEY, String SITE_ID, String email, String password) {
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.register(API_KEY, SITE_ID, email, password).enqueue(new Callback<ResponseBody>() {
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
                        t.getLocalizedMessage();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
