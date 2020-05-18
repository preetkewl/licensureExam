package com.crcexam.UI.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.crcexam.R;
import com.crcexam.constants.Constant;
import com.crcexam.network.RetrofitLoggedIn;
import com.crcexam.network.RetrofitService;
import com.crcexam.utils.ConnectionDetector;
import com.crcexam.utils.ProgressHUD;
import com.crcexam.utils.Utility;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ForgotPasswordActivity extends AppCompatActivity {

    Activity activity = this;
    @BindView(R.id.edtEmail)
    EditText edtEmail;
    @BindView(R.id.btnReset)
    Button btnReset;

    private Context mContext;
    private ProgressHUD progressHUD;
    private ConnectionDetector cd;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);

        Utility.setStatusBarColor(activity);

        mContext = ForgotPasswordActivity.this;
        cd = new ConnectionDetector(mContext);
        hideKeyboard(activity);
    }



    @OnClick({R.id.btnReset, R.id.imgMenu})
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.btnReset:

                validation();
                break;

            case R.id.imgMenu:
                finish();
                break;

        }
    }

    private void validation() {
        if (((EditText) findViewById(R.id.edtEmail)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Email_Alert), mContext);
        }
        else if (((EditText) findViewById(R.id.edtEmail)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper(mContext.getString(R.string.Password_Alert), mContext);
        }else{
            String etMail= edtEmail.getText().toString();


            if (cd.isConnectingToInternet()) {
                progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                    }
                });
                reset(Constant.API_KEY, Constant.SITE_ID, etMail);

            } else {
                Utility.toastHelper("No internet connection!", mContext);
            }
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



    private void reset(String API_KEY, String SITE_ID, String email) {
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(ForgotPasswordActivity.this, false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.reset(API_KEY, SITE_ID, email,"1").enqueue(new Callback<ResponseBody>() {
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
                                Utility.toastHelper("Reset Password link sent to your email. Please check your email.", mContext);
                                edtEmail.setText("");
                            } else {
                                String error = response.errorBody().string();
                                Log.e("errorr ", error);
                                JSONObject object = new JSONObject(error);
                                if (object.has("response")) {
                                    Utility.toastHelper("Invalid email. Please check your email.", mContext);
                                } else {
                                    Utility.toastHelper("Invalid email. Please check your email.", mContext);
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
