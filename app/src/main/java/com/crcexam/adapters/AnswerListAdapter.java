package com.crcexam.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.crcexam.R;
import com.crcexam.constants.Constant;
import com.crcexam.interfaces.RecyclerviewClickListner;
import com.crcexam.utils.Utility;

import org.json.JSONObject;

import java.util.ArrayList;

public class AnswerListAdapter extends RecyclerView.Adapter<AnswerListAdapter.MyViewHolder> {
    RecyclerviewClickListner mRvListener;
    private Context mContext;
    private ArrayList<JSONObject> testList;
    // private ArrayList<HashMap<String, String>> testList;
    private RadioButton lastCheckedRB = null;

    public AnswerListAdapter(Context mContext, ArrayList<JSONObject> testList, RecyclerviewClickListner mRvListener) {
        this.mContext = mContext;
        this.testList = testList;
        this.mRvListener = mRvListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_radio_button, parent, false);

        return new MyViewHolder(itemView);
    }


    public void addList(ArrayList<JSONObject> lstNew) {
        this.testList = lstNew;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        try {
            char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            holder.txtAnswer.setText(testList.get(position).getString("Answer"));
            holder.txtAnswer.setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
            holder.imgNumber.setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
            holder.imgNumber.setText(alphabet[position] + "");
            if (!testList.get(position).has("is_refresh")) {
                holder.itemViewSelected.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mRvListener.onItemClick(holder.itemViewSelected, position, testList.toString());
                    }
                });
            } else {
                if (testList.get(position).getBoolean("IsCorrect")) {
                    holder.imgNumber.setText("");
                    holder.imgNumber.setBackground(mContext.getResources().getDrawable(R.drawable.ic_checked_mark));

                } else {
                    holder.imgNumber.setText("");
                    holder.imgNumber.setBackground(mContext.getResources().getDrawable(R.drawable.ic_cross_mark));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return testList
                .size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView txtAnswer, imgNumber;
        private View itemViewSelected;

        public MyViewHolder(View view) {
            super(view);
            itemViewSelected = view;
            imgNumber = view.findViewById(R.id.imgNumber);
            txtAnswer = view.findViewById(R.id.txtAnswer);

        }
    }

}