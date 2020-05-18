package com.crcexam.UI.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crcexam.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class QuestionListFragment extends Fragment {

    private Context mContext;
    private View rootView;


    public QuestionListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getContext();
        rootView = inflater.inflate(R.layout.fragment_question_list, container, false);
        return rootView;
    }

}
