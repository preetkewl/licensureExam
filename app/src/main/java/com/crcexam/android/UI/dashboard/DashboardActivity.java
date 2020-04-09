package com.crcexam.android.UI.dashboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.UI.auth.LoginActivity;
import com.crcexam.android.UI.fragments.HistoryFragment;
import com.crcexam.android.UI.fragments.HomeFragment;
import com.crcexam.android.UI.fragments.InfoFragment;
import com.crcexam.android.UI.fragments.MultipleSelectQstCountFragment;
import com.crcexam.android.UI.fragments.ProfileFragment;
import com.crcexam.android.UI.fragments.SelectionFragment;
import com.crcexam.android.UI.fragments.SetSelectionFragment;
import com.crcexam.android.UI.fragments.StoreFragment;
import com.crcexam.android.constants.Constant;
import com.crcexam.android.network.RetrofitLoggedIn;
import com.crcexam.android.network.RetrofitService;
import com.crcexam.android.utils.PreferenceClass;
import com.crcexam.android.utils.Utility;

import org.json.JSONObject;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.crcexam.android.constants.Constant.EMAIL;
import static com.crcexam.android.constants.Constant.UserData.USER_NAME;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private static final String TAG = "DashboardActivity";
    public static BottomNavigationView bottomNav;
    Context mContext;
    Toolbar mToolbar;
    private NavigationView navDrawer;
    private DrawerLayout mdrawer;
    Button logoutBT;
    ImageView iv_back_btn;
    Activity activity = this;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    openDashboard();
                    return true;
                case R.id.navigation_results:
                    openHistory();
                    return true;
                case R.id.navigation_store:
                    openStore();
                    return true;
                case R.id.navigation_info:
                    openInfo();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        setActionBar();
        mdrawer = findViewById(R.id.drawer_layout);
        navDrawer = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mdrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mdrawer.addDrawerListener(toggle);
        toggle.syncState();
        navDrawer.setNavigationItemSelectedListener(this);
        bottomNav = findViewById(R.id.navigation);
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        openDashboard();

        //setDrawerData();
        getProfile();


        logoutBT= (Button) findViewById(R.id.menu_logout);
        logoutBT.setOnClickListener(this);
        View headerView = navDrawer.getHeaderView(0);
//        TextView navUsername = (TextView) headerView.findViewById(R.id.navUsername);
        iv_back_btn = (ImageView) headerView.findViewById(R.id.iv_back_btn);
