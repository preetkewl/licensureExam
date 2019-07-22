package com.crcexam.android.UI.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.crcexam.android.R;
import com.crcexam.android.adapters.ExamListAdapter;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.interfaces.RecyclerviewClickListner;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.ConnectionDetector;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.ProgressHUD;
import com.crcexam.android.utils.Utility;

import org.json.JSONArray;
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
public class SetSelectionFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private static final String TAG = "FlipSetSelectionFragmen";
    Toolbar mToolbar;
    ArrayList<String> lstSpinner = new ArrayList<>();
    ArrayList<JSONObject> lstQuestions = new ArrayList<>();
    ProgressHUD progressHUD;
    RecyclerView recyclerView;
    ArrayList<JSONObject> homeArraylist = new ArrayList<>();
    ExamListAdapter homeAdapter;
    RecyclerviewClickListner recyclerviewClickListner;
    ConnectionDetector cd;
    private Context mContext;
    private View rootView;
    private int SelectedPos = 0;

    public SetSelectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_flip_set_selection, container, false);
        mContext = getContext();
        cd = new ConnectionDetector(mContext);
        //setActionBar();
        setFontStyle();
        Log.e(TAG, "onCreateView: setCheck " );
        init();
        listner();
        return rootView;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void setActionBar() {
        try {
            mToolbar = rootView.findViewById(R.id.toolbar_dash);
            //setSupportActionBar(mToolbar);
//            ActionBar actionBar = getSupportActionBar();
//            actionBar.setDisplayShowTitleEnabled(false);
//            actionBar.setDisplayHomeAsUpEnabled(false);
//            actionBar.setHomeButtonEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFontStyle() {
        // ((TextView) rootView.findViewById(R.id.tv_testName)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Bold));
        //((TextView) rootView.findViewById(R.id.tv_title)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Bold));
        ((TextView) rootView.findViewById(R.id.tv_selection_one)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.tv_selection_two)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.tv_selection_three)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));

    }

    private void init() {
        try {
            Bundle bundle = SetSelectionFragment.this.getArguments();
            if (bundle != null) {
                String bundleStr = bundle.getString("data");
                JSONObject object = new JSONObject(bundleStr);
                getAllExamList(object.getInt("id"));

                // ((TextView) rootView.findViewById(R.id.tv_testName)).setText(object.getString("displayName"));
            }else {
                Log.e(TAG, "setCheck init: else bundle null" );
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
                            ArrayList<JSONObject> lstBuy = new ArrayList<>();
                            if (response.code() == 200) {
                                JSONArray array = new JSONArray(response.body().string());
                                Log.e("array  ", array + "");
                                for (int i = 0; i < array.length(); i++) {
                                    homeArraylist.add(array.getJSONObject(i));
                                    if (array.getJSONObject(i).getBoolean("isFree") && array.getJSONObject(i).getString("contentType").equalsIgnoreCase("FlipSet")) {
                                        lstBuy.add(array.getJSONObject(i));
                                    }
                                }
                                PreferenceClass.setStringPreference(mContext, Constant.STORE_DATA, lstBuy.toString());
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

    private void getAllExamList(int id) {
        Log.e(TAG, "getAllExamList: setCheck "+id );
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
                            if (response.code() == 200) {
                                JSONObject obj = new JSONObject(response.body().string());
                                Log.e(TAG, "setCheck onResponse: obj "+obj );
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
            JSONArray array = obj.getJSONArray("FlipCards");
            ((TextView) rootView.findViewById(R.id.tv_selection_two)).setText("Start all " + array.length() + " FlipSet");
            if (array.length() > 4)
                ((TextView) rootView.findViewById(R.id.tv_selection_one)).setText("Start 5 random FlipSet");
            else
                ((TextView) rootView.findViewById(R.id.tv_selection_one)).setText("Start " + array.length() + " random FlipSet");
            for (int i = 0; i < array.length(); i++) {
                lstQuestions.add(array.getJSONObject(i));
            }
            for (int i = 1; i <= array.length(); i++) {
                if (i % 5 == 0) {
                    lstSpinner.add(i + " Card");
                }

            }
            if (array.length() % 5 != 0) {
                lstSpinner.add("All " + array.length() + " Card");
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.spiiner_text, lstSpinner);
            Spinner spinner = (Spinner) rootView.findViewById(R.id.spinnerQuestion);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setOnItemSelectedListener(this);
            spinner.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*case R.id.imv_back:
                //finish();
                loadFragment(new HomeFragment());
                break;*/
            case R.id.tv_selection_one:
                Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onClick:tv_selection_one setCheck "+lstQuestions);
                if (SelectedPos == 0) {
                    FlipQuestionListFragment flipQuestionListFragment = new FlipQuestionListFragment();
                    Bundle bundle = new Bundle();

                    if (lstQuestions.size() > 4) {
                        bundle.putString("data", lstQuestions.subList(0, 5) + "");
                        flipQuestionListFragment.setArguments(bundle);
                        loadFragment(flipQuestionListFragment);
                        //startActivity(new Intent(SetSelectionFragment.this, FlipQuestionListFragment.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                    } else {
                        bundle.putString("data", lstQuestions.subList(0, lstQuestions.size()) + "");
                        flipQuestionListFragment.setArguments(bundle);
                        loadFragment(flipQuestionListFragment);
                        // startActivity(new Intent(FlipSetSelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()) + ""));
                        // startActivity(new Intent(FlipSetSelectionActivity.this, MultiOptionQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                    }
                }/* else {

                    if (lstQuestions.size() % 5 != 0) {
                        Log.e(" lstQuestions size ", lstQuestions.size() + "");
                        Log.e(" not 555 >0 ", lstQuestions.subList(0, lstQuestions.size()).size() + "");
                        startActivity(new Intent(FlipSetSelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()).size() + ""));

                    } else {
                        Log.e(" df >0 ", lstQuestions.subList(0, 5 * (SelectedPos + 1)) + "");
                        startActivity(new Intent(FlipSetSelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5) + ""));
                        //startActivity(new Intent(SelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                    }
                }*/

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
               /* if (lstQuestions.size() % 5 != 0) {
                    Log.e(" lstQuestions size ", lstQuestions.size() + "");
                    Log.e(" not 555 >0 ", lstQuestions.subList(0, lstQuestions.size()).size() + "");
               */
                //  startActivity(new Intent(FlipSetSelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, lstQuestions.size()) + ""));

              /*  } else {
                    Log.e(" df >0 ", lstQuestions.subList(0, 5 * (SelectedPos + 1)) + "");
                    startActivity(new Intent(FlipSetSelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions + ""));
                    //startActivity(new Intent(FlipSetSelectionActivity.this, FlipQuestionListActivity.class).putExtra("data", lstQuestions.subList(0, 5) + ""));

                }*/
                // }
                break;
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.spinnerQuestion) {
            SelectedPos = position;
            ((TextView) rootView.findViewById(R.id.tv_selection_one)).setText("Start " + lstSpinner.get(position) + " random cards");



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
