package com.crcexam.UI.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crcexam.R;
import com.crcexam.utils.ProgressHUD;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlipQuestionListResultScreenFragment extends Fragment implements View.OnClickListener  {

    private static final String TAG = "FlipQuestionListResultS";
    private Context mContext;
    private View rootView;
    ProgressHUD progressHUD;


    public FlipQuestionListResultScreenFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getContext();
        rootView = inflater.inflate(R.layout.fragment_flip_question_list_result_screen, container, false);
        setListners();
        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtYes:
                loadFragment(new SetSelectionFragment());
                break;
            case R.id.txtNo:
                loadFragment(new HomeFragment());
                break;
        }
    }




    private void setListners() {
        ((TextView) rootView.findViewById(R.id.txtYes)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.txtNo)).setOnClickListener(this);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }


}
