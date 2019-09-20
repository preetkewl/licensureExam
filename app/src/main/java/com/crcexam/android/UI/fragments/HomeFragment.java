package com.crcexam.android.UI.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

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

import static com.crcexam.android.UI.dashboard.DashboardActivity.bottomNav;
import static com.crcexam.android.constants.Constant.UserData.EMAIL;
import static com.crcexam.android.constants.Constant.UserData.USER_NAME;

public class HomeFragment extends Fragment implements RecyclerviewClickListner, View.OnClickListener {

    public static final String mypreference = "mypref";
    private static final String TAG = "HomeFragment";
    View rootView;
    Context mContext;
    RecyclerView recyclerView;
    ArrayList<JSONObject> homeArraylist = new ArrayList<>();
    ExamListAdapter homeAdapter;
    RecyclerviewClickListner recyclerviewClickListner;
    ProgressHUD progressHUD;
    private ConnectionDetector connectionDetector;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();
        setFontStyle();
        setListener();
        connectionDetector = new ConnectionDetector(mContext);
        if (connectionDetector.isConnectingToInternet()) {
            /*if (PreferenceClass.getStringPreferences(mContext, EMAIL).equalsIgnoreCase("")) {*/
//            progressHUD = ProgressHUD.show(mContext, "", true, false, new DialogInterface.OnCancelListener() {
//                @Override
//                public void onCancel(DialogInterface dialog) {
//                    // TODO Auto-generated method stub
//                }
//            });
            getProfile();
            //}
        } else {
            Utility.toastHelper(mContext.getResources().getString(R.string.check_network), mContext);
        }
        //getAllExamList();
        return rootView;
    }

    private void getProfile() {
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(getActivity(), false);
            RetrofitService home2hotel = null;
            if (retrofit != null) {
                home2hotel = retrofit.create(RetrofitService.class);
            }
            if (home2hotel != null) {
                home2hotel.getProfile(Constant.API_KEY, Constant.SITE_ID, PreferenceClass.getStringPreferences(mContext, Constant.UserData.AUTH_TOKEN)).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        try {
//                            if (progressHUD != null && progressHUD.isShowing()) {
//                                progressHUD.dismiss();
//                            }
                            // hideLoader(indicatorView);

                            Log.e("res code ", response.code() + "");
                            if (response.code() == 200) {
                                String res = response.body().string();
                                Log.e("log object ", res + "");
                                JSONObject object = new JSONObject(res);
                                if (object.getString("responsecode").equalsIgnoreCase("200")){
                                    Log.e("log obj ", object + "");
                                    String username = object.getJSONObject("account").getString("FirstName") + " " + object.getJSONObject("account").getString("LastName");
                                    String useremail = object.getJSONObject("account").getString("Username");

                                    Log.e(TAG, "username onResponse: " + useremail);
                                    ((TextView) getActivity().findViewById(R.id.tvDrawerName)).setText(object.getJSONObject("account").getString("FirstName") + " " + object.getJSONObject("account").getString("LastName"));
                                    ((TextView) getActivity().findViewById(R.id.tvDrawerEmail)).setText(object.getJSONObject("account").getString("Username"));

                                    PreferenceClass.setStringPreference(mContext, USER_NAME, username);
                                    PreferenceClass.setStringPreference(mContext, EMAIL, useremail);
                                    Log.e(TAG, "PreferenceClass onResponse: " + PreferenceClass.getStringPreferences(mContext, EMAIL));
                                }else {
                                    //Do nothing
                                }

                            } else {
                                String error = response.errorBody().string();
                                Log.e("errorr ", error);
                                JSONObject object = new JSONObject(error);
                                if (object.has("response")) {
                                    Utility.toastHelper(object.getString("response"), mContext);
                                } else {
                                    Utility.toastHelper(response.message(), mContext);
                                }
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

    private void setFontStyle() {
        //((TextView) rootView.findViewById(R.id.txtDashboard)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
       // ((TextView) rootView.findViewById(R.id.txtResult)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtProfile)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.txtRef)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        //((TextView) rootView.findViewById(R.id.txtDirMsg)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        //  ((TextView) rootView.findViewById(R.id.txtDirMsgTitle)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.btnSampleQuiz)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        ((TextView) rootView.findViewById(R.id.btnSampleFlip)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
//        ((TextView) rootView.findViewById(R.id.btnExamPro)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));
        //((TextView) rootView.findViewById(R.id.btnPurchasedItem)).setTypeface(Utility.setFontStyle(mContext, Constant.FontStyle.Roboto_Light));

    }

    private void setListener() {
//        ((TextView) rootView.findViewById(R.id.btnSampleQuiz)).setOnClickListener(this);
//        ((TextView) rootView.findViewById(R.id.btnSampleFlip)).setOnClickListener(this);
//        ((TextView) rootView.findViewById(R.id.btnExamPro)).setOnClickListener(this);
        rootView.findViewById(R.id.txtProfile).setOnClickListener(this);
        rootView.findViewById(R.id.txtRef).setOnClickListener(this);
        //rootView.findViewById(R.id.txtResult).setOnClickListener(this);
        rootView.findViewById(R.id.cardView_sample_quiz).setOnClickListener(this);
        rootView.findViewById(R.id.cardView_sample_flipCard).setOnClickListener(this);
        //rootView.findViewById(R.id.cardView_exam_pro).setOnClickListener(this);
       // rootView.findViewById(R.id.cardView_Prchased_item).setOnClickListener(this);

        //   ((TextView) rootView.findViewById(R.id.txtDirMsgTitle)).setOnClickListener(this);

    }

    @Override
    public void onItemClick(View view, int position, String response) {
        try {
            JSONObject object = new JSONObject(response);
            if (object.getString("contentType").equalsIgnoreCase("FlipSet")) {
                loadFragment(new SetSelectionFragment());
                //startActivity(new Intent(getActivity(), FlipSetSelectionActivity.class).putExtra("data", response));

            } else {
                loadFragment(new SelectionFragment());
                // startActivity(new Intent(getActivity(), SelectionActivity.class).putExtra("data", response));

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
                //loadFragment(new MultipleSelectQstCountFragment());
                // startActivity(new Intent(getActivity(), MultipleSelectQstCountActivity.class).putExtra("contentType", "FlipSet"));
                break;
            /*case R.id.cardView_exam_pro:
                bottomNav.setSelectedItemId(R.id.navigation_store);
                loadFragment(new StoreFragment());
                break;*/
            /*case R.id.cardView_Prchased_item:
                loadFragment(new PurchasedItemsFragment());
                break;*/
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

          /*  case R.id.txtResult:
                bottomNav.setSelectedItemId(R.id.navigation_results);
                loadFragment(new HistoryFragment());
                break;*/
        }
    }


    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        //transaction.addToBackStack(fragment.getClass().getName());
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
