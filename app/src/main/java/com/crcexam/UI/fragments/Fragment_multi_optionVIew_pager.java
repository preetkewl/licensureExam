package com.crcexam.UI.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crcexam.R;
import com.crcexam.adapters.MultiQuestionLIstAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_multi_optionVIew_pager extends Fragment {


    private ViewPager mPager;
    private MultiQuestionLIstAdapter mAdapter;
    private View rootView;
    private Context mContext;


    public Fragment_multi_optionVIew_pager() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_fragment_multi_option_view_pager, container, false);
        mAdapter = new MultiQuestionLIstAdapter(getActivity().getSupportFragmentManager());
        mContext = getContext();
        mPager = rootView.findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        return rootView;
    }

}
