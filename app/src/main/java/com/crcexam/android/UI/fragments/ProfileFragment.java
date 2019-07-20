package com.crcexam.android.UI.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.UI.auth.LoginActivity;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.ConnectionDetector;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.ProgressHUD;
import com.crcexam.android.utils.Utility;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View rootView;
    private Context mContext;
    private Toolbar mToolbar;
    private ArrayList<String> lstSpinner = new ArrayList<>();
    private ArrayList<JSONObject> lstQuestions = new ArrayList<>();
    private ProgressHUD progressHUD;
    private int year_first, year_end, month_first, month_end, day_first, day_end;
    private Calendar c;
    private String startDate, endDate, chkInDate, chkOutDate;
    private String strExamDate = "";
    private ConnectionDetector connectionDetector;


    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = getContext();
        connectionDetector = new ConnectionDetector(mContext);
        setListener();
        c = Calendar.getInstance();
        year_first = c.get(Calendar.YEAR);
        month_first = c.get(Calendar.MONTH);
        day_first = c.get(Calendar.DAY_OF_MONTH);
        setFontStyle();
        editProfile(false);
        if (connectionDetector.isConnectingToInternet()) {
            progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                }
            });
            getProfile();
        } else {
            Utility.toastHelper(mContext.getResources().getString(R.string.check_network), mContext);
        }

        return rootView;
    }

    private void setListener() {
        ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setOnClickListener(this);
        ((Button) rootView.findViewById(R.id.btnEditProfile)).setOnClickListener(this);
        ((EditText) rootView.findViewById(R.id.edtExamDate)).setOnClickListener(this);

    }

    private void setFontStyle() {
        ((TextView) rootView.findViewById(R.id.txtUserName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtFirstName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtLastName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtAddress1)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtAddress2)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtCity)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtZIP)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtTelephone)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtExamDate)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        //((TextView) rootView.findViewById(R.id.tv_title)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Bold));
        ((EditText) rootView.findViewById(R.id.edtUsername)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtFirstName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtLastName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtAddress1)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtAddress2)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtCity)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtZIP)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtTelephone)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtExamDate)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((Button) rootView.findViewById(R.id.btnEditProfile)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));

        //username and exam date should not be editable.
        ((EditText) rootView.findViewById(R.id.edtUsername)).setEnabled(false);
        ((EditText) rootView.findViewById(R.id.edtExamDate)).setEnabled(false);

    }

  /*  @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mToolbar != null) {
            ((LinearLayout) mToolbar.findViewById(R.id.llLogOut)).setVisibility(View.GONE);
        }
    }*/

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.imv_back:
                finish();
                break;*/
            case R.id.btnUpdateProfile:
                validateProfile();
                break;
            case R.id.edtExamDate:
                showDate();
                break;
            case R.id.btnEditProfile:
                editProfile(true);
                break;

           /* case R.id.llLogOut:
                logout();
                break;*/
        }
    }

    private void logout() {
        PreferenceClass.clearPreference(mContext);
        Intent in = new Intent(getActivity(), LoginActivity.class);
        startActivity(in);
    }

    private void editProfile(boolean isEnable) {
        ((EditText) rootView.findViewById(R.id.edtFirstName)).setEnabled(isEnable);
        ((EditText) rootView.findViewById(R.id.edtLastName)).setEnabled(isEnable);
        ((EditText) rootView.findViewById(R.id.edtTelephone)).setEnabled(isEnable);
        ((EditText) rootView.findViewById(R.id.edtAddress1)).setEnabled(isEnable);
        ((EditText) rootView.findViewById(R.id.edtAddress2)).setEnabled(isEnable);
        ((EditText) rootView.findViewById(R.id.edtCity)).setEnabled(isEnable);
        ((EditText) rootView.findViewById(R.id.edtZIP)).setEnabled(isEnable);
        ((CheckBox) rootView.findViewById(R.id.ChkReceiveEmails)).setEnabled(isEnable);

    }

    private void getProfile() {
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.getProfile(Constant.API_KEY, Constant.SITE_ID, PreferenceClass.getStringPreferences(mContext, Constant.UserData.AUTH_TOKEN)).enqueue(new Callback<ResponseBody>() {
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
                                    Log.e("account ", object.getJSONObject("account").toString());
                                    setDataOnView(object.getJSONObject("account"));
                                } else if (object.getInt("responsecode") == 201) {
                                    Utility.toastHelper(object.getString("response"), mContext);
                                } else if (object.getInt("responsecode") == 301) {
                                    //  Utility.toastHelper(object.getString("response"), mContext);
                                    resetData();
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

    private void setDataOnView(JSONObject objUser) {

        try {
            if (!objUser.isNull("Username"))
                ((EditText) rootView.findViewById(R.id.edtUsername)).setText(objUser.getString("Username"));
            if (!objUser.isNull("FirstName"))
                ((EditText) rootView.findViewById(R.id.edtFirstName)).setText(objUser.getString("FirstName"));
            if (!objUser.isNull("LastName"))
                ((EditText) rootView.findViewById(R.id.edtLastName)).setText(objUser.getString("LastName"));
            if (!objUser.isNull("Address1"))
                ((EditText) rootView.findViewById(R.id.edtAddress1)).setText(objUser.getString("Address1"));
            if (!objUser.isNull("Address2"))
                ((EditText) rootView.findViewById(R.id.edtAddress2)).setText(objUser.getString("Address2"));
            if (!objUser.isNull("City"))
                ((EditText) rootView.findViewById(R.id.edtCity)).setText(objUser.getString("City"));
            if (!objUser.isNull("ZIP"))
                ((EditText) rootView.findViewById(R.id.edtZIP)).setText(objUser.getString("ZIP"));
            if (!objUser.isNull("Telephone"))
                ((EditText) rootView.findViewById(R.id.edtTelephone)).setText(objUser.getString("Telephone"));
            if (!objUser.isNull("ReceiveEmails"))
                ((CheckBox) rootView.findViewById(R.id.ChkReceiveEmails)).setChecked(objUser.getBoolean("ReceiveEmails"));
            if (!objUser.isNull("ExamDate")) {
                Log.e("edtExamDate  ", objUser.getString("ExamDate"));
                //   Log.e("dfd   ", Utility.parseTime(objUser.getString("ExamDate"), "yyyy-MM-dd'T'HH:mm:ss.SSS", "dd-MM-yyyy"));
               /* if (objUser.getString("ExamDate").contains("Z"))
                    ((EditText) rootView.findViewById(R.id.edtExamDate)).setText(Utility.parseTime(objUser.getString("ExamDate"), "yyyy-MM-dd'T'HH:mm:ss.SSSZ", "MM-dd-yyyy") + "");
                else*/
                ((EditText) rootView.findViewById(R.id.edtExamDate)).setText(Utility.parseTime(objUser.getString("ExamDate"), "yyyy-MM-dd'T'HH:mm:ss.SSS", "MM-dd-yyyy") + "");
                startDate = objUser.getString("ExamDate");
                // ((EditText) rootView.findViewById(R.id.edtTelephone)).setText(objUser.getString("ExamDate"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void resetData() {
        PreferenceClass.clearPreference(mContext);
        Intent intent = new Intent(mContext, LoginActivity.class);
        //Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    private void validateProfile() {
        if (((EditText) rootView.findViewById(R.id.edtUsername)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("username is required", mContext);
        } else if (((EditText) rootView.findViewById(R.id.edtFirstName)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("First name is required", mContext);
        } else if (((EditText) rootView.findViewById(R.id.edtLastName)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("Last name is required", mContext);
        } else if (((EditText) rootView.findViewById(R.id.edtTelephone)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("Telephone is required", mContext);
        } else {

            if (connectionDetector.isConnectingToInternet()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("Username", ((EditText) rootView.findViewById(R.id.edtUsername)).getText().toString().trim());
                jsonObject.addProperty("FirstName", ((EditText) rootView.findViewById(R.id.edtFirstName)).getText().toString().trim());
                jsonObject.addProperty("LastName", ((EditText) rootView.findViewById(R.id.edtLastName)).getText().toString().trim());
                jsonObject.addProperty("Telephone", ((EditText) rootView.findViewById(R.id.edtTelephone)).getText().toString().trim());
                jsonObject.addProperty("Address1", ((EditText) rootView.findViewById(R.id.edtAddress1)).getText().toString().trim());
                jsonObject.addProperty("Address2", ((EditText) rootView.findViewById(R.id.edtAddress2)).getText().toString().trim());
                jsonObject.addProperty("City", ((EditText) rootView.findViewById(R.id.edtCity)).getText().toString().trim());
                jsonObject.addProperty("ZIP", ((EditText) rootView.findViewById(R.id.edtZIP)).getText().toString().trim());
                jsonObject.addProperty("StateAbbreviation", "FL");
                jsonObject.addProperty("ReceiveEmails", ((CheckBox) rootView.findViewById(R.id.ChkReceiveEmails)).isChecked() ? true : false);
                Log.e("dfdf g", Utility.parseTime(((EditText) rootView.findViewById(R.id.edtExamDate)).getText().toString(), "MM-dd-yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS") + "");
                if (!startDate.isEmpty())
                    jsonObject.addProperty("ExamDate", startDate);
                //jsonObject.addProperty("ExamDate", Utility.parseTime(((EditText) rootView.findViewById(R.id.edtExamDate)).getText().toString(), "MM-dd-yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS") + "Z");
                // jsonObject.addProperty("ExamDate", Utility.parseTime(((EditText) rootView.findViewById(R.id.edtExamDate)).getText().toString(), "MM/dd/yyyy", "yyyy-MM-dd'T'HH:mm:ss.SSS"));
                jsonObject.addProperty("CreateDate", Utility.getCurrentDate() + "Z");
                Log.e("jsonObject ", jsonObject + "");
                progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                    }
                });
                updateProfile(jsonObject);
            }
        }

    }

    private void updateProfile(JsonObject jsonObject) {

        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.updateProfile(Constant.API_KEY, Constant.SITE_ID, PreferenceClass.getStringPreferences(mContext, Constant.UserData.AUTH_TOKEN), "application/x-www-form-urlencoded", jsonObject).enqueue(new Callback<ResponseBody>() {
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
                                    Utility.toastHelper(object.getString("response"), mContext);
                                } else if (object.getInt("responsecode") == 201) {
                                    Utility.toastHelper(object.getString("response"), mContext);
                                } else if (object.getInt("responsecode") == 301) {
                                    // Utility.toastHelper(object.getString("response"), mContext);
                                    resetData();
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

    private void showDate() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(mContext, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int selectedYear, int selectedMonth, int selectedDate) {
                year_first = selectedYear;
                month_first = selectedMonth;
                day_first = selectedDate;
                if (day_first < 10 && (month_first + 1) < 10) {
                    ((EditText) rootView.findViewById(R.id.edtExamDate)).setText(new StringBuilder().append("0").append(month_first + 1).append("-0").append(day_first).append("-" + year_first));
                    startDate = ((EditText) rootView.findViewById(R.id.edtExamDate)).getText().toString().trim();
                } else if (day_first < 10) {
                    ((EditText) rootView.findViewById(R.id.edtExamDate)).setText(new StringBuilder().append(month_first + 1).append("-0").append(day_first).append("-").append(year_first));
                    startDate = ((EditText) rootView.findViewById(R.id.edtExamDate)).getText().toString().trim();
                } else if ((month_first + 1) < 10) {
                    ((EditText) rootView.findViewById(R.id.edtExamDate)).setText(new StringBuilder().append("0").append(month_first + 1).append("-").append(day_first).append("-").append(year_first));
                    startDate = ((EditText) rootView.findViewById(R.id.edtExamDate)).getText().toString().trim();
                } else {
                    ((EditText) rootView.findViewById(R.id.edtExamDate)).setText(new StringBuilder().append(month_first + 1).append("-").append(day_first).append("-").append(year_first));
                    startDate = ((EditText) rootView.findViewById(R.id.edtExamDate)).getText().toString().trim();
                }
                // chkInDate = startDate;
                // ((EditText) findViewById(R.id.edtExamDate)).setText(Utility.parseDate(((EditText) findViewById(R.id.edtExamDate)).getText().toString()));
                /*if (((EditText) findViewById(R.id.etdCheckout)).getText().toString().length() > 0) {
                    if (Utility.compareTwoDates(((EditText) findViewById(R.id.edtExamDate)).getText().toString(), ((EditText) findViewById(R.id.etdCheckout)).getText().toString())) {

                    } else {
                        ((EditText) findViewById(R.id.etdCheckout)).setText("");
                    }
                }*/
            }
        }, year_first, month_first, day_first);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }
}
