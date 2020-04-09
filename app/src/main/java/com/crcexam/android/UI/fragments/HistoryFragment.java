package com.crcexam.android.UI.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.adapters.HistoryAdapter;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.ConnectionDetector;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.ProgressHUD;
import com.crcexam.android.utils.Utility;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class HistoryFragment extends Fragment implements View.OnClickListener{

    View rootView;
    Context mContext;
    RecyclerView recyclerView;
    ArrayList<JSONObject> historyArraylist = new ArrayList<>();
    HistoryAdapter historyAdapter;
    private ProgressHUD progressHUD;
    private ConnectionDetector connectionDetector;
    Toolbar mToolbar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        mContext = getActivity();
        connectionDetector = new ConnectionDetector(mContext);
        adapter();
        /*if (connectionDetector.isConnectingToInternet()) {
            progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                }
            });
            //getHistory();
        } else {
            Utility.toastHelper(mContext.getResources().getString(R.string.check_network), mContext);
        }*/

        setActionBar();
        return rootView;
    }

    private void setActionBar() {
        try {
            mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_dash);
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setVisibility(View.VISIBLE);
            ((ImageView) mToolbar.findViewById(R.id.imgHome)).setVisibility(View.GONE);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setOnClickListener(this);
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setText("Result");
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setVisibility(View.VISIBLE);
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.OpenSans_Bold));
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void adapter() {
        try {
            if (PreferenceClass.getStringPreferences(mContext, Constant.HISTORY).length() > 20) {
                JSONArray array = new JSONArray(PreferenceClass.getStringPreferences(mContext, Constant.HISTORY));
                for (int i = 0; i < array.length(); i++) {
                    historyArraylist.add(array.getJSONObject(i));
                }
            }else {
                ((TextView)rootView.findViewById(R.id.txtNot)).setVisibility(View.VISIBLE);
            }
            Log.e("historyArraylist",historyArraylist.size()+"");
            recyclerView = rootView.findViewById(R.id.rv_historylist);
//            JSONObject obj = new JSONObject(loadJSONFromAsset());
//            JSONArray jsonArray = obj.getJSONArray("history");
//            for (int i = 0; i < jsonArray.length(); i++) {
//                historyArraylist.add(jsonArray.getJSONObject(i));
//            }
            Collections.reverse(historyArraylist);
            historyAdapter = new HistoryAdapter(getActivity(), historyArraylist);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(historyAdapter);
            recyclerView.setNestedScrollingEnabled(false);
            historyAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getHistory(JsonObject jsonObject){
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.getHistoryResult(Constant.API_KEY, Constant.SITE_ID, PreferenceClass.getStringPreferences(mContext, Constant.UserData.AUTH_TOKEN),"application/x-www-form-urlencoded",jsonObject).enqueue(new Callback<ResponseBody>() {
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
                                    //setDataOnView(object.getJSONObject("account"));
                                } else if (object.getInt("responsecode") == 201) {
                                    Utility.toastHelper(object.getString("response"), mContext);
                                } else if (object.getInt("responsecode") == 301) {
                                    //  Utility.toastHelper(object.getString("response"), mContext);
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


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("history.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    public void onClick(View v) {
        try {
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setVisibility(View.GONE);
            ((ImageView) mToolbar.findViewById(R.id.imgHome)).setVisibility(View.VISIBLE);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setOnClickListener(this);
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setVisibility(View.GONE);
            Objects.requireNonNull(getActivity()).onBackPressed();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
