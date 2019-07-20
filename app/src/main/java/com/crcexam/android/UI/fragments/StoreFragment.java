package com.crcexam.android.UI.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.crcexam.android.R;
import com.crcexam.android.adapters.StoreAdapter;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.interfaces.RecyclerviewClickListner;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.ProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StoreFragment extends Fragment implements View.OnClickListener, RecyclerviewClickListner {
    static final String TAG = "StoreFragment";
    private static final String ACTIVITY_NUMBER = "activity_num";
    private static final String LOG_TAG = "iabv3";
    // PRODUCT & SUBSCRIPTION IDS
    private static final String PRODUCT_ID = "com.anjlab.test.iab.s2.p5";
    private static final String SUBSCRIPTION_ID = "com.anjlab.test.iab.subs1";
    private static final String LICENSE_KEY = null; // PUT YOUR MERCHANT KEY HERE;
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID = null;
    View rootView;
    Context mContext;
    RecyclerView recyclerView;
    ArrayList<JSONObject> homeArraylist = new ArrayList<>();
    StoreAdapter mAdapter;
    RecyclerviewClickListner recyclerviewClickListner;
    ProgressHUD progressHUD;
    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_store, container, false);
        mContext = getActivity();
        //((TextView) rootView.findViewById(R.id.txtCrc)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Bold));
        //init();
        recyclerviewClickListner = StoreFragment.this;
        adapter();
        getAllExamList();
        initBillingLib();
        return rootView;
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

    private void initBillingLib() {
        if (!BillingProcessor.isIabServiceAvailable(getActivity())) {
            Toast.makeText(mContext, "In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16", Toast.LENGTH_SHORT).show();
        }

        bp = new BillingProcessor(getActivity(), LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //  Toast.makeText(mContext,"onProductPurchased: " + productId,Toast.LENGTH_SHORT).show();
                //  updateTextViews();
            }

            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                //  Toast.makeText(mContext,"onBillingError: " + Integer.toString(errorCode),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBillingInitialized() {
                //   Toast.makeText(mContext,"onBillingInitialized",Toast.LENGTH_SHORT).show();
                readyToPurchase = true;
                // updateTextViews();
            }

            @Override
            public void onPurchaseHistoryRestored() {
                String sku1 = "";
                //Toast.makeText(mContext,"onPurchaseHistoryRestored",Toast.LENGTH_SHORT).show();
                for (String sku : bp.listOwnedProducts())
                    sku1 = sku;
                Log.d(LOG_TAG, "Owned Managed Product: " + sku1);
                for (String sku : bp.listOwnedSubscriptions())
                    sku1 = sku;
                Log.d(LOG_TAG, "Owned Subscription: " + sku1);

                // Toast.makeText(mContext,"onProductPurchased: " + sku1,Toast.LENGTH_SHORT).show();

                Log.d(LOG_TAG, "Owned Subscription: " + "fghffgh fh");
                // updateTextViews();
            }
        });

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("store.json");
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

    private void getAllExamList() {
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
                            Log.e("onResponse  ", response.code() + "");
                            homeArraylist.clear();
                            ArrayList<JSONObject> lstMultipleChoice = new ArrayList<>();
                            if (response.code() == 200) {
                                JSONArray array = new JSONArray(response.body().string());
                                Log.e("array  ", array + "");
                                for (int i = 0; i < array.length(); i++) {
                                    //homeArraylist.add(array.getJSONObject(i));
                                    if (!array.getJSONObject(i).getBoolean("isFree")) {
                                        lstMultipleChoice.add(array.getJSONObject(i));
                                    }

                                }
                                PreferenceClass.setStringPreference(mContext, Constant.STORE_DATA, lstMultipleChoice.toString());

                                mAdapter = new StoreAdapter(mContext, lstMultipleChoice, recyclerviewClickListner);
                                recyclerView.setAdapter(mAdapter);
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
      /*  switch (v.getId()) {
            case R.id.tv_testAmount:
                try {
                    JSONObject obj = new JSONObject(v.getTag().toString());
                    Log.e("dffdg ", obj.toString());
                    onBuyGasButtonClicked();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }*/
    }

    @Override
    public void onItemClick(View view, int position, String response) {
        if (!readyToPurchase) {
            Toast.makeText(mContext, "Billing not initialized.", Toast.LENGTH_SHORT).show();
            return;
        }
        bp.purchase(getActivity(), PRODUCT_ID);
    }
}

