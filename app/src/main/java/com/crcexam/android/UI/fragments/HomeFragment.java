package com.crcexam.android.UI.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.UI.dashboard.DashboardActivity;
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
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.crcexam.android.UI.dashboard.DashboardActivity.bottomNav;
import static com.crcexam.android.constants.Constant.UserData.EMAIL;
import static com.crcexam.android.constants.Constant.UserData.USER_NAME;

public class HomeFragment extends Fragment implements RecyclerviewClickListner, View.OnClickListener {

    View rootView;
    Context mContext;
    RecyclerView recyclerView;
    ArrayList<JSONObject> homeArraylist = new ArrayList<>();
    ExamListAdapter homeAdapter;
    RecyclerviewClickListner recyclerviewClickListner;
    ProgressHUD progressHUD;
    private ConnectionDetector connectionDetector;
    Toolbar mToolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();
        setFontStyle();
        setListener();
        connectionDetector = new ConnectionDetector(mContext);
        if (!connectionDetector.isConnectingToInternet()) {

            Utility.toastHelper(mContext.getResources().getString(R.string.check_network), mContext);
        }
        hideKeyboard(getActivity());
        setActionBar();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        return rootView;
    }

    private void setActionBar() {
        try {
            mToolbar = getActivity().findViewById(R.id.toolbar_dash);
            ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
            ( mToolbar.findViewById(R.id.imgback)).setVisibility(View.GONE);
            ((ImageView) mToolbar.findViewById(R.id.imgHome)).setVisibility(View.VISIBLE);
            ((ImageView) mToolbar.findViewById(R.id.imgHome)).setOnClickListener(this);
//            ((TextView) mToolbar.findViewById(R.id.tv_title)).setText("Result");
            ((TextView) mToolbar.findViewById(R.id.tv_title)).setVisibility(View.GONE);
//            ((TextView) mToolbar.findViewById(R.id.tv_title)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.OpenSans_Bold));
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setHomeButtonEnabled(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }





    private void setFontStyle() {
        ((TextView) rootView.findViewById(R.id.txtProfile)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtRef)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.btnSampleQuiz)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.btnSampleFlip)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
    }

    private void setListener() {
        rootView.findViewById(R.id.txtProfile).setOnClickListener(this);
        rootView.findViewById(R.id.txtRef).setOnClickListener(this);
        rootView.findViewById(R.id.cardView_sample_quiz).setOnClickListener(this);
        rootView.findViewById(R.id.cardView_sample_flipCard).setOnClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position, String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.getString("contentType").equalsIgnoreCase("FlipSet")) {
                loadFragment(new SetSelectionFragment());
            } else {
                loadFragment(new SelectionFragment());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getAllExamList() {
        try {
            progressHUD = ProgressHUD.show(getActivity(), "", true, false, new DialogInterface.OnCancelListener() {
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
                                    if (!array.getJSONObject(i).getBoolean("isFree")) {
                                        lstBuy.add(array.getJSONObject(i));
                                    }
                                }
                                PreferenceClass.setStringPreference(mContext, Constant.STORE_DATA, lstBuy.toString());
                                homeAdapter = new ExamListAdapter(getActivity(), homeArraylist, recyclerviewClickListner,"");
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardView_sample_quiz:
                MultipleSelectQstCountFragment fragment = new MultipleSelectQstCountFragment();
                Bundle bundle = new Bundle();
                bundle.putString("contentType", "MultipleChoice");
                fragment.setArguments(bundle);
                loadFragment(fragment);
                //startActivity(new Intent(getActivity(), MultipleSelectQstCountActivity.class).putExtra("contentType", "MultipleChoice"));
                break;
            case R.id.cardView_sample_flipCard:
                MultipleSelectQstCountFragment fragments = new MultipleSelectQstCountFragment();
                Bundle bundles = new Bundle();
                bundles.putString("contentType", "FlipSet");
                fragments.setArguments(bundles);
                loadFragment(fragments);
                              break;

            case R.id.txtProfile:
                loadFragment(new ProfileFragment());
                //  startActivity(new Intent(getActivity(), ProfileActivity.class));
                break;


            case R.id.txtRef:
                shareLink();
                break;

            case R.id.txtDirMsgTitle:
                /*Intent intent = new Intent(getActivity(), SplashScreenActivity.class);
                startActivity(intent);*/
                break;


            case R.id.imgHome:
                ((DashboardActivity)getActivity()).switchDrawer();
                break;

        }
    }


    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void shareLink() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Share App");
        share.putExtra(Intent.EXTRA_TEXT, "Download Licensure Exams from Play store now. https://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
        startActivity(Intent.createChooser(share, "Share App"));
    }


}
