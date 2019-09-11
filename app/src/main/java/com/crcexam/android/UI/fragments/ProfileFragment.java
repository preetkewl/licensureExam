package com.crcexam.android.UI.fragments;


import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crcexam.android.R;
import com.crcexam.android.UI.auth.LoginActivity;
import com.crcexam.android.adapters.SpinnerAdapter;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.ConnectionDetector;
import com.crcexam.android.utils.PasswordValidator;
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


    private static final String TAG = "ProfileFragment";
    BottomSheetBehavior sheetBehavior, sheetBehaviorEditDob;
    RelativeLayout layoutBottomSheet, dobSheet;
    String fnameProfile, lnameProfile, moNumberProfile, address1Profile,
            address2Profile, cityProfile,
            zip_codeProfile;
    String[] spinnerValueOccupationType = {
            "AL",
            "AK",
            "AZ",
            "AR",
            "CA",
            "CO",
            "CT",
            "DE",
            "FL",
            "GA",
            "HI",
            "ID",
            "IL",
            "IN",
            "IA",
            "KS",
            "KY",
            "LA",
            "ME",
            "MD",
            "MA",
            "MI",
            "MN",
            "MS",
            "MO",
            "MT",
            "NE",
            "NV",
            "NH",
            "NJ",
            "NM",
            "NY",
            "NC",
            "ND",
            "OH",
            "OK",
            "OR",
            "PA",
            "RI",
            "SC",
            "SD",
            "TN",
            "TX",
            "UT",
            "VT",
            "VA",
            "WA",
            "WV",
            "WI",
            "WY"
    };
    private boolean runTextChange = false;
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
    private EditText editText_userName, editText_firstName, editText_lastName, editText_mobileNum, editText_addressone, editText_addresstwo, editText_City, editText_zipCode;
