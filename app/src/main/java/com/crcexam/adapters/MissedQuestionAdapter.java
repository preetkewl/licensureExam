package com.crcexam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crcexam.R;
import com.crcexam.interfaces.RecyclerviewClickListner;

import org.json.JSONObject;

import java.util.ArrayList;

public class MissedQuestionAdapter extends RecyclerView.Adapter<MissedQuestionAdapter.MyViewHolder> {
    private Context mContext;
   private ArrayList<JSONObject> missedQustList;
    public MissedQuestionAdapter(Context mContext, ArrayList<JSONObject> missedQustList, RecyclerviewClickListner mRvListener) {
        this.mContext = mContext;
        this.missedQustList = missedQustList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_radio_button, parent, false);

        return new MyViewHolder(itemView);
    }


    public void addList(ArrayList<JSONObject> lstNew)
    {
        this.missedQustList=lstNew;
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            holder.txtCorrectAns.setText(missedQustList.get(position).getString("currect_ans"));
            holder.txtYourAns.setText(missedQustList.get(position).getString("your_ans"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return missedQustList
                .size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtYourAns,txtCorrectAns;
        private View itemViewSelected;

        public MyViewHolder(View view) {
            super(view);
            itemViewSelected = view;
           // radioButton = view.findViewById(R.id.radio_button);
          /*  txtCorrectAns = view.findViewById(R.id.txtCorrectAns);
            txtYourAns = view.findViewById(R.id.txtYourAns);*/

        }
    }

}