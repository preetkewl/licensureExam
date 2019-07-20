package com.crcexam.android.UI.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.adapters.ExamListAdapter;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.interfaces.RecyclerviewClickListner;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.ProgressHUD;
import com.crcexam.android.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HomeFragment extends Fragment implements RecyclerviewClickListner, View.OnClickListener {

    View rootView;
    Context mContext;
    RecyclerView recyclerView;
    ArrayList<JSONObject> homeArraylist = new ArrayList<>();
    ExamListAdapter homeAdapter;
    RecyclerviewClickListner recyclerviewClickListner;
    ProgressHUD progressHUD;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();
        setFontStyle();
        setListener();
        return rootView;
    }

    private void setFontStyle() {
        ((TextView) rootView.findViewById(R.id.txtDashboard)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtCrc)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Bold));
        ((TextView) rootView.findViewById(R.id.txtResult)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtProfile)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtRef)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtDirMsg)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtDirMsgTitle)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((Button) rootView.findViewById(R.id.btnSampleQuiz)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((Button) rootView.findViewById(R.id.btnSampleFlip)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((Button) rootView.findViewById(R.id.btnExamPro)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));

    }

    private void setListener() {
        ((Button) rootView.findViewById(R.id.btnSampleQuiz)).setOnClickListener(this);
        ((Button) rootView.findViewById(R.id.btnSampleFlip)).setOnClickListener(this);
        ((Button) rootView.findViewById(R.id.btnExamPro)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.txtProfile)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.txtRef)).setOnClickListener(this);

    }

    @Override
    public void onItemClick(View view, int position, String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.getString("contentType").equalsIgnoreCase("FlipSet")) {
                //startActivity(new Intent(getActivity(), FlipSetSelectionActivity.class).putExtra("data", response));

            } else {
               // startActivity(new Intent(getActivity(), SelectionActivity.class).putExtra("data", response));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getAllExamList() {
        try {
            progressHUD = ProgressHUD.show(getActivity(), "", true, false, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                }
            });
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.getAllExamList().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (progressHUD.isShowing() && progressHUD != null) {
                                progressHUD.dismiss();
                            }
                            Log.e("onResponse  ", response.code() + "");
                            homeArraylist.clear();
                            ArrayList<JSONObject> lstBuy = new ArrayList<>();
                            if (response.code() == 200) {
                                JSONArray array = new JSONArray(response.body().string());
                                Log.e("array  ", array + "");
                                for (int i = 0; i < array.length(); i++) {
                                    homeArraylist.add(array.getJSONObject(i));
                                    if (!array.getJSONObject(i).getBoolean("isFree")) {
                                        lstBuy.add(array.getJSONObject(i));
                                    }
                                }
                                PreferenceClass.setStringPreference(mContext, Constant.STORE_DATA, lstBuy.toString());
                                homeAdapter = new ExamListAdapter(getActivity(), homeArraylist, recyclerviewClickListner);

                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setAdapter(homeAdapter);
                                recyclerView.setNestedScrollingEnabled(false);
                            } else {
                                String error = response.errorBody().string();
                                Log.e("error  ", error + "");
                                JSONObject object = new JSONObject(error);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSampleQuiz:
                Log.e("contentType mul ", "MultipleChoice");
                //startActivity(new Intent(getActivity(), MultipleSelectQstCountActivity.class).putExtra("contentType", "MultipleChoice"));
                break;
            case R.id.btnSampleFlip:
                Log.e("contentType flip ", "Flipset");
               // startActivity(new Intent(getActivity(), MultipleSelectQstCountActivity.class).putExtra("contentType", "FlipSet"));
                break;
            case R.id.txtProfile:
              //  startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;
            case R.id.btnExamPro:
                Fragment fragment;
                fragment = new StoreFragment();
                loadFragment(fragment);
               // navigation.setSelectedItemId(R.id.navigation_store);
                break;

            case R.id.txtRef:
                shareLink();
                break;
        }
    }


    private void loadFragment(Fragment fragment) {
        // load fragment
      /*  FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();*/
    }

    private void shareLink() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Share App");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.crc.android");
        startActivity(Intent.createChooser(share, "Share App"));
    }


}
