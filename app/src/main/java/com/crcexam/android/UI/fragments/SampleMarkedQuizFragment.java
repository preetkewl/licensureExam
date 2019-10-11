package com.crcexam.android.UI.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.database.DatabaseHandler;
import com.crcexam.android.utils.PreferenceClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class SampleMarkedQuizFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "SampleMarkedQuizFragmen";
    private Context mContext;
    private View rootView;
    JSONArray arrayOption;
    DatabaseHandler db;
    int position = 0;
    private JSONArray jsonArray;

    public SampleMarkedQuizFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mContext = getContext();
        db = new DatabaseHandler(mContext);
        rootView = inflater.inflate(R.layout.fragment_sample_marked_quiz, container, false);
        showMarkedQuiz();
        setListners();

        return rootView;

    }


    private void showMarkedQuiz() {
        String str = PreferenceClass.getStringPreferences(mContext, Constant.WRONGQUESTION);
        Log.e(TAG, "showMarkedQuiz: str "+str );
        if (!str.isEmpty()) {
            try {
                jsonArray = new JSONArray(str);
                showQuestion(0);
                position = 0;
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else {
            ((TextView) rootView.findViewById(R.id.missed_quetions)).setVisibility(View.GONE);
            ((TextView) rootView.findViewById(R.id.textView_youranswer)).setVisibility(View.GONE);
            ((TextView) rootView.findViewById(R.id.textView_correctanswer)).setVisibility(View.GONE);
            ((TextView) rootView.findViewById(R.id.textView_decription)).setVisibility(View.GONE);
            ((ImageView) rootView.findViewById(R.id.imv_sampleprevious)).setVisibility(View.GONE);
            ((ImageView) rootView.findViewById(R.id.imv_samplenext)).setVisibility(View.GONE);
        }
    }

    private void showQuestion(int i) {
        Log.e(TAG, "showMarkedQuiz  i =  "+i );
        try {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            ((TextView) rootView.findViewById(R.id.tv_samplemarkedquestnNumber)).setText("Question" + " " + (position + 1) + " of " + jsonArray.length());
            ((TextView) rootView.findViewById(R.id.missed_quetions)).setText("Question : " + jsonObject.getJSONObject("questions").getString("Question"));
            ((TextView) rootView.findViewById(R.id.textView_youranswer)).setText("Wrong Answer : " + jsonObject.getString("your_wrong_ans"));
            ((TextView) rootView.findViewById(R.id.textView_correctanswer)).setText("Correct Answer : " + jsonObject.getString("currect_ans"));
            ((TextView) rootView.findViewById(R.id.textView_decription)).setText("Description : " + jsonObject.getString("explaination"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

/*

    private void setNextQuestnFromList() {
        try {
            {
                position++;
                // RunAnimation();
                ((TextView) rootView.findViewById(R.id.missed_quetions)).setText(arrayOption.getJSONObject(position).getJSONObject("questions").getString("Question"));
                ((TextView) rootView.findViewById(R.id.tv_samplemarkedquestnNumber)).setText("Question" + " " + (position + 1) + " of " + arrayOption.length());
                ((ImageView) rootView.findViewById(R.id.imv_sampleprevious)).setVisibility(View.VISIBLE);

            }
            if (position == arrayOption.length() - 1) {
                ((ImageView) rootView.findViewById(R.id.imv_samplenext)).setVisibility(View.VISIBLE);
                ((ImageView) rootView.findViewById(R.id.imv_sampleprevious)).setVisibility(View.VISIBLE);
                loadFragment(new HomeFragment());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_samplenext:
                //setNextQuestnFromList();
                Log.e(TAG, "showMarkedQuiz onClick: position = "+position );
                Log.e(TAG, "showMarkedQuiz onClick: length "+jsonArray.length());

                position++;
                if (position+1 <= jsonArray.length()){
                    Log.e(TAG, "showMarkedQuiz insideIF position "+position );
                    showQuestion(position);

                }else {
                    Log.e(TAG, "showMarkedQuiz onClick: empty" );
                }
                break;
            case R.id.imv_sampleprevious:
                loadFragment(new HomeFragment());
                break;
        }

    }


    private void setListners() {
        ((ImageView) rootView.findViewById(R.id.imv_samplenext)).setOnClickListener(this);
        ((ImageView) rootView.findViewById(R.id.imv_sampleprevious)).setOnClickListener(this);

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment).commit();
    }
}
