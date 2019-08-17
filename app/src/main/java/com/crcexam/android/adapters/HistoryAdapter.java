package com.crcexam.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    private Context mContext;
    private ArrayList<JSONObject> testList;


    public HistoryAdapter(Context mContext, ArrayList<JSONObject> testList) {
        this.mContext = mContext;
        this.testList = testList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_history, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        try {
            holder.tvDate.setText(Utility.getCurrentDate(testList.get(position).getLong("date")));
            holder.tvCorrectAns.setText(testList.get(position).getString("percentage")+"%");
            holder.tvNumbr.setText(testList.get(position).getString("currect_answer")+" out of "+testList.get(position).getString("total_question"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvDate, tvCorrectAns, tvNumbr;


        public MyViewHolder(View view) {
            super(view);
            tvDate = view.findViewById(R.id.tv_date);
            tvCorrectAns = view.findViewById(R.id.tv_correctanswer);
            tvNumbr = view.findViewById(R.id.tv_number);
        }
    }

}