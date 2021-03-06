package com.crcexam.UI.fragments;


import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crcexam.R;
import com.crcexam.adapters.MissedQuestionAdapter;
import com.crcexam.adapters.RetestSampleQuizAdapter;
import com.crcexam.constants.Constant;
import com.crcexam.database.DatabaseHandler;
import com.crcexam.interfaces.RecyclerViewClickWithJson;
import com.crcexam.utils.PreferenceClass;
import com.crcexam.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class MultiOptionQuestionListFragment extends Fragment implements View.OnClickListener, RecyclerViewClickWithJson {


    private static final String TAG = "MultiOptionQuestionList";
    public static int totalCurrectAns = 0;
    RecyclerView recyclerView;
    ArrayList<JSONObject> lstMissedQst = new ArrayList<>();
    ArrayList<JSONObject> lstQuestions = new ArrayList<>();
    RetestSampleQuizAdapter multiOptionAdapter;
    MissedQuestionAdapter missedQuestionAdapter;
    RecyclerViewClickWithJson mRvListener;
    // public static ArrayList<HashMap<String, String>> arrayList = new ArrayList<>();
    int position = 0;
    JSONArray arrayOption;
    JSONObject currentObj = null;
    ArrayList<JSONObject> lstAnswers = new ArrayList<>();
    ArrayList<JSONObject> lstMissedQuestion = new ArrayList<>();
    DatabaseHandler db;
    private Context mContext;
    private View rootView;
    private boolean is_SelectedAns = false, is_Missed = false;
    private int questionPostion = 0, missed_qst_pos = 0;
    private boolean is_first = true;
    private JSONObject jsonObject ;
    private boolean isMarked = false;
    private String str_your_wrong_ans = "";
    String title_name;

    public MultiOptionQuestionListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment\
        mContext = getContext();
        rootView = inflater.inflate(R.layout.fragment_multi_option_question_list, container, false);
        mRvListener = this;
        db = new DatabaseHandler(mContext);
        init();
        return rootView;
    }

    private void init() {
        listner();
        Bundle bundle = MultiOptionQuestionListFragment.this.getArguments();
        if (bundle != null) {
            Log.e(TAG, "init: bundle.toString()  " + bundle.toString());
            if (bundle.getString("data") != null) {
                getList();
            } else {
                Toast.makeText(mContext, "No data found", Toast.LENGTH_SHORT).show();
            }
            title_name=bundle.getString("displayName");
        }
        /*if (getActivity().getIntent().getStringExtra("data") != null) {
            getList();
        } else {
            Toast.makeText(mContext, "No data found", Toast.LENGTH_SHORT).show();
        }*/
    }

    private void listner() {
        ((ImageView) rootView.findViewById(R.id.imv_previous)).setOnClickListener(this);
        ((ImageView) rootView.findViewById(R.id.imv_next)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.imgMarkedQuestion)).setOnClickListener(this);

    }

    private void adapterAllQuestion(JSONObject jsonObject) {
        try {
            is_SelectedAns = false;
            ((TextView) rootView.findViewById(R.id.tv_answerDescription)).setText("");
            questionPostion = questionPostion + 1;
            lstAnswers.clear();
            currentObj = jsonObject;
            recyclerView = rootView.findViewById(R.id.recyclerview_option);
            multiOptionAdapter = new RetestSampleQuizAdapter(mContext, lstAnswers, this);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(mLayoutManager);
            // JSONObject object=new JSONObject(jsonObject.toString().replaceAll("\\\\\\\\",""));
            Log.e("jsonObject qq ", jsonObject + "");
            Log.e("jsonObject qq2 ", jsonObject.getJSONObject("questions").getString("Question") + "");
            JSONArray jsonArray = jsonObject.getJSONObject("questions").getJSONArray("Answers");
            Log.e("jsonArray ans ", jsonArray + "");
            shuffleJsonArray(jsonArray);
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

    //This method shuffle the the answers.
    public static JSONArray shuffleJsonArray (JSONArray array) throws JSONException {
        // Implementing Fisher–Yates shuffle
        Random rnd = new Random();
        for (int i = array.length() - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            // Simple swap
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
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

            Log.e(TAG, " Spann adapterMissedQuestion: " + PreferenceClass.getStringPreferences(mContext,currentObj.getString("your_ans")));
            sYourAns.setSpan(new StyleSpan(Typeface.BOLD), 0, 12, 0); // set size
            sYourAns.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 12, 0);// set color
            ((TextView) rootView.findViewById(R.id.txtYourAns)).setText(sYourAns);
            ((TextView) rootView.findViewById(R.id.tv_question)).setText(currentObj.getJSONObject("questions").getString("Question"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("multioption.json");
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

    private void getList() {
        try {
            Bundle bundle = MultiOptionQuestionListFragment.this.getArguments();
            if (bundle != null) {
                totalCurrectAns = 0;
                //if (getActivity().getIntent().hasExtra("is_misssed"))
                if (bundle.containsKey("is_misssed")){
                    Log.e("hasExtra  ", " ismissedddd");
                    is_Missed = true;
                }
                if (!is_Missed) {
                    if (is_first) {
                        is_first = false;
                        db.clearAllQuestion();
                    }
                    if (db.getAllQuestions().size() == 0) {
                        Log.e(TAG, "getList: " +  bundle.getString("data") );
                        JSONArray jsonArray = new JSONArray(bundle.getString("data"));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            for (int j = 0; j < jsonArray.getJSONObject(i).getJSONArray("Answers").length(); j++) {
                                if (jsonArray.getJSONObject(i).getJSONArray("Answers").getJSONObject(j).getBoolean("IsCorrect")) {
                                    db.addQuestions("false", "Sample_Quiz", jsonArray.getJSONObject(i) + "", "", jsonArray.getJSONObject(i).getJSONArray("Answers").getJSONObject(j).getString("Answer"),
                                            jsonArray.getJSONObject(i).getString("Explanation"), "false");
                                }
                            }
                        }
                    }
                    Log.e("dfdf all question ", db.getAllQuestions().size() + "");
                    arrayOption = new JSONArray(db.getAllQuestions());
                    Log.e("dffdgf f", arrayOption.length() + "");
                    adapterAllQuestion(arrayOption.getJSONObject(0));
                    ((TextView) rootView.findViewById(R.id.tv_question)).setText(arrayOption.getJSONObject(0).getJSONObject("questions").getString("Question"));
                    ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setText("Question 1 of" + " " + arrayOption.length());
                } else {
                    ((LinearLayout) rootView.findViewById(R.id.rl_missed_question)).setVisibility(View.VISIBLE);
                    rootView.findViewById(R.id.recyclerview_option).setVisibility(View.GONE);
                    if (db.getAllMissedQuestionsList().size() > 0) {
                        lstMissedQuestion.addAll(db.getAllMissedQuestionsList());
                        adapterMissedQuestion(0);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void RunAnimation() {
        Animation a = AnimationUtils.loadAnimation(mContext, R.anim.xml);
        a.reset();
        TextView tv = (TextView) rootView.findViewById(R.id.tv_question);
        tv.clearAnimation();
        tv.startAnimation(a);
    }

    private void setNextQuestnFromList() {
        try {
            {
                position++;
                // RunAnimation();
                adapterAllQuestion(arrayOption.getJSONObject(position));
                ((TextView) rootView.findViewById(R.id.tv_question)).setText(arrayOption.getJSONObject(position).getJSONObject("questions").getString("Question"));
                ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setText("Question" + " " + (position + 1) + " of " + arrayOption.length());
                ((ImageView) rootView.findViewById(R.id.imv_previous)).setVisibility(View.VISIBLE);
                ((RelativeLayout) rootView.findViewById(R.id.retest_relativelayout)).setVisibility(View.GONE);
            }
            if (position == arrayOption.length() - 1) {
                ((ImageView) rootView.findViewById(R.id.imv_next)).setVisibility(View.VISIBLE);
                ((ImageView) rootView.findViewById(R.id.imv_previous)).setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setPreviousQuestnFromList() {
        try {
            if (position <= arrayOption.length() - 1 && position >= 0) {
                position--;
                ((ImageView) rootView.findViewById(R.id.imv_previous)).setVisibility(View.VISIBLE);
                ((ImageView) rootView.findViewById(R.id.imv_next)).setVisibility(View.VISIBLE);
                //RunAnimation();
                adapterAllQuestion(arrayOption.getJSONObject(position));
                ((TextView) rootView.findViewById(R.id.tv_question)).setText(arrayOption.getJSONObject(position).getString("question"));
                ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setText("Question" + " " + (position + 1) + " of " + arrayOption.length());
            }
            if (position == arrayOption.length() - 1) {
                ((ImageView) rootView.findViewById(R.id.imv_next)).setVisibility(View.VISIBLE);
                ((ImageView) rootView.findViewById(R.id.imv_previous)).setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imv_previous:
                //finish();
                Utility.clearBackStack(mContext);
                Objects.requireNonNull(getActivity()).onBackPressed();
                //loadFragment(new HomeFragment());
                // setPreviousQuestnFromList();
                break;
            case R.id.imv_next:
                if (!is_Missed) {
                    try {
                        if (getActivity().getIntent().hasExtra("is_misssed")) {
                            is_Missed = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        if (is_SelectedAns) {
                            if (position == arrayOption.length() - 1) {
                                JSONObject object = new JSONObject();
                                object.put("totla_currect", totalCurrectAns);
                                object.put("totla_question", arrayOption.length());
                                object.put("test_name",title_name);
                                Log.e("go for result ", object + "");
                                ResultFragment resultFragment = new ResultFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString("data", object + "");
                                resultFragment.setArguments(bundle);
                                loadFragment(resultFragment);
                                // startActivity(new Intent(mContext, ResultFragment.class).putExtra("data", object + ""));
                                //finish();
                                //loadFragment(new HomeFragment());
                            }
                            else {
                                setNextQuestnFromList();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {

                    if (missed_qst_pos == lstMissedQuestion.size() - 1) {
                        //finish();
                        Utility.clearBackStack(mContext);
                        ResultFragment resultFragment = new ResultFragment();
                        Bundle bundle = new Bundle();
                        Bundle bundle1= getArguments();
                        JSONObject object = null;
                        try {
                            object = new JSONObject(bundle1.getString("data2"));
                            bundle.putString("data", object + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        resultFragment.setArguments(bundle);
                        loadFragment(resultFragment);
                    } else {
                        missed_qst_pos++;
                        adapterMissedQuestion(missed_qst_pos);

                    }
                }
                break;
            case R.id.imgMarkedQuestion:
                if (isMarked){
                    isMarked =false;
                    ((TextView) rootView.findViewById(R.id.imgMarkedQuestion)).setBackground(getResources().getDrawable(R.drawable.ic_retest));
                }else {
                    //Toast.makeText(mContext, "Text View Clicked", Toast.LENGTH_SHORT).show();
                    ((TextView) rootView.findViewById(R.id.imgMarkedQuestion)).setBackground(getResources().getDrawable(R.drawable.ic_retest_blck));
                    setRetestExamQuestions(jsonObject);
                    isMarked = true;
                }

                break;
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }




    @Override
    public void onItemClick(View view, int position, String response, JSONObject jsonObjectRecycler) {
        Log.e(TAG, "jsonObjectRecycler onItemClick: "+jsonObjectRecycler );
        try {
            DatabaseHandler db = new DatabaseHandler(mContext);
            is_SelectedAns = true;
            if (!lstAnswers.toString().contains("is_refresh")) {
                ((TextView) rootView.findViewById(R.id.tv_answerDescription)).setText(arrayOption.getJSONObject(questionPostion - 1).getJSONObject("questions").getString("Explanation"));
                Log.e("optio sel  " + (questionPostion - 1), "    " + arrayOption + "");
                JSONArray object = new JSONArray(response);
                Log.e("object array ", object + "");
                if (object.getJSONObject(position).getBoolean("IsCorrect")) {
                    // totalCurrectAns++;
                    Log.e(TAG, "onItemClick:object " + object );
                    totalCurrectAns = totalCurrectAns + 1;
                    ((TextView) view.findViewById(R.id.imgNumber)).setText("");
                    ((TextView) view.findViewById(R.id.imgNumber)).setBackground(mContext.getResources().getDrawable(R.drawable.ic_checked_mark));
                    try {
                        db.updateQuestionData((arrayOption.getJSONObject(questionPostion - 1).getString("id")), "true", object.getJSONObject(position).getString("Answer"), "false");
                        Log.e(TAG, "onItemClick: db " + db );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < object.length(); i++) {
                        if (object.getJSONObject(i).getBoolean("IsCorrect")) {
                            JSONObject object1 = object.getJSONObject(i);
                            Log.e(TAG, "onItemClick:object1 " + object1 );
                            object1.put("is_refresh", true);
                            lstAnswers.set(i, object1);
                            // multiOptionAdapter.notifyItemChanged(i);
                            break;

                        }
                    }
                    //see comented code below
                } else {
                    Log.e(TAG, "cc onItemClick: "+jsonObjectRecycler );
                    str_your_wrong_ans = jsonObjectRecycler.getString("Answer");
                    db.updateQuestionData((arrayOption.getJSONObject(questionPostion - 1).getString("id")), "true", object.getJSONObject(position).getString("Answer"), "true");
                    Log.e(TAG, "---------------------------------------------------------------------------------" );
                    jsonObject = new JSONObject();
                    jsonObject = arrayOption.getJSONObject(questionPostion - 1);
                    Log.e(TAG, "wrongAnsCheck - onItemClick: arrayOption - "+arrayOption.getJSONObject(questionPostion - 1));

                    Log.e(TAG, "---------------------------------------------------------------------------------" );

                    ((TextView) view.findViewById(R.id.imgNumber)).setText("");
                    ((TextView) view.findViewById(R.id.imgNumber)).setBackground(mContext.getResources().getDrawable(R.drawable.ic_cross_mark));


                    for (int i = 0; i < object.length(); i++) {
                        if (object.getJSONObject(i).getBoolean("IsCorrect")) {
                            JSONObject object1 = object.getJSONObject(i);
                            // ((TextView) findViewById(R.id.tv_answerDescription)).setText(object.getJSONObject(position).getString("Explanation"));
                            object1.put("is_refresh", true);
                            lstAnswers.set(i, object1);
                            multiOptionAdapter.notifyItemChanged(i);
                            break;
                        }
                    }
                    ((RelativeLayout)rootView.findViewById(R.id.retest_relativelayout)).setVisibility(View.VISIBLE);
                    ((TextView) rootView.findViewById(R.id.imgMarkedQuestion)).setBackground(getResources().getDrawable(R.drawable.ic_retest));
                }
                // }
            } else {
                Log.e("else of is ref ad ", "dfdfgdfdfgdf df");
            }
            Log.e("sdfdsf ", db.getAllQuestions().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRetestExamQuestions(JSONObject jsonObject) {
        try {
            jsonObject.put("your_wrong_ans",str_your_wrong_ans);
            Log.e(TAG, "wrongAnsCheck : jsonObject " + jsonObject);
            Log.e(TAG, "wrongAnsCheck : CART_JSON_ARRAY_LIST " + PreferenceClass.getStringPreferences(mContext, Constant.WRONGQUESTION));

            String questionSte = PreferenceClass.getStringPreferences(mContext,Constant.WRONGQUESTION);
            if (questionSte.isEmpty()) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.put(jsonObject);
                PreferenceClass.setStringPreference(mContext, Constant.WRONGQUESTION, jsonArray.toString());
                Log.e(TAG, " wrongAnsCheck CART_JSON_ARRAY_LIST SECOND - " + PreferenceClass.getStringPreferences(mContext, Constant.WRONGQUESTION));
            } else {
                JSONArray jsonArray = new JSONArray(questionSte);
                jsonArray.put(jsonObject);
                PreferenceClass.setStringPreference(mContext, Constant.WRONGQUESTION, jsonArray.toString());
                Log.e(TAG, "setRetestExamQuestions wrongAnsCheck CART_JSON_ARRAY_LIST THIRD - " + PreferenceClass.getStringPreferences(mContext, Constant.WRONGQUESTION));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    /* try {
                        lstMissedQuestion.clear();
                        JSONArray array = null;
                        if (!db.getAllMissedQuestions().toString().contains(currentObj.getString("Id")))
                            db.addMissedQuestions(currentObj.toString());


                        if (PreferenceClass.getStringPreferences(mContext, Constant.MISSED_QUESTIONS).length() > 20) {
                            array = new JSONArray(PreferenceClass.getStringPreferences(mContext, Constant.MISSED_QUESTIONS));
                            for (int i = 0; i < array.length(); i++) {
                                lstMissedQuestion.add(array.getJSONObject(i));
                            }
                            System.out.println(("adddwddd prev " + lstMissedQuestion.size() + "  " + lstMissedQuestion.toString()));
                            Log.e("arrayOption ifff ", arrayOption.length() + "  " + arrayOption);
                            lstMissedQuestion.add(arrayOption.getJSONObject(questionPostion - 1));
                            System.out.println("adddwddd new  " + lstMissedQuestion.size() + "  " + lstMissedQuestion.toString());

                            PreferenceClass.setStringPreference(mContext, Constant.MISSED_QUESTIONS, lstMissedQuestion.toString());
                        } else {
                            Log.e("arrayOption else ", arrayOption.length() + "  " + arrayOption);
                            lstMissedQuestion.add(arrayOption.getJSONObject(questionPostion - 1));
                            System.out.println("first new  " + lstMissedQuestion.size() + "  " + lstMissedQuestion.toString());

                            PreferenceClass.setStringPreference(mContext, Constant.MISSED_QUESTIONS, lstMissedQuestion.toString());

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
}
