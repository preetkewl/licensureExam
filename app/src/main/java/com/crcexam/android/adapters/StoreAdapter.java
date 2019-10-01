package com.crcexam.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.interfaces.RecyclerviewClickListner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<JSONObject> testList;
    private RecyclerviewClickListner listener;


    public StoreAdapter(Context mContext, ArrayList<JSONObject> testList, RecyclerviewClickListner listener) {
        this.mContext = mContext;
        this.testList = testList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_store_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            holder.tvTitle.setText(testList.get(position).getString("displayName"));
            holder.tvDescriptn.setText(testList.get(position).getString("longDescription"));
            holder.tvAmount.setText("Buy $0"+testList.get(position).getString("price"));
            holder.tvAmount.setTag(testList.get(position));
            holder.tvAmount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(holder.itemView,position,testList.get(position).toString());
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return testList
                .size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDescriptn,tvAmount;


        public MyViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tv_testName);
            tvDescriptn = view.findViewById(R.id.tv_testDescrptn);
            tvAmount = view.findViewById(R.id.tv_testAmount);
        }
    }

}