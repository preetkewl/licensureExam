package com.crcexam.android.UI.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import com.crcexam.android.R;
import com.crcexam.android.adapters.StoreAdapter;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StoreFragment extends Fragment implements View.OnClickListener, RecyclerviewClickListner, PurchasesUpdatedListener {

    static final String TAG = "StoreFragment";
    View rootView;
    Context mContext;
    RecyclerView recyclerView;
    ArrayList<JSONObject> homeArraylist = new ArrayList<>();
    StoreAdapter mAdapter;
    RecyclerviewClickListner recyclerviewClickListner;
    ProgressHUD progressHUD;
    PreferenceClass preferenceClass;
    Toolbar mToolbar;
    HashSet<String> hashSet = new HashSet<String>();
    ArrayList<JSONObject> lstMultipleChoice = new ArrayList<>();

    private BillingClient billingClient;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_store, container, false);
        mContext = getActivity();
        preferenceClass = new PreferenceClass();
        setUpBillingClient();
        recyclerviewClickListner = StoreFragment.this;
        hashSet = (HashSet<String>) PreferenceClass.getExams(getActivity());
        adapter();
        getAllExamList();
        setActionBar();
        return rootView;
    }

    private void setUpBillingClient() {
        billingClient = BillingClient.newBuilder(getContext()).setListener(this).enablePendingPurchases().build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                loadProductList();
            }

            @Override
            public void onBillingServiceDisconnected() {
                Toast.makeText(mContext, "Something went wrong, Try again later!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProductList() {
        if (billingClient.isReady()) {
            SkuDetailsParams detailsParams = SkuDetailsParams.newBuilder().setSkusList(
                    Arrays.asList("5", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18"))
                    .setType(BillingClient.SkuType.INAPP)
                    .build();

            billingClient.querySkuDetailsAsync(detailsParams, new SkuDetailsResponseListener() {
                @Override
                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> list) {

                    mAdapter = new StoreAdapter(mContext, lstMultipleChoice, list, billingClient, recyclerviewClickListner);
                    recyclerView.setAdapter(mAdapter);


                }
            });
        }
    }

    private void setActionBar() {
        try {
            mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_dash);
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setVisibility(View.VISIBLE);
            ((ImageView) mToolbar.findViewById(R.id.imgHome)).setVisibility(View.GONE);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setOnClickListener(this);
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setText("Store");
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
            recyclerView = rootView.findViewById(R.id.rv_storelist);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    private void getAllExamList() {
        Log.e(TAG, "doubleCheck getAllExamList: called");
        try {
            progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
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
                            Log.e(TAG, response.code() + " code doubleCheck ");
                            homeArraylist.clear();
                            if (response.code() == 200) {
                                JSONArray array = new JSONArray(response.body().string());

                                for (int i = 0; i < array.length(); i++) {
                                    Log.e(TAG, "doubleCheck onResponse: getJSONObject = " + array.getJSONObject(i));
                                    //homeArraylist.add(array.getJSONObject(i));
//                                    if (!array.getJSONObject(i).getBoolean("isFree")) {
//                                        lstMultipleChoice.add(array.getJSONObject(i));
//                                    }
                                    lstMultipleChoice.add(array.getJSONObject(i));
                                }
                                PreferenceClass.setStringPreference(mContext, Constant.STORE_DATA, lstMultipleChoice.toString());
                                mAdapter.notifyDataSetChanged();
                            } else {
                                String error = response.errorBody().string();
                                Log.e("IabCheck error  ", error + "");
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
            case R.id.imgback:
                try {
                    ((ImageView) mToolbar.findViewById(R.id.imgback)).setVisibility(View.GONE);
                    ((ImageView) mToolbar.findViewById(R.id.imgHome)).setVisibility(View.VISIBLE);
                    ((ImageView) mToolbar.findViewById(R.id.imgback)).setOnClickListener(this);
                    ((TextView) mToolbar.findViewById(R.id.tv_title)).setVisibility(View.GONE);
                    Objects.requireNonNull(getActivity()).onBackPressed();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onItemClick(View view, int position, String response) {
        Log.e("Data: ", position + response);
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> list) {

        try {
            Purchase purchase = list.get(0);
            hashSet.add(purchase.getSku());
            PreferenceClass.setExams(getActivity(),hashSet);
            Toast.makeText(mContext, "Congratulations this item is now available", Toast.LENGTH_SHORT).show();
        } catch (Exception ex){
            ex.printStackTrace();
            Toast.makeText(mContext, "This item is currently unavailable/You already have this item!", Toast.LENGTH_SHORT).show();
        }
    }
}