package com.crcexam.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.interfaces.RecyclerviewClickListner;
import com.crcexam.android.utils.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExamListAdapter extends RecyclerView.Adapter<ExamListAdapter.MyViewHolder> {
    RecyclerviewClickListner recyclerviewClickListner;
    private Context mContext;
    private ArrayList<JSONObject> testList;
    private String type;


    public ExamListAdapter(Context mContext, ArrayList<JSONObject> testList, RecyclerviewClickListner recyclerviewClickListner, String type) {
        this.mContext = mContext;
        this.testList = testList;
        this.type = type;
        this.recyclerviewClickListner = recyclerviewClickListner;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_home_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            holder.tvTitle.setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
            holder.tvTestName.setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
            holder.tvDescriptn.setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));

            if (type.equalsIgnoreCase("FQL")) {
                holder.imgIcon.setImageResource(R.drawable.group2);
            } else if (type.equalsIgnoreCase("SQ")) {
                holder.imgIcon.setImageResource(R.drawable.group1);
            }
            holder.tvTitle.setText(testList.get(position).getString("contentType"));
            holder.tvTestName.setText(testList.get(position).getString("displayName"));
            holder.tvDescriptn.setText(testList.get(position).getString("shortDescription"));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        recyclerviewClickListner.onItemClick(v, position, testList.get(position).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return testList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvTestName, tvDescriptn;
        public ImageView imgIcon;
        View itemView;

        public MyViewHolder(View view) {
            super(view);
            itemView = view;
            tvTitle = view.findViewById(R.id.tv_title);
            tvTestName = view.findViewById(R.id.tv_testName);
            tvDescriptn = view.findViewById(R.id.tv_testDescrptn);
            imgIcon = view.findViewById(R.id.imageView2);
        }
    }

}