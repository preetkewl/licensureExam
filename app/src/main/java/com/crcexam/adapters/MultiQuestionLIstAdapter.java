package com.crcexam.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.crcexam.R;
import com.crcexam.database.DatabaseHandler;
import com.crcexam.interfaces.RecyclerviewClickListner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MultiQuestionLIstAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "MultiOptionQuestionList";
    public static int totalCurrectAns = 0;
    RecyclerView recyclerView;
    ArrayList<JSONObject> lstMissedQst = new ArrayList<>();
    ArrayList<JSONObject> lstQuestions = new ArrayList<>();
    AnswerListAdapter multiOptionAdapter;
    MissedQuestionAdapter missedQuestionAdapter;
    RecyclerviewClickListner mRvListener;
    // public static ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    int position = 0;
    JSONArray arrayOption;
    JSONObject currentObj = null;
    ArrayList<JSONObject> lstAnswers = new ArrayList<>();
    ArrayList<JSONObject> lstMissedQuestion = new ArrayList<>();
    DatabaseHandler db;
    private ArrayList<Integer> page_indexes;
    private Context mContext;
    private View rootView;
    private boolean is_SelectedAns = false, is_Missed = false;
    private int questionPostion = 0, missed_qst_pos = 0;
    private boolean is_first = true;


    public MultiQuestionLIstAdapter(FragmentManager fm) {
        super(fm);
    }


    @Override
    public Fragment getItem(int i) {
        return null;
    }

    @Override
    public int getCount() {
        return 0;
    }

    private void setNextQuestnFromList() {
        try {
            {
                position++;
                // RunAnimation();
                adapterAllQuestion(arrayOption.getJSONObject(position));
                ((TextView) rootView.findViewById(R.id.tv_question)).setText(arrayOption.getJSONObject(position).getJSONObject("questions").getString("Question"));
                ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setText("Question" + " " + (position + 1) + " of " + arrayOption.length());
                rootView.findViewById(R.id.imv_previous).setVisibility(View.VISIBLE);
            }
            if (position == arrayOption.length() - 1) {
                rootView.findViewById(R.id.imv_next).setVisibility(View.VISIBLE);
                rootView.findViewById(R.id.imv_previous).setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void adapterAllQuestion(JSONObject jsonObject) {
        try {
            is_SelectedAns = false;
            ((TextView) rootView.findViewById(R.id.tv_answerDescription)).setText("");
            questionPostion = questionPostion + 1;
            lstAnswers.clear();
            currentObj = jsonObject;
            recyclerView = rootView.findViewById(R.id.recyclerview_option);
            multiOptionAdapter = new AnswerListAdapter(mContext, lstAnswers, mRvListener);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(mLayoutManager);
            // JSONObject object=new JSONObject(jsonObject.toString().replaceAll("\\\\\\\\",""));
            Log.e("jsonObject qq ", jsonObject + "");
            Log.e("jsonObject qq2 ", jsonObject.getJSONObject("questions").getString("Question") + "");
            JSONArray jsonArray = jsonObject.getJSONObject("questions").getJSONArray("Answers");
            Log.e("jsonArray ans ", jsonArray + "");
            //JSONArray jsonArray = jsonObject.getJSONArray("Answers");
            for (int i = 0; i < jsonArray.length(); i++) {
                lstAnswers.add(jsonArray.getJSONObject(i));
            }
            multiOptionAdapter.addList(lstAnswers);
            recyclerView.setAdapter(multiOptionAdapter);
            recyclerView.setNestedScrollingEnabled(false);
            multiOptionAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void adapterMissedQuestion(int pos) {
        try {
            Log.e("adapterMissedQuestion ", pos + "  lstMissedQuestion.get(pos) " + lstMissedQuestion.get(pos));
            is_SelectedAns = false;
            // questionPostion = questionPostion + 1;
            currentObj = lstMissedQuestion.get(pos);
            ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setText((pos + 1) + " of " + lstMissedQuestion.size());
            SpannableString ss1 = new SpannableString(mContext.getResources().getString(R.string.Correct_Answer1) + " " + currentObj.getString("currect_ans"));
            ss1.setSpan(new StyleSpan(Typeface.BOLD), 0, 15, 0); // set size
            ss1.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 15, 0);// set color
            ((TextView) rootView.findViewById(R.id.txtExplaintion)).setText(currentObj.getString("explaination"));
            ((TextView) rootView.findViewById(R.id.txtCorrectAns)).setText(ss1);
            SpannableString sYourAns = new SpannableString(mContext.getResources().getString(R.string.your_Answer1) + " " + currentObj.getString("your_ans"));
            sYourAns.setSpan(new StyleSpan(Typeface.BOLD), 0, 12, 0); // set size
            sYourAns.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 12, 0);// set color
            ((TextView) rootView.findViewById(R.id.txtYourAns)).setText(sYourAns);
            ((TextView) rootView.findViewById(R.id.tv_question)).setText(currentObj.getJSONObject("questions").getString("Question"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // This is called when notifyDataSetChanged() is called
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        return PagerAdapter.POSITION_NONE;
    }
}