//        navUsername.setText("Your Text Here");

        setStatusBarColor();
        setClickListeners();
        hideKeyboard(activity);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    private void setStatusBarColor() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    private void getProfile() {
        Log.e(TAG, "getProfile:vvvvvvvvvvvvvvvvvvvvvvvvvvvvvv ");
        try {
            RetrofitLoggedIn retrofitLoggedIn = new RetrofitLoggedIn();
            Retrofit retrofit = retrofitLoggedIn.RetrofitClient(this, false);
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
                                //if (object.getString("responsecode").equalsIgnoreCase("200")) {
                                    Log.e("log obj ", object + "");
                                    String username = object.getJSONObject("account").getString("FirstName") + " " + object.getJSONObject("account").getString("LastName");
                                    String useremail = object.getJSONObject("account").getString("Username");
                                    Log.e(TAG, "username onResponse: " + useremail);
                                    ((TextView) navDrawer.findViewById(R.id.tvDrawerName)).setText(object.getJSONObject("account").getString("FirstName") + " " + object.getJSONObject("account").getString("LastName"));
                                    ((TextView)  navDrawer.findViewById(R.id.tvDrawerEmail)).setText(object.getJSONObject("account").getString("Username"));

                                    PreferenceClass.setStringPreference(mContext, USER_NAME, username);
                                    PreferenceClass.setStringPreference(mContext, EMAIL, useremail);

                                    Log.e(TAG, "PreferenceClass onResponse: " + PreferenceClass.getStringPreferences(mContext, EMAIL));
                               // } else {
                                    //Do nothing
                                //}

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

    private void setDrawerData() {
        if (!PreferenceClass.getStringPreferences(mContext, USER_NAME).equalsIgnoreCase("") && !PreferenceClass.getStringPreferences(mContext, EMAIL).equalsIgnoreCase("")) {
            ((TextView) findViewById(R.id.tvDrawerName)).setText(PreferenceClass.getStringPreferences(mContext, USER_NAME));
            ((TextView) findViewById(R.id.tvDrawerEmail)).setText(PreferenceClass.getStringPreferences(mContext, EMAIL));
        } else {
           // ((TextView) findViewById(R.id.tvDrawerName)).setText("Your Name");
           // ((TextView) findViewById(R.id.tvDrawerEmail)).setText("Your Email");
        }

    }

    private void setClickListeners() {
        findViewById(R.id.imgHome).setOnClickListener(this);
        iv_back_btn.setOnClickListener(this);
    }

    private void setActionBar() {
        try {
            mToolbar = findViewById(R.id.toolbar_dash);
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle bottomNav view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_dashboard) {
            bottomNav.setSelectedItemId(R.id.navigation_home);
            openDashboard();
        } else if (id == R.id.menu_results) {
            bottomNav.setSelectedItemId(R.id.navigation_results);
            openHistory();
        } else if (id == R.id.menu_store) {
            bottomNav.setSelectedItemId(R.id.navigation_store);
            openStore();
        } else if (id == R.id.menu_profile) {
            bottomNav.setSelected(false);
            openProfile();
        } else if (id == R.id.menu_logout) {
            logout();
        } else if (id == R.id.menu_refer) {
            mdrawer.closeDrawer(Gravity.LEFT);
            shareLink();
            return false;
        } else if (id == R.id.menu_info) {
            bottomNav.setSelectedItemId(R.id.navigation_info);
            openInfo();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout() {
        PreferenceClass.clearPreference(mContext);
        Intent intent = new Intent(mContext, LoginActivity.class);
        //Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        try {

//            int count = getFragmentManager().getBackStackEntryCount();
//            for(int i=0; getFragmentManager().getBackStackEntryCount() > 0;i++) {
//                if (getFragmentManager().getBackStackEntryCount() > 0) {
//                    getFragmentManager().popBackStack();
//
//                }
//            }

            final MultipleSelectQstCountFragment multipleSelectQstCountFragment =
                    (MultipleSelectQstCountFragment) getSupportFragmentManager().findFragmentByTag(Constant.MULTIPLESELECTQSTCOUNTFRAGMENT);
            final SelectionFragment selectionFragment =
                    (SelectionFragment) getSupportFragmentManager().findFragmentByTag(Constant.SELECTIONFRAGMENT);
            final SetSelectionFragment setSelectionFragment =
                    (SetSelectionFragment) getSupportFragmentManager().findFragmentByTag(Constant.SETSELECTIONFRAGMENT);

            if(multipleSelectQstCountFragment!=null && multipleSelectQstCountFragment.getTag().equals(Constant.SETSELECTIONFRAGMENT)){
//                int count = getFragmentManager().getBackStackEntryCount();
                for(int i=0; getFragmentManager().getBackStackEntryCount() > 0;i++) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();

                    }
                }

            }else if(selectionFragment!=null && selectionFragment.getTag().equals(Constant.SETSELECTIONFRAGMENT)){
//                int count = getFragmentManager().getBackStackEntryCount();
                for(int i=0; getFragmentManager().getBackStackEntryCount() > 0;i++) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();

                    }
                }


            }else if(setSelectionFragment!=null && setSelectionFragment.getTag().equals(Constant.SETSELECTIONFRAGMENT)){


//                int count = getFragmentManager().getBackStackEntryCount();
                for(int i=0; getFragmentManager().getBackStackEntryCount() > 0;i++) {
                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        getFragmentManager().popBackStack();

                    }
                }

            }
        }catch (Exception e){
            int count = getFragmentManager().getBackStackEntryCount();
            for(int i=0; getFragmentManager().getBackStackEntryCount() > 0;i++) {
                if (getFragmentManager().getBackStackEntryCount() > 0) {
                    getFragmentManager().popBackStack();

                }
            }
            e.printStackTrace();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getCurrentFragment() instanceof HomeFragment) {
                super.onBackPressed();
            } else {
                bottomNav.setSelectedItemId(R.id.navigation_home);
                loadFragment(new HomeFragment());
            }
            //super.onBackPressed();
        }
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.frame_container);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgHome:
                switchDrawer();
                break;
            case R.id.menu_logout:
                logout();
                break;

                case R.id.iv_back_btn:
                switchDrawer();
                break;
        }
    }

    private void shareLink() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "Share App");
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getApplication().getPackageName());
        startActivity(Intent.createChooser(share, "Share App"));
    }


    public void switchDrawer() {
        if(mdrawer!=null && navDrawer!=null) {
            if (mdrawer.isDrawerOpen(navDrawer)) {
                mdrawer.closeDrawer(Gravity.LEFT);
            } else {
                mdrawer.openDrawer(Gravity.LEFT);
            }
        }
    }

    private void openProfile() {
        if (getCurrentFragment() instanceof ProfileFragment) {
            switchDrawer();
        } else {
            setToolbarTitle(getResources().getString(R.string.profile));
            navDrawer.setCheckedItem(R.id.menu_profile);
            loadFragment(new ProfileFragment());
        }
    }

    private void openInfo() {
        if (getCurrentFragment() instanceof InfoFragment) {
            switchDrawer();
        } else {
            setToolbarTitle(getResources().getString(R.string.info));
            navDrawer.setCheckedItem(R.id.menu_info);
            loadFragment(new InfoFragment());
        }
    }

    private void setToolbarTitle(String title) {
        try {
            ((TextView) findViewById(R.id.tv_title)).setText(title);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openStore() {
        if (getCurrentFragment() instanceof StoreFragment) {
            switchDrawer();
        } else {
            setToolbarTitle(getResources().getString(R.string.store));
            navDrawer.setCheckedItem(R.id.menu_store);
            loadFragment(new StoreFragment());
        }

    }

    private void openHistory() {

        if (getCurrentFragment() instanceof HistoryFragment) {
            switchDrawer();
        } else {
            setToolbarTitle(getResources().getString(R.string.result));
            navDrawer.setCheckedItem(R.id.menu_results);
            loadFragment(new HistoryFragment());
        }
    }

    private void openDashboard() {
        if (getCurrentFragment() instanceof HomeFragment) {
            switchDrawer();
        } else {
            setToolbarTitle("");
//            setToolbarTitle(getResources().getString(R.string.crc_exam));
            navDrawer.setCheckedItem(R.id.menu_dashboard);
            loadFragment(new HomeFragment());
        }
    }
}


