package com.crcexam.android.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.SkuDetails;
import com.crcexam.android.R;
import com.crcexam.android.UI.fragments.StoreFragment;
import com.crcexam.android.interfaces.IProductListner;
import com.crcexam.android.interfaces.RecyclerviewClickListner;
import com.crcexam.android.utils.PreferenceClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
    private static final String TAG = "StoreAdapter";
    private Context mContext;
    private ArrayList<JSONObject> testList;
    private List<SkuDetails> skuDetails;
    private RecyclerviewClickListner listener;
    HashSet<String> hashSet = new HashSet<String>();
    BillingClient client;


    public StoreAdapter(Context mContext, ArrayList<JSONObject> testList, List<SkuDetails> skuDetailsList, BillingClient billingClient, RecyclerviewClickListner listener) {
        this.mContext = mContext;
        this.testList = testList;
        this.listener = listener;
        this.skuDetails = skuDetailsList;
        this.client = billingClient;
        hashSet = (HashSet<String>) PreferenceClass.getExams(mContext);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_store_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            holder.tvTitle.setText(testList.get(position).getString("displayName"));
            holder.tvDescriptn.setText(testList.get(position).getString("longDescription"));

            if (testList.get(position).getBoolean("isFree")){
                holder.tvAmount.setText("FREE");
            }else {
                holder.tvAmount.setText("Buy $"+String.format("%.2f", testList.get(position).getDouble("price")));
            }

            holder.tvAmount.setTag(testList.get(position));

            holder.tvAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (testList.get(position).getBoolean("isFree")){
                            hashSet.add(testList.get(position).getString("id"));
                            PreferenceClass.setExams(mContext,hashSet);
                            Toast.makeText(mContext, "Congratulations this item is now available", Toast.LENGTH_SHORT).show();
                        } else {
                            for (SkuDetails details: skuDetails){
                                try {
                                    if (details.getSku().equals(testList.get(position).getString("id"))){
                                        BillingFlowParams billingFlowParams =  BillingFlowParams.newBuilder().setSkuDetails(details).build();
                                        client.launchBillingFlow((Activity) mContext, billingFlowParams);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


//                    listener.onItemClick(holder.itemView, position, testList.get(position).toString());


                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public int getItemCount() {
        return testList
                .size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDescriptn;
        private Button tvAmount;
        IProductListner iProductListner;

        public void setiProductListner(IProductListner iProductListner) {
            this.iProductListner = iProductListner;
        }

        public MyViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_testName);
            tvDescriptn = view.findViewById(R.id.tv_testDescrptn);
            tvAmount = view.findViewById(R.id.tv_testAmount);

            tvAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    iProductListner.onProductClickListner(view, getAdapterPosition());
                }
            });
        }
    }
}