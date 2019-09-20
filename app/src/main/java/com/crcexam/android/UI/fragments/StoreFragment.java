package com.crcexam.android.UI.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.crcexam.android.InAppBilling.IabHelper;
import com.crcexam.android.InAppBilling.IabResult;
import com.crcexam.android.InAppBilling.Inventory;
import com.crcexam.android.InAppBilling.Purchase;
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

/*import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;*/

public class StoreFragment extends Fragment implements View.OnClickListener, RecyclerviewClickListner {
    static final String TAG = "StoreFragment";
    private static final String ACTIVITY_NUMBER = "activity_num";
    private static final String LOG_TAG = "iabv3";
    // PRODUCT & SUBSCRIPTION IDS
    //private static final String PRODUCT_ID = "com.anjlab.test.iab.s2.p5";
    //for testing -
    private static final String PRODUCT_ID = "android.test.purchased";
    private static final String PRODUCT_ID_SKU = "android.test.purchased";
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
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {

            Log.v(TAG, "IabCheck purchase finished result " + result + " purchase " + purchase);

            if (purchase != null) {

                if (purchase.getSku().equals(PRODUCT_ID_SKU)) {

                    Toast.makeText(getActivity(), "Purchase successful!", Toast.LENGTH_SHORT).show();
                    //consume();
                 /*   premiumEditor.putBoolean("hasPremium", true);
                    premiumEditor.commit();*/
                }
                if (result.getResponse() == 7) {
                    Toast.makeText(mContext, "Item Already Owned", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (result.getResponse() == 7) {
                    Toast.makeText(mContext, "Item Already Owned", Toast.LENGTH_SHORT).show();
                    // consume();
                }
                return;
            }
            if (result.isFailure()) {
                return;
            }
        }
    };
    private Inventory mInventory;
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            mInventory = inventory;

            if (result.isFailure()) {
                // handle error here

                Log.v(TAG, "IabCheck failure in checking if user has purchases");
            } else {
                // does the user have the premium upgrade?
                if (inventory.hasPurchase("premium_version")) {

                /*    premiumEditor.putBoolean("hasPremium", true);
                    premiumEditor.commit();*/

                    Log.v(TAG, "IabCheck Has purchase, saving in storage");

                } else {

                   /* premiumEditor.putBoolean("hasPremium", false);
                    premiumEditor.commit();*/

                    Log.v(TAG, "IabCheck Doesn't have purchase, saving in storage");

                }
            }
        }
    };
    //  private BillingProcessor bp;
    private boolean readyToPurchase = false;
    private IabHelper mHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_store, container, false);
        mContext = getActivity();
        //((TextView) rootView.findViewById(R.id.txtCrc)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Bold));
        //init();
        recyclerviewClickListner = StoreFragment.this;
        adapter();
        //Previous code -
        getAllExamList();
        //  initBillingLib();
        //new Code -
        initIab();
        return rootView;
    }

    private void initIab() {
        mHelper = new IabHelper(getActivity(), getResources().getString(R.string.base64_encoded_RSA_public_key));
        mHelper.enableDebugLogging(false); //set to false in real app


        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.

                    if (result.getResponse() == 3) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("In app billing")
                                .setMessage("This device is not compatible with In App Billing, so" +
                                        " you may not be able to buy the premium version on your phone. ")
                                .setPositiveButton("Okay", null)
                                .show();
                    }
                    Log.v(TAG, "IabCheck Problem setting up In-app Billing: " + result);
                } else {
                    Log.v(TAG, "IabCheck YAY, in app billing set up! " + result);
                    try {
                        mHelper.queryInventoryAsync(mGotInventoryListener); //Getting inventory of purchases and assigning listener
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    public void buyPremium() {
        Log.e(TAG, "IabCheck buyPremium: called");
        try {

            mHelper.flagEndAsync();//If any async is going, make sure we have it stop eventually
            mHelper.launchPurchaseFlow(getActivity(), PRODUCT_ID_SKU, 9, mPurchaseFinishedListener, "SECURITYSTRING"); //Making purchase request and attaching listener
        } catch (Exception e) {
            e.printStackTrace();

            mHelper.flagEndAsync();//If any async is going, make sure we have it stop eventually
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error")
                    .setMessage("An error occurred in buying the premium version. Please try again.")
                    .setPositiveButton("Okay", null)
                    .show();
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

        Log.d(TAG, "IabCheck onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {

        } else
            Log.d(TAG, "IabCheck onActivityResult handled by IABUtil.");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null)
            try {
                mHelper.dispose();
                mHelper = null;

            } catch (IabHelper.IabAsyncInProgressException e) {
                e.printStackTrace();
            }
    }

   /* private void initBillingLib() {
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
                Log.d(LOG_TAG, "IabCheck Owned Managed Product: " + sku1);
                for (String sku : bp.listOwnedSubscriptions())
                    sku1 = sku;
                Log.d(LOG_TAG, "IabCheck Owned Subscription: " + sku1);

                // Toast.makeText(mContext,"onProductPurchased: " + sku1,Toast.LENGTH_SHORT).show();

                Log.d(LOG_TAG, "IabCheck Owned Subscription: " + "fghffgh fh");
                // updateTextViews();
            }
        });

    }*/

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
                            Log.e("IabCheck onResponse  ", response.code() + "");
                            homeArraylist.clear();
                            ArrayList<JSONObject> lstMultipleChoice = new ArrayList<>();
                            if (response.code() == 200) {
                                JSONArray array = new JSONArray(response.body().string());
                                Log.e("IabCheck array  ", array + "");
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
        /*if (!readyToPurchase) {
            Toast.makeText(mContext, "Billing not initialized.", Toast.LENGTH_SHORT).show();
            return;
        }*/
        if (mHelper != null) {
            //consume();
            /// mHelper.flagEndAsync();
            buyPremium();
        }
        //bp.purchase(getActivity(), PRODUCT_ID);
    }

    public void consume() {

        //MAKING A QUERY TO GET AN ACCURATE INVENTORY
        try {
            mHelper.flagEndAsync(); //If any async is going, make sure we have it stop eventually

            mHelper.queryInventoryAsync(mGotInventoryListener); //Getting inventory of purchases and assigning listener

            if (mInventory.getPurchase(PRODUCT_ID_SKU) == null) {
                Toast.makeText(getActivity(), "Already consumed!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "IabCheck consume: Already consumed! ");
            }
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
            Log.e(TAG, "IabCheck consume: Error, try again ");

            Toast.makeText(getActivity(), "Error, try again", Toast.LENGTH_SHORT).show();
            mHelper.flagEndAsync();//If any async is going, make sure we have it stop eventually
        }

        //ACTUALLY CONSUMING
        try {
            mHelper.flagEndAsync();//If any async is going, make sure we have it stop eventually

            this.mHelper.consumeAsync(mInventory.getPurchase(PRODUCT_ID_SKU), new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase paramAnonymousPurchase, IabResult paramAnonymousIabResult) {
//resell the gas to them
                }
            });

            return;
        } catch (IabHelper.IabAsyncInProgressException localIabAsyncInProgressException) {
            localIabAsyncInProgressException.printStackTrace();
            Toast.makeText(getActivity(), "ASYNC IN PROGRESS ALREADY!!!!" + localIabAsyncInProgressException, Toast.LENGTH_LONG).show();
            Log.v("myTag", "IabCheck ASYNC IN PROGRESS ALREADY!!!");
            mHelper.flagEndAsync();
        }
    }
}

