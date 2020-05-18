package com.crcexam.UI.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.R;
import com.crcexam.UI.dashboard.DashboardActivity;
import com.crcexam.adapters.ExamListAdapter;
import com.crcexam.constants.Constant;
import com.crcexam.interfaces.RecyclerviewClickListner;
import com.crcexam.network.RetrofitLoggedIn;
import com.crcexam.network.RetrofitService;
import com.crcexam.utils.PreferenceClass;
import com.crcexam.utils.ProgressHUD;
import com.crcexam.utils.Utility;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MultipleSelectQstCountFragment extends Fragment implements RecyclerviewClickListner, View.OnClickListener {

    private static final String TAG = "MultipleSelectQstCountF";
    RecyclerView recyclerView;
    ArrayList<JSONObject> homeArraylist = new ArrayList<>();
    ArrayList<JSONObject> lstMultipleChoice = new ArrayList<>();
    ArrayList<JSONObject> lstFlipSet = new ArrayList<>();
    ExamListAdapter homeAdapter;
    RecyclerviewClickListner recyclerviewClickListner;
    PreferenceClass preferenceClass;
    ProgressHUD progressHUD;
    HashSet<String> hashSet = new HashSet<String>();
    Toolbar mToolbar;
    int count;
    private Context mContext;
    private View rootView;

    public MultipleSelectQstCountFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_multiple_select_qst_count, container, false);
        mContext = getContext();
        preferenceClass = new PreferenceClass();
        recyclerviewClickListner = this;
        recyclerView = rootView.findViewById(R.id.rv_homelist);
        setActionBar();
        getAllExamList();
        hashSet = (HashSet<String>) PreferenceClass.getExams(getActivity());

        for (String str: hashSet){
            getPurchasedExamList(Integer.parseInt(str));
        }

         return rootView;
    }

    private void setActionBar() {
        try {
            mToolbar = getActivity().findViewById(R.id.toolbar_dash);
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setVisibility(View.VISIBLE);
            ((ImageView) mToolbar.findViewById(R.id.imgHome)).setVisibility(View.GONE);
            ((ImageView) mToolbar.findViewById(R.id.imgback)).setOnClickListener(this);
            Bundle bundle = MultipleSelectQstCountFragment.this.getArguments();
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setText(bundle.getString("contentType"));
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setVisibility(View.VISIBLE);
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.OpenSans_Bold));
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(false);

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
                PreferenceClass.setExamId(getActivity(), object.getString("id"));

                FragmentTransaction transaction = Objects.requireNonNull(this.getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, setSelectionFragment, Constant.SETSELECTIONFRAGMENT);
                transaction.commit();
            } else {
                SelectionFragment selectionFragment = new SelectionFragment();
                bundle.putString("data", response);
                selectionFragment.setArguments(bundle);
                PreferenceClass.setExamId(getActivity(), object.getString("id"));

                FragmentTransaction transaction = Objects.requireNonNull(this.getActivity()).getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, selectionFragment, Constant.SELECTIONFRAGMENT);
                transaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
                        count = 0;
                        try {
                            if (progressHUD.isShowing() && progressHUD != null) {
                                progressHUD.dismiss();
                            }
                            homeArraylist.clear();

                            if (response.code() == 200) {
                                JSONArray array = new JSONArray(response.body().string());
                                Log.e("array  ", array + "");
                                Bundle bundle = MultipleSelectQstCountFragment.this.getArguments();
                                if (bundle != null) {
                                    if (bundle.getString("contentType").equalsIgnoreCase("MultipleChoice")) {
                                        for (int i = 0; i < array.length(); i++) {
                                            count++;
                                            if (array.getJSONObject(i).getBoolean("isFree") && array.getJSONObject(i).getString("contentType").equalsIgnoreCase("MultipleChoice")) {
                                                lstMultipleChoice.add(array.getJSONObject(i));
                                            }
                                        }
                                        PreferenceClass.setMultiple(getActivity(), count);
                                    }

                                    if (bundle.getString("contentType").equalsIgnoreCase("FlipSet")) {
                                        for (int i = 0; i < array.length(); i++) {
                                            count++;
                                            if (array.getJSONObject(i).getBoolean("isFree") && array.getJSONObject(i).getString("contentType").equalsIgnoreCase("FlipSet")) {
                                                lstFlipSet.add(array.getJSONObject(i));
                                            }
                                        }
                                        PreferenceClass.setFlip(getActivity(), count);
                                    }

                                    PreferenceClass.setStringPreference(mContext, Constant.STORE_DATA, lstMultipleChoice.toString());

                                    if (bundle.getString("contentType").equalsIgnoreCase("FlipSet")) {
                                        homeAdapter = new ExamListAdapter(mContext, lstFlipSet, recyclerviewClickListner,"FQL");
                                    } else {
                                        homeAdapter = new ExamListAdapter(mContext, lstMultipleChoice, recyclerviewClickListner,"SQ");
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

    private void getPurchasedExamList(int examID) {
        try {
//            progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    // TODO Auto-generated method stub
//                }
//            });
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.getExamByID(6, examID).enqueue(new Callback<ResponseBody>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
//                            if (progressHUD.isShowing() && progressHUD != null) {
//                                progressHUD.dismiss();
//                            }
                            homeArraylist.clear();

                            if (response.code() == 200) {
                                JSONObject array = new JSONObject(response.body().string());
                                Log.e("array  ", array + "");
                                Bundle bundle = MultipleSelectQstCountFragment.this.getArguments();
                                if (bundle != null) {
                                    if (bundle.getString("contentType").equalsIgnoreCase("MultipleChoice"))
//                                        for (int i = 0; i < array.length(); i++) {
                                            if (array.getString("contentType").equalsIgnoreCase("MultipleChoice")) {
                                                lstMultipleChoice.add(array);
                                            }
//                                        }

                                    if (bundle.getString("contentType").equalsIgnoreCase("FlipSet"))
//                                        for (int i = 0; i < array.length(); i++) {
                                            if (array.getString("contentType").equalsIgnoreCase("FlipSet")) {
                                                lstFlipSet.add(array);
                                            }
//                                        }

                                    PreferenceClass.setStringPreference(mContext, Constant.STORE_DATA, lstMultipleChoice.toString());

                                    if (bundle.getString("contentType").equalsIgnoreCase("FlipSet")) {
                                        homeAdapter = new ExamListAdapter(mContext, lstFlipSet, recyclerviewClickListner,"FQL");
                                    } else {
                                        homeAdapter = new ExamListAdapter(mContext, lstMultipleChoice, recyclerviewClickListner,"SQ");
                                    }

//                                            homeAdapter.notifyDataSetChanged();
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
                loadFragment(new HomeFragment());

//                Comment by Kanwar
//                try {
//                    final MultipleSelectQstCountFragment multipleSelectQstCountFragment =
//                            (MultipleSelectQstCountFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Constant.MULTIPLESELECTQSTCOUNTFRAGMENT);
//                    final SelectionFragment selectionFragment =
//                            (SelectionFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Constant.SELECTIONFRAGMENT);
//                    final SetSelectionFragment setSelectionFragment =
//                            (SetSelectionFragment) getActivity().getSupportFragmentManager().findFragmentByTag(Constant.SETSELECTIONFRAGMENT);
//
//                    if(multipleSelectQstCountFragment!=null && multipleSelectQstCountFragment.getTag().equals(Constant.SETSELECTIONFRAGMENT)){
//                        int count = getFragmentManager().getBackStackEntryCount();
//                        for(int i=0; getFragmentManager().getBackStackEntryCount() > 0;i++) {
//                            if (getFragmentManager().getBackStackEntryCount() > 0) {
//                                getFragmentManager().popBackStack();
//
//                            }
//                        }
//                        FragmentTransaction transaction = Objects.requireNonNull(this.getActivity()).getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.frame_container, new HomeFragment(), Constant.HOMEFRAGMENT);
//                        transaction.commit();
//                    }else if(selectionFragment!=null && selectionFragment.getTag().equals(Constant.SETSELECTIONFRAGMENT)){
//                        int count = getFragmentManager().getBackStackEntryCount();
//                        for(int i=0; getFragmentManager().getBackStackEntryCount() > 0;i++) {
//                            if (getFragmentManager().getBackStackEntryCount() > 0) {
//                                getFragmentManager().popBackStack();
//
//                            }
//                        }
//                        FragmentTransaction transaction = Objects.requireNonNull(this.getActivity()).getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.frame_container, new SelectionFragment(), Constant.SETSELECTIONFRAGMENT);
//                        transaction.commit();
//
//                    }else if(setSelectionFragment!=null && setSelectionFragment.getTag().equals(Constant.SETSELECTIONFRAGMENT)){
//
//                        Bundle bundle = MultipleSelectQstCountFragment.this.getArguments();
//                        Bundle bundle2 = new Bundle();
//                        if (bundle != null) {
//                            if (bundle.getString("contentType").equalsIgnoreCase("MultipleChoice")){
//                                bundle2.putString("contentType", "MultipleChoice");
//                            }
//                            else if (bundle.getString("contentType").equalsIgnoreCase("FlipSet")){
//                                bundle2.putString("contentType", "FlipSet");
//                            }
//                        }
//                        int count = getFragmentManager().getBackStackEntryCount();
//                        for(int i=0; getFragmentManager().getBackStackEntryCount() > 0;i++) {
//                            if (getFragmentManager().getBackStackEntryCount() > 0) {
//                                getFragmentManager().popBackStack();
//
//                            }
//                        }
//                        Fragment fragment = new MultipleSelectQstCountFragment();
//                        fragment.setArguments(bundle2);
//                        FragmentTransaction transaction = Objects.requireNonNull(this.getActivity()).getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.frame_container, fragment, Constant.SETSELECTIONFRAGMENT);
//                        transaction.commit();
//                    }
//                }catch (Exception e){
//                    int count = getFragmentManager().getBackStackEntryCount();
//                    for(int i=0; getFragmentManager().getBackStackEntryCount() > 0;i++) {
//                        if (getFragmentManager().getBackStackEntryCount() > 0) {
//                            getFragmentManager().popBackStack();
//
//                        }
//                    }
//                    e.printStackTrace();
//                }





//                Bundle bundle = MultipleSelectQstCountFragment.this.getArguments();
//                if (bundle != null) {
//                    if (bundle.getString("contentType").equalsIgnoreCase("MultipleChoice")){
//
//                        HomeFragment fragment = new HomeFragment();
//                        Bundle bundle2 = new Bundle();
//                        bundle2.putString("contentType", "MultipleChoice");
//                        fragment.setArguments(bundle2);
////                        loadFragment(fragment);
//
//                        FragmentTransaction transaction = Objects.requireNonNull(this.getActivity()).getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.frame_container, fragment, Constant.HOMEFRAGMENT);
//                        transaction.commit();
//                    }
//                    else if (bundle.getString("contentType").equalsIgnoreCase("FlipSet")){
//                        HomeFragment fragment = new HomeFragment();
//                        Bundle bundle2 = new Bundle();
//                        bundle2.putString("contentType", "FlipSet");
//                        fragment.setArguments(bundle2);
////                        loadFragment(fragment);
//
//                        FragmentTransaction transaction = Objects.requireNonNull(this.getActivity()).getSupportFragmentManager().beginTransaction();
//                        transaction.replace(R.id.frame_container, fragment, Constant.HOMEFRAGMENT);
//                        transaction.commit();
//                    }
//
//                }


//                loadFragment(new HomeFragment());

//                backPress();
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loadFragment(Fragment fragment) {
//        FragmentManager fm = getFragmentManager();
//        if (fm.getBackStackEntryCount() > 0) {
//            Log.i("MainActivity", "popping backstack");
//            fm.popBackStack();
//        } else {
//            Log.i("MainActivity", "nothing on backstack, calling super");
////            super.onBackPressed();
//            getActivity().finish();
//        }

        FragmentTransaction transaction = Objects.requireNonNull((DashboardActivity)mContext).getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void backPress(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        if (fm.getBackStackEntryCount() > 0) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();
        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
//            super.onBackPressed();
            getActivity().finish();
        }

    }
}
