package com.crcexam.android.UI.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.adapters.ExamListAdapter;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.interfaces.RecyclerviewClickListner;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.ConnectionDetector;
import com.crcexam.android.utils.ProgressHUD;
import com.crcexam.android.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class FlipQuestionListFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "FlipQuestionListActivit";
    Context mContext;
    Toolbar mToolbar;
    ArrayList<JSONObject> questionArraylist = new ArrayList<>();
    int position = 0;
    RecyclerView recyclerView;
    ArrayList<JSONObject> homeArraylist = new ArrayList<>();
    ExamListAdapter homeAdapter;
    RecyclerviewClickListner recyclerviewClickListner;
    ProgressHUD progressHUD;
    ConnectionDetector cd;
    int isBack = 0;
    boolean isFirstForFinish = false;
    private View rootView;


    public FlipQuestionListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_flip_question_list, container, false);
        mContext = getContext();
        cd = new ConnectionDetector(mContext);
        setFontStyle();
        init();
        return rootView;
    }

    private void init() {
        listner();
        getList();

    }

    private void setFontStyle() {

        ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.tv_question)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
    }

    private void listner() {
        ((ImageView) rootView.findViewById(R.id.imv_previous)).setOnClickListener(this);
        ((ImageView) rootView.findViewById(R.id.imv_next)).setOnClickListener(this);
    }


    private void getAllExamList() {
        try {
            progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // TODO Auto-generated method stub
                }
            });
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.getAllExamList().enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (progressHUD.isShowing() && progressHUD != null) {
                                progressHUD.dismiss();
                            }
                            Log.e("onResponse  ", response.code() + "");
                            homeArraylist.clear();
                            ArrayList<JSONObject> lstFlipSet = new ArrayList<>();
                            if (response.code() == 200) {
                                JSONArray array = new JSONArray(response.body().string());
                                for (int i = 0; i < array.length(); i++) {
                                    homeArraylist.add(array.getJSONObject(i));
                                    if (array.getJSONObject(i).getBoolean("isFree") && array.getJSONObject(i).getString("contentType").equalsIgnoreCase("FlipSet")) {
                                        lstFlipSet.add(array.getJSONObject(i));
                                    }
                                }
                                Log.e("lstFlipSet  ", lstFlipSet + "");
                                homeAdapter = new ExamListAdapter(mContext, homeArraylist, recyclerviewClickListner);
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setAdapter(homeAdapter);
                                recyclerView.setNestedScrollingEnabled(false);
                            } else {
                                String error = response.errorBody().string();
                                Log.e("error  ", error + "");
                                JSONObject object = new JSONObject(error);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.getLocalizedMessage();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getList() {
        JSONObject obj = null;
        try {
            // JSONArray jsonArray = obj.getJSONArray("questions");
            JSONArray jsonArray = new JSONArray(getActivity().getIntent().getStringExtra("data"));
            Log.e("question length  ", jsonArray.length() + "");
            for (int i = 0; i < jsonArray.length(); i++) {
                questionArraylist.add(jsonArray.getJSONObject(i));
            }
            ((TextView) rootView.findViewById(R.id.tv_question)).setText(questionArraylist.get(0).getString("Front"));
            ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setText("Card 1 of" + " " + questionArraylist.size());

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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void setNextQuestnFromList() {
        try {
            if (position <= questionArraylist.size() - 1) {
                ((TextView) rootView.findViewById(R.id.tv_question)).setVisibility(View.VISIBLE);
                position++;
                questionArraylist.get(position);
                //RunAnimation();
                ((TextView) rootView.findViewById(R.id.tv_question)).setText(questionArraylist.get(position).getString("Front"));
                ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setText("Card" + " " + (position + 1) + " of " + questionArraylist.size());
                ((ImageView) rootView.findViewById(R.id.imv_previous)).setVisibility(View.VISIBLE);
            } else {
                Log.d("TAG", "Reached Last Record");

            }
            if (position == questionArraylist.size() - 1) {
                ((ImageView) rootView.findViewById(R.id.imv_next)).setVisibility(View.VISIBLE);
                ((ImageView) rootView.findViewById(R.id.imv_previous)).setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showBackAnswer() {
        try {
            if (position <= questionArraylist.size()) {
                ((TextView) rootView.findViewById(R.id.tvAnsBack)).setVisibility(View.VISIBLE);
                ((TextView) rootView.findViewById(R.id.tv_question)).setVisibility(View.GONE);
                ((TextView) rootView.findViewById(R.id.tvAnsBack)).setText(Html.fromHtml(questionArraylist.get(position).getString("Back")));
                ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setText("Answer" + " " + (position + 1) + " of " + questionArraylist.size());
                ((ImageView) rootView.findViewById(R.id.imv_previous)).setVisibility(View.VISIBLE);
            } else {
                Log.d("TAG", "Reached Last Record");
            }
            if (position == questionArraylist.size() - 1) {
                ((ImageView) rootView.findViewById(R.id.imv_next)).setVisibility(View.VISIBLE);
                ((ImageView) rootView.findViewById(R.id.imv_previous)).setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setPreviousQuestnFromList() {
        try {
            Log.e("position previuos", position + "");
            Log.e("questionArray previous", questionArraylist.size() - 1 + "");

            if (position <= questionArraylist.size() - 1) {
                position--;
                questionArraylist.get(position);
                //RunAnimation();
                Log.e("position previuos 1", position + "");
                ((TextView) rootView.findViewById(R.id.tvAnsBack)).setVisibility(View.VISIBLE);
                ((TextView) rootView.findViewById(R.id.tvAnsBack)).setText(questionArraylist.get(position).getString("Back"));
                ((TextView) rootView.findViewById(R.id.tv_questnNumber)).setText("Card" + " " + (position + 1) + " of " + questionArraylist.size());
                ((ImageView) rootView.findViewById(R.id.imv_next)).setVisibility(View.VISIBLE);
            } else {
                Log.d("TAG", "Reached Last Record");

            }
            if (position == 0) {
                ((ImageView) rootView.findViewById(R.id.imv_previous)).setVisibility(View.GONE);
                ((ImageView) rootView.findViewById(R.id.imv_next)).setVisibility(View.VISIBLE);
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
                loadFragment(new HomeFragment());
                //setPreviousQuestnFromList();
                break;
            case R.id.imv_next:
                ((TextView) rootView.findViewById(R.id.tvAnsBack)).setVisibility(View.GONE);
                Log.e(TAG, "onClick: " + position);
                if (position == questionArraylist.size() - 1) {
                    // startActivity(new Intent(this,ResultActivity.class));
                    if (!isFirstForFinish) {
                        showBackAnswer();
                        isFirstForFinish = true;
                    } else {
                       // getActivity().finish();
                        loadFragment(new HomeFragment());

                    }
                } else {
                    isBack++;
                    if (isBack % 2 == 0) {
                        setNextQuestnFromList();
                    } else {
                        showBackAnswer();
                    }
                }
                break;
        }
    }
}