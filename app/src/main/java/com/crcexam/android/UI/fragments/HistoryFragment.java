package com.crcexam.android.UI.fragments;

import android.content.Context;
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
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.adapters.HistoryAdapter;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    View rootView;
    Context mContext;
    RecyclerView recyclerView;
    ArrayList<JSONObject> historyArraylist = new ArrayList<>();
    HistoryAdapter historyAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_history, container, false);
        mContext = getActivity();
        adapter();
        return rootView;
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
           /* JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = obj.getJSONArray("history");
            for (int i = 0; i < jsonArray.length(); i++) {
                historyArraylist.add(jsonArray.getJSONObject(i));
            }*/
            historyAdapter = new HistoryAdapter(getActivity(), historyArraylist);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(historyAdapter);
            recyclerView.setNestedScrollingEnabled(false);
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
}
