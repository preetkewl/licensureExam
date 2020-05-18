package com.crcexam.interfaces;

import android.view.View;

import org.json.JSONObject;

public interface RecyclerViewClickWithJson {
    public void onItemClick(View view, int position, String response, JSONObject jsonObject);
}
