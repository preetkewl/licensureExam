package com.crcexam.android.UI.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.ProgressHUD;
import com.crcexam.android.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectionFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    Context mContext;
    Toolbar mToolbar;
    ArrayList<String> lstSpinner = new ArrayList<>();
    ArrayList<JSONObject> lstQuestions = new ArrayList<>();
    ProgressHUD progressHUD;
    private int SelectedPos = 0;
    private View rootView;


    public SelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_selection, container, false);
        mContext = getContext();
        //setActionBar();
        setFontStyle();
        init();
        return rootView;
    }

    private void setActionBar() {
        try {
            mToolbar = rootView.findViewById(R.id.toolbar_dash);
            // setSupportActionBar(mToolbar);
//            ActionBar actionBar = getSupportActionBar();
//            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setHomeButtonEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFontStyle() {
        //((TextView) rootView.findViewById(R.id.tv_testName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Bold));
        //((TextView) rootView.findViewById(R.id.tv_title)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Bold));
        ((TextView) rootView.findViewById(R.id.tv_selection_one)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.tv_selection_two)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.tv_selection_three)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));

    }

    private void init() {
        try {
            Bundle bundle = SelectionFragment.this.getArguments();
            if (bundle != null) {
                String bundleStr = bundle.getString("data");
                JSONObject object = new JSONObject(bundleStr);
                getAllExamList(object.getInt("id"));
                listner();
                ((TextView) getActivity().findViewById(R.id.tv_title)).setText(object.getString("displayName"));
               // ((TextView) rootView.findViewById(R.id.tv_testName)).setText(object.getString("displayName"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void listner() {
        // ((ImageView) rootView.findViewById(R.id.imv_back)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.tv_selection_one)).setOnClickListener(this);
        ((TextView) rootView.findViewById(R.id.tv_selection_two)).setOnClickListener(this);

    }

    private void getAllExamList(int id) {
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
                home2hotel.getExpDetailById(id).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (progressHUD.isShowing() && progressHUD != null) {
                                progressHUD.dismiss();
                            }
                            Log.e("onResponse  ", response.code() + "");
                            if (response.code() == 200) {
                                JSONObject obj = new JSONObject(response.body().string());
                                setData(obj);
                            } else {
                                String error = response.errorBody().string();
                                Log.e("error  ", error + "");
                                // JSONObject object = new JSONObject(error);

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

    private void setData(JSONObject obj) {
        try {

            PreferenceClass.setStringPreference(mContext, Constant.SELECTED_QUESTION, obj.toString());

            JSONArray array = obj.getJSONArray("Questions");
            for (int i = 0; i < array.length(); i++) {
                lstQuestions.add(array.getJSONObject(i));
            }
            for (int i = 1; i <= array.length(); i++) {
                if (i % 5 == 0) {
                    lstSpinner.add(i + " Questions");
                }

            }
            if (array.length() % 5 != 0) {
                lstSpinner.add("All " + array.length() + " Questions");
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, R.layout.spiiner_text, lstSpinner);
            Spinner spinner = (Spinner) rootView.findViewById(R.id.spinnerQuestion);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setOnItemSelectedListener(this);
            spinner.setAdapter(adapter);
            ((TextView) rootView.findViewById(R.id.tv_selection_two)).setText("Start all " + array.length() + " Questions");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.imv_back:
                //finish();
                loadFragment(new HomeFragment());
                break;*/
            case R.id.tv_selection_one:
                MultiOptionQuestionListFragment multiOptionQuestionListFragment = new MultiOptionQuestionListFragment();
                Bundle bundle = new Bundle();
                if (SelectedPos == 0) {
                    if (lstQuestions.size() % 5 != 0) {
                        Log.e(" not 555 ", lstQuestions.subList(0, lstQuestions.size()).size() + "");
                        bundle.putString("data", lstQuestions.subList(0, lstQuestions.size()) + "");
                        multiOptionQuestionListFragment.setArguments(bundle);
                        loadFragment(multiOptionQuestionListFragment);
                        //startActivity(new Intent(mContext, MultiOptionQuestionListFragment.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()) + ""));

                    } else {
                        Log.e(" df", lstQuestions.subList(0, 5).size() + "");
                        bundle.putString("data", lstQuestions.subList(0, 5) + "");
                        multiOptionQuestionListFragment.setArguments(bundle);
                        loadFragment(multiOptionQuestionListFragment);
                        //startActivity(new Intent(mContext, MultiOptionQuestionListFragment.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                    }
                } else {

                    if (lstQuestions.size() % 5 != 0) {
                        Log.e(" lstQuestions size ", lstQuestions.size() + "");
                        Log.e(" not 555 >0 ", lstQuestions.subList(0, lstQuestions.size()).size() + "");
                        bundle.putString("data", lstQuestions.subList(0, lstQuestions.size()).size() + "");
                        multiOptionQuestionListFragment.setArguments(bundle);
                        loadFragment(multiOptionQuestionListFragment);
                        //startActivity(new Intent(mContext, MultiOptionQuestionListFragment.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()).size() + ""));

                    } else {
                        Log.e(" df >0 ", lstQuestions.subList(0, 5 * (SelectedPos + 1)) + "");
                        bundle.putString("data", lstQuestions.subList(0, 5) + "");
                        multiOptionQuestionListFragment.setArguments(bundle);
                        loadFragment(multiOptionQuestionListFragment);
                        //startActivity(new Intent(mContext, MultiOptionQuestionListFragment.class).putExtra("data", lstQuestions.subList(0, 5) + ""));
                        //startActivity(new Intent(SelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                    }
                }
                // already commented -
              /*  if (SelectedPos==0) {
                    if (lstQuestions.size()>=5){
                        startActivity(new Intent(SelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                    }else {
                        startActivity(new Intent(SelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()-1) + ""));

                    }
                }else {
                    startActivity(new Intent(SelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5*SelectedPos) + ""));

                }*/
                break;

            case R.id.tv_selection_two:
                MultiOptionQuestionListFragment multiOptionQuestionListFragment1 = new MultiOptionQuestionListFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("data", lstQuestions + "");
                multiOptionQuestionListFragment1.setArguments(bundle1);
                loadFragment(multiOptionQuestionListFragment1);
                //startActivity(new Intent(mContext, MultiOptionQuestionListFragment.class).putExtra("data", lstQuestions + ""));
                // already commented -
                /*if (SelectedPos == 0) {
                    if (lstQuestions.size() % 5 != 0&&lstQuestions.size()>5) {
                        Log.e(" not 555 ", lstQuestions.subList(0, lstQuestions.size()).size() + "");
                        startActivity(new Intent(SelectionActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()) + ""));

                    }else {
                        Log.e(" df", lstQuestions.subList(0, 5).size() + "");
                        startActivity(new Intent(SelectionActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                    }
                } else {

                    if (lstQuestions.size() % 5 != 0) {
                        Log.e(" lstQuestions size ", lstQuestions.size() + "");
                        Log.e(" not 555 >0 ", lstQuestions.subList(0, lstQuestions.size()).size() + "");
                        startActivity(new Intent(SelectionActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()).size()  + ""));

                    }else {
                        Log.e(" df >0 ", lstQuestions.subList(0, 5*(SelectedPos+1)) + "");
                        startActivity(new Intent(SelectionActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                    }
                }*/
               /* if (SelectedPos==0) {
                    if (lstQuestions.size()>=5){
                        startActivity(new Intent(SelectionActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                    }else {
                        startActivity(new Intent(SelectionActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()-1) + ""));

                    }
                }else {
                    if (lstQuestions.size()>=5){
                        startActivity(new Intent(SelectionActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5*(SelectedPos+1)) + ""));

                    }else {
                        startActivity(new Intent(SelectionActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()-1) + ""));

                    }
                }*/
                break;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment).addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinnerQuestion) {
            SelectedPos = position;
            Log.e("ddfd ", position + "");
            ((TextView) rootView.findViewById(R.id.tv_selection_one)).setText("Start " + lstSpinner.get(position) + " random questions");


//already commented -
/*
            if (position == 0) {
                if (lstQuestions.size() % 5 != 0) {
                    Log.e(" not 555 ", lstQuestions.subList(0, lstQuestions.size()-1).size() + "");
                }else {
                    Log.e(" df", lstQuestions.subList(0, 5).size() + "");
                }
            } else {

                if (lstQuestions.size() % 5 != 0) {
                    Log.e(" lstQuestions size ", lstQuestions.size() + "");
                    Log.e(" not 555 >0 ", lstQuestions.subList(0, lstQuestions.size()).size() + "");
                }else {

                    Log.e(" df >0 ", lstQuestions.subList(0, 5*(position+1)) + "");
                }
                Log.e("ddfgfddfgdfg df", lstQuestions.subList(0, 5 * position).size() + "");
            }*/

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
