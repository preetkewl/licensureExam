package com.crcexam.android.interfaces;

import org.json.JSONObject;

public interface OnItemCheckListener {
    void onItemCheck(JSONObject item);

    void onItemUncheck(JSONObject item);
}
