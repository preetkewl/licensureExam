package com.crcexam.android.UI.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.adapters.ExamListAdapter;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.interfaces.RecyclerviewClickListner;
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
public class MultipleSelectQstCountFragment extends Fragment implements RecyclerviewClickListner, View.OnClickListener {

    private static final String TAG = "MultipleSelectQstCountF";
    RecyclerView recyclerView;
    ArrayList<JSONObject> homeArraylist = new ArrayList<>();
    ExamListAdapter homeAdapter;
    RecyclerviewClickListner recyclerviewClickListner;
    ProgressHUD progressHUD;
    Toolbar mToolbar;
    private Context mContext;
    private View rootView;

    public MultipleSelectQstCountFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_multiple_select_qst_count, container, false);
        mContext = getContext();
        recyclerviewClickListner = this;
        recyclerView = rootView.findViewById(R.id.rv_homelist);
        //setActionBar();
        getAllExamList();
        return rootView;
    }

    private void setActionBar() {
        try {
            mToolbar = mToolbar.findViewById(R.id.toolbar_dash);
            //setSupportActionBar(mToolbar);
            Log.e("contentType sss ", getActivity().getIntent().getExtras().getString("contentType"));
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setText(getActivity().getIntent().getExtras().getString("contentType"));
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setVisibility(View.VISIBLE);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setOnClickListener(this);
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.OpenSans_Bold));
            //ActionBar actionBar = getSupportActionBar();
            //actionBar.setDisplayShowCustomEnabled(true);
            //actionBar.setDisplayShowTitleEnabled(false);
            //actionBar.setDisplayHomeAsUpEnabled(false);
            //actionBar.setHomeButtonEnabled(false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
/*

    private void adapter() {
        try {
            recyclerView = findViewById(R.id.rv_homelist);
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONArray jsonArray = obj.getJSONArray("demolist");
            for (int i = 0; i < jsonArray.length(); i++) {
                homeArraylist.add(jsonArray.getJSONObject(i));
            }
            homeAdapter = new ExamListAdapter(mContext, homeArraylist, recyclerviewClickListner);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(homeAdapter);
            recyclerView.setNestedScrollingEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("demolist.json");
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
*/

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onItemClick(View view, int position, String response) {
        try {
            Log.e(TAG, "onItemClick: setCheck " + response);
            JSONObject object = new JSONObject(response);
            Bundle bundle = new Bundle();
            if (object.getString("contentType").equalsIgnoreCase("FlipSet")) {
                SetSelectionFragment setSelectionFragment = new SetSelectionFragment();
                bundle.putString("data", response);
                setSelectionFragment.setArguments(bundle);
                loadFragment(setSelectionFragment);
                // startActivity(new Intent(mContext, SetSelectionFragment.class).putExtra("data", response));
            } else {
                SelectionFragment selectionFragment = new SelectionFragment();
                bundle.putString("data", response);
                selectionFragment.setArguments(bundle);
                loadFragment(selectionFragment);
                //startActivity(new Intent(mContext, SelectionFragment.class).putExtra("data", response));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //(getIntent().getExtras().getString("KEY"));

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
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
                            if (progressHUD.isShowing() && progressHUD != null) {
                                progressHUD.dismiss();
                            }
                            Log.e("onResponse  ", response.code() + "");
                            homeArraylist.clear();
                            ArrayList<JSONObject> lstMultipleChoice = new ArrayList<>();
                            ArrayList<JSONObject> lstFlipSet = new ArrayList<>();
                            if (response.code() == 200) {
                                JSONArray array = new JSONArray(response.body().string());
                                Log.e("array  ", array + "");
                                Bundle bundle = MultipleSelectQstCountFragment.this.getArguments();
                                if (bundle != null) {
                                    if (bundle.getString("contentType").equalsIgnoreCase("MultipleChoice"))
                                        for (int i = 0; i < array.length(); i++) {
                                            //homeArraylist.add(array.getJSONObject(i));
                                            if (array.getJSONObject(i).getBoolean("isFree") && array.getJSONObject(i).getString("contentType").equalsIgnoreCase("MultipleChoice")) {
                                                lstMultipleChoice.add(array.getJSONObject(i));
                                            }
                                        }

                                    if (bundle.getString("contentType").equalsIgnoreCase("FlipSet"))
                                        for (int i = 0; i < array.length(); i++) {
                                            if (array.getJSONObject(i).getBoolean("isFree") && array.getJSONObject(i).getString("contentType").equalsIgnoreCase("FlipSet")) {
                                                lstFlipSet.add(array.getJSONObject(i));
                                            }
                                        }
                                    PreferenceClass.setStringPreference(mContext, Constant.STORE_DATA, lstMultipleChoice.toString());

                                    if (bundle.getString("contentType").equalsIgnoreCase("FlipSet")) {
                                        homeAdapter = new ExamListAdapter(mContext, lstFlipSet, recyclerviewClickListner);

                                    } else {
                                        homeAdapter = new ExamListAdapter(mContext, lstMultipleChoice, recyclerviewClickListner);
                                    }
                                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
                                    recyclerView.setLayoutManager(mLayoutManager);
                                    recyclerView.setAdapter(homeAdapter);
                                    recyclerView.setNestedScrollingEnabled(false);
                                }

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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgback:
                //finish();
                Objects.requireNonNull(getActivity()).onBackPressed();
                //loadFragment(new HomeFragment());
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();

    }
}