private Spinner stateSpinner ;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.e(TAG, "onTextChanged: " + s);
            Log.e(TAG, "setDataOnView onTextChanged: runTextChange is = " + runTextChange);
            if (runTextChange) {
                enableProfileUpdate();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public ProfileFragment() {
        // Required empty public constructor
    }


    private void enableProfileUpdate() {

        String username = editText_userName.getText().toString();
        String firstname = editText_firstName.getText().toString();
        String lastname = editText_lastName.getText().toString();
        String mNumber = editText_mobileNum.getText().toString();
        String address1 = editText_addressone.getText().toString();
        String address2 = editText_addresstwo.getText().toString();
        String city = editText_City.getText().toString();
//        String state = stateSpinner.getSelectedView().toString();
        String zip_code = editText_zipCode.getText().toString();

        if (username.equals("")) {
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        } else if (firstname.equals("")) {
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        } else if (lastname.equals("")) {
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        } else if (mNumber.equals("")) {
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        } else if (address1.equals("")) {
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        } else if (address2.equals("")) {
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        } else if (city.equals("")) {
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        }/*else if (state.equals("")){
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        }*/
        else if (zip_code.equals("")) {
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        } else {
            ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = getContext();
        connectionDetector = new ConnectionDetector(mContext);
        setListener();
        c = Calendar.getInstance();
        year_first = c.get(Calendar.YEAR);
        month_first = c.get(Calendar.MONTH);
        day_first = c.get(Calendar.DAY_OF_MONTH);
        ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setEnabled(false);
        editText_userName = rootView.findViewById(R.id.edtUsername);
        editText_firstName = rootView.findViewById(R.id.edtFirstName);
        editText_lastName = rootView.findViewById(R.id.edtLastName);
        editText_addressone = rootView.findViewById(R.id.edtAddress1);
        editText_addresstwo = rootView.findViewById(R.id.edtAddress2);
        editText_mobileNum = rootView.findViewById(R.id.edtTelephone);
        editText_City = rootView.findViewById(R.id.edtCity);
        editText_zipCode = rootView.findViewById(R.id.edtZIP);
        ((EditText) rootView.findViewById(R.id.edtUsername)).addTextChangedListener(mTextWatcher);
        ((EditText) rootView.findViewById(R.id.edtFirstName)).addTextChangedListener(mTextWatcher);
        ((EditText) rootView.findViewById(R.id.edtLastName)).addTextChangedListener(mTextWatcher);
        ((EditText) rootView.findViewById(R.id.edtAddress1)).addTextChangedListener(mTextWatcher);
        ((EditText) rootView.findViewById(R.id.edtAddress2)).addTextChangedListener(mTextWatcher);
        ((EditText) rootView.findViewById(R.id.edtTelephone)).addTextChangedListener(mTextWatcher);
        ((EditText) rootView.findViewById(R.id.edtCity)).addTextChangedListener(mTextWatcher);
        ((EditText) rootView.findViewById(R.id.edtZIP)).addTextChangedListener(mTextWatcher);
        setFontStyle();
        //editProfile(false);
        initBottomSheet();
        setStateSpinner();
        if (connectionDetector.isConnectingToInternet()) {
            progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                }
            });
            getProfile();
        } else {
            Utility.toastHelper(mContext.getResources().getString(R.string.check_network), mContext);
        }
        /*int id =bottomNav.getMenu().;
        bottomNav.setC*/


        return rootView;
    }


    private void setListener() {
        rootView.findViewById(R.id.btnUpdateProfile).setOnClickListener(this);
        // rootView.findViewById(R.id.btnEditProfile).setOnClickListener(this);
        rootView.findViewById(R.id.btnChangePassword).setOnClickListener(this);
        rootView.findViewById(R.id.edtExamDate).setOnClickListener(this);

    }

    private void setFontStyle() {
        ((TextView) rootView.findViewById(R.id.txtUserName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtFirstName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtLastName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtAddress1)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtAddress2)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtCity)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtState)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
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
//        ((Spinner) rootView.findViewById(R.id.spinner_state)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtZIP)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtTelephone)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((EditText) rootView.findViewById(R.id.edtExamDate)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((Button) rootView.findViewById(R.id.btnUpdateProfile)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        //((Button) rootView.findViewById(R.id.btnEditProfile)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));

        //username and exam date should not be editable.
        rootView.findViewById(R.id.edtUsername).setEnabled(true);
        rootView.findViewById(R.id.edtExamDate).setEnabled(false);

    }

  /*  @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mToolbar != null) {
            ((LinearLayout) mToolbar.findViewById(R.id.llLogOut)).setVisibility(View.GONE);
        }
    }*/


    private void setStateSpinner() {
        final Spinner spinnerOcc = (Spinner) rootView.findViewById(R.id.spinner_state);

        SpinnerAdapter adapter = new SpinnerAdapter(mContext, R.layout.spinner_layout);
        adapter.addAll(spinnerValueOccupationType);
        adapter.add(getString(R.string.select_state));
        spinnerOcc.setAdapter(adapter);
        spinnerOcc.setSelection(adapter.getCount());
        spinnerOcc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // TODO Auto-generated method stub
                if (spinnerOcc.getSelectedItem() == getString(R.string.select_state)) {
                    //Do nothing.
                } else {

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

    }

    private void initBottomSheet() {
        layoutBottomSheet = rootView.findViewById(R.id.bottom_sheet_change_password);

        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        layoutBottomSheet.findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "ChekcPassword onClick:Working ");
                validatePassword();
            }
        });
        layoutBottomSheet.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        rootView.findViewById(R.id.bg).setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        rootView.findViewById(R.id.bg).setVisibility(View.GONE);
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                rootView.findViewById(R.id.bg).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.bg).setAlpha(slideOffset);
            }
        });
    }

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
           /* case R.id.btnEditProfile:
               // editProfile(true);
                break;*/
            case R.id.btnChangePassword:
                if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    //  btnBottomSheet.setText("Close sheet");
                } else {
                    sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    // btnBottomSheet.setText("Expand sheet");
                }
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
        rootView.findViewById(R.id.edtUsername).setEnabled(isEnable);
        rootView.findViewById(R.id.edtFirstName).setEnabled(isEnable);
        rootView.findViewById(R.id.edtLastName).setEnabled(isEnable);
        rootView.findViewById(R.id.edtTelephone).setEnabled(isEnable);
        rootView.findViewById(R.id.edtAddress1).setEnabled(isEnable);
        rootView.findViewById(R.id.edtAddress2).setEnabled(isEnable);
        rootView.findViewById(R.id.edtCity).setEnabled(isEnable);
        rootView.findViewById(R.id.spinner_state).setEnabled(isEnable);
        rootView.findViewById(R.id.edtZIP).setEnabled(isEnable);
        rootView.findViewById(R.id.edtChangePassword).setEnabled(isEnable);
        rootView.findViewById(R.id.ChkReceiveEmails).setEnabled(isEnable);

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

    //Set Data on Profile Page/Screen
    private void setDataOnView(JSONObject objUser) {
        try {
            int position = 0;
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
            if (!objUser.isNull("StateAbbreviation"))
                for (int a = 0; a < spinnerValueOccupationType.length; a++) {
                    if (spinnerValueOccupationType[a].equalsIgnoreCase(objUser.getString("StateAbbreviation"))) {
                        position = a;
                    }
                }
            ((Spinner) rootView.findViewById(R.id.spinner_state)).setSelection(position);
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

            Log.e(TAG, "setDataOnView: runTextChange is = " + runTextChange);

            runTextChange = true;

            Log.e(TAG, "setDataOnView: runTextChange is = " + runTextChange);

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


    private void validatePassword() {
        PasswordValidator passwordValidator = new PasswordValidator();
        String oldPass = ((EditText) layoutBottomSheet.findViewById(R.id.edtPasswordOld)).getText().toString();
        String newPass = ((EditText) layoutBottomSheet.findViewById(R.id.edtPasswordNew)).getText().toString();
        String newPassConfirm = ((EditText) layoutBottomSheet.findViewById(R.id.edtPasswordConfirm)).getText().toString();

        if (newPass.trim().length() > 0 && !passwordValidator.validate(newPass) && (newPass.trim().length() < 8)) {
            Utility.toastHelper(getString(R.string.invalid_password), mContext);
        } else if (oldPass.isEmpty() || newPass.isEmpty() || newPassConfirm.isEmpty()) {
            Utility.toastHelper("Password cannot be empty", mContext);
        } else {


/*        if (((EditText) layoutBottomSheet.findViewById(R.id.edtPasswordOld)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("old password is required", mContext);
        } else if (((EditText) layoutBottomSheet.findViewById(R.id.edtPasswordNew)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("New Password is required", mContext);
        } else if (((EditText) layoutBottomSheet.findViewById(R.id.edtPasswordConfirm)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("Confirm Password is required", mContext);
        } else {*/
            if (connectionDetector.isConnectingToInternet()) {
                updatePassword();
            }
        }
    }

    private void updatePassword() {
        progressHUD.show();
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.updatePassword(Constant.API_KEY, Constant.SITE_ID, PreferenceClass.getStringPreferences(mContext, Constant.UserData.AUTH_TOKEN),
                        ((EditText) layoutBottomSheet.findViewById(R.id.edtPasswordOld)).getText().toString(),
                        ((EditText) layoutBottomSheet.findViewById(R.id.edtPasswordNew)).getText().toString())
                        .enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                try {
                                    progressHUD.dismiss();
                                    // hideLoader(indicatorView);
                                    Log.e("res code", "CheckPasswrord " + response.code() + "");
                                    if (response.isSuccessful() && response.code() == 200) {
                                        Log.e(TAG, "onResponse: CheckPasswrord body " + response.body());
                                        JSONObject object = new JSONObject(response.body().string());
                                        Log.e(TAG, "CheckPasswrord onResponse: " + object);
                                        Toast.makeText(mContext, object.getString("response"), Toast.LENGTH_SHORT).show();
                                        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                                        Log.e("chechpasswordlog obj ", object + "");
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
                                progressHUD.hide();
                                t.getLocalizedMessage();
                            }
                        });
            }
        } catch (Exception e) {
            progressHUD.hide();
            e.printStackTrace();
        }
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
        } else if (((EditText) rootView.findViewById(R.id.edtAddress1)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("PLease Enter Your First Address", mContext);
        } else if (((EditText) rootView.findViewById(R.id.edtAddress2)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("PLease Enter Your Second Address", mContext);
        } else if (((EditText) rootView.findViewById(R.id.edtCity)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("PLease Enter Your City", mContext);
        } else if (((Spinner) rootView.findViewById(R.id.spinner_state)).getSelectedItem().toString().trim().equalsIgnoreCase("Please Select Your State.")) {
            Utility.toastHelper("PLease Select Your State", mContext);
        } else if (((EditText) rootView.findViewById(R.id.edtZIP)).getText().toString().trim().isEmpty()) {
            Utility.toastHelper("PLease Enter Your ZIP Code", mContext);
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
                jsonObject.addProperty("State", ((Spinner) rootView.findViewById(R.id.spinner_state)).getSelectedItem().toString());
                jsonObject.addProperty("ZIP", ((EditText) rootView.findViewById(R.id.edtZIP)).getText().toString().trim());
                jsonObject.addProperty("StateAbbreviation", "FL");
                jsonObject.addProperty("ReceiveEmails", ((CheckBox) rootView.findViewById(R.id.ChkReceiveEmails)).isChecked());
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

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    private void updateProfile(JsonObject jsonObject) {
        Log.e(TAG, "jsonObject updateProfile: " + jsonObject);
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
                                    loadFragment(new HomeFragment());
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
