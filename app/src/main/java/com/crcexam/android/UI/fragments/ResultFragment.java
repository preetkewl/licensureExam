package com.crcexam.android.UI.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.database.DatabaseHandler;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.Utility;
import com.crcexam.android.utils.circleprogress.DonutProgress;
import com.google.gson.JsonObject;

import org.json.JSONArray;
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
public class ResultFragment extends Fragment implements View.OnClickListener {

    Toolbar mToolbar;
    DatabaseHandler db;
    private Context mContext;
    private View rootView;

    public ResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        mContext = getContext();
        rootView = inflater.inflate(R.layout.fragment_result, container, false);
        db = new DatabaseHandler(mContext);
        // setActionBar();
        ((DonutProgress) rootView.findViewById(R.id.donut_progress)).setShowText(false);
        return rootView;
    }

    /*private void setActionBar() {
        try {
            Log.e("alll  ", db.getAllQuestions().toString());
            JSONObject object = new JSONObject(getActivity().getIntent().getStringExtra("data"));
            double totalQuestion = object.getInt("totla_question");
            double totalCurrectAns = object.getInt("totla_currect");
            double per = (totalCurrectAns / totalQuestion) * 100;
            ((DonutProgress) rootView.findViewById(R.id.donut_progress)).setProgress((float) per);
            Log.e("totalQuestion ", totalQuestion + "  totalCurrectAns  " + totalCurrectAns + " per " + per);
            ((TextView) rootView.findViewById(R.id.txtCurrect)).setText("Corrrect answer " + Utility.twoDecimal(per + "") + "%");
            ((TextView) rootView.findViewById(R.id.txtIncorrect)).setText("Incorrrect answer " + Utility.twoDecimal((100 - per) + "") + "%");
            Log.e("fggff ", totalCurrectAns / totalQuestion + "");
            //  Log.e("fggff ",(totalCurrectAns/totalQuestion)*100+"");
            Log.e("fggff ", (totalCurrectAns / totalQuestion) * 100 + "");
            mToolbar = mToolbar.findViewById(R.id.toolbar_dash);
            //setSupportActionBar(mToolbar);
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setVisibility(View.GONE);
            ((TextView) mToolbar.findViewById(R.id.tv_title_center)).setVisibility(View.VISIBLE);
            ((TextView) mToolbar.findViewById(R.id.tv_title_center)).setText("Test Results");
            ((ImageView) mToolbar.findViewById(R.id.imgRight)).setVisibility(View.VISIBLE);
            setListener();
            if (totalCurrectAns == totalQuestion) {
                PreferenceClass.setStringPreference(mContext, Constant.MISSED_QUESTIONS, "");
            }

            try {
                ArrayList<JSONObject> lstHistory = new ArrayList<>();
                if (PreferenceClass.getStringPreferences(mContext, Constant.HISTORY).length() > 20) {
                    JSONArray array = new JSONArray(PreferenceClass.getStringPreferences(mContext, Constant.HISTORY));
                    for (int i = 0; i < array.length(); i++) {
                        lstHistory.add(array.getJSONObject(i));
                    }
                    JSONObject object1 = new JSONObject();
                    object1.put("date", Calendar.getInstance().getTimeInMillis());
                    object1.put("currect_answer", totalCurrectAns);
                    object1.put("total_question", totalQuestion);
                    object1.put("percentage", Utility.twoDecimal(per + ""));
                    lstHistory.add(object1);
                    PreferenceClass.setStringPreference(mContext, Constant.HISTORY, lstHistory.toString());

                } else {
                    JSONObject object1 = new JSONObject();
                    object1.put("date", Calendar.getInstance().getTimeInMillis());
                    object1.put("currect_answer", totalCurrectAns);
                    object1.put("total_question", totalQuestion);
                    object1.put("percentage", Utility.twoDecimal(per + ""));
                    lstHistory.add(object1);
                    PreferenceClass.setStringPreference(mContext, Constant.HISTORY, lstHistory.toString());
                }
                Log.e("HISTORY   ", PreferenceClass.getStringPreferences(mContext, Constant.HISTORY));


                Log.e("MISSED_QUESTIONS  ", PreferenceClass.getStringPreferences(mContext, Constant.MISSED_QUESTIONS));
               *//* JSONArray jsonArray = new JSONArray(PreferenceClass.getStringPreferences(mContext, Constant.MISSED_QUESTIONS));
                Log.e("MISSED_QUESTIONS len ", jsonArray.length() + "");*//*
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
*/
    private void setListener() {
        ((ImageView) mToolbar.findViewById(R.id.imgRight)).setOnClickListener(this);
        ((Button) rootView.findViewById(R.id.btnMissed)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgRight:
                //finish();
                loadFragment(new HomeFragment());
                break;
            case R.id.btnMissed:
                if (db.getAllMissedQuestionsList().size() > 0) {
                    //startActivity(new Intent(mContext, MultiOptionQuestionListActivity.class).putExtra("data", db.getAllMissedQuestionsList().toString()).putExtra("is_misssed", true));
                    //finish();
                    loadFragment(new HomeFragment());
                }/*if (PreferenceClass.getStringPreferences(mContext, Constant.MISSED_QUESTIONS).length() > 20) {
                    startActivity(new Intent(ResultActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", PreferenceClass.getStringPreferences(mContext, Constant.MISSED_QUESTIONS)
                    ).putExtra("is_misssed", true));
                    finish();
                }*/
                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void saveHistory(JsonObject jsonObject) {
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.saveHistory(Constant.API_KEY, Constant.SITE_ID, PreferenceClass.getStringPreferences(mContext, Constant.UserData.AUTH_TOKEN), jsonObject).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {


                            Log.e("res sh code ", response.code() + "");
                            if (response.code() == 200) {
                                String res = response.body().string();
                                Log.e("log object ", res + "");
                                JSONObject object = new JSONObject(res);
                                Log.e("log obj ", object + "");
                                if (object.getString("response").equalsIgnoreCase("success") && object.getInt("responsecode") == 0) {

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
