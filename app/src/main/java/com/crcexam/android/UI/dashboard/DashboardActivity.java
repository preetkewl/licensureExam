package com.crcexam.android.UI.dashboard;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.crcexam.android.R;
import com.crcexam.android.UI.fragments.HistoryFragment;
import com.crcexam.android.UI.fragments.HomeFragment;
import com.crcexam.android.UI.fragments.InfoFragment;
import com.crcexam.android.UI.fragments.ProfileFragment;
import com.crcexam.android.UI.fragments.StoreFragment;
import com.crcexam.android.helper.BottomNavigationBehavior;
import com.crcexam.android.utils.PreferenceClass;

import static com.crcexam.android.constants.Constant.UserData.EMAIL;
import static com.crcexam.android.constants.Constant.UserData.USER_NAME;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public static BottomNavigationView bottomNav;
    Context mContext;
    Toolbar mToolbar;
    private static final String TAG = "DashboardActivity";
    private NavigationView navDrawer;
    private DrawerLayout mdrawer;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    openDashboard();
                    return true;
                case R.id.navigation_history:
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
        bottomNav = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        openDashboard();
        setClickListeners();
        setDrawerData();
    }

    private void setDrawerData() {
       /* if (!PreferenceClass.getStringPreferences(mContext, USER_NAME).equalsIgnoreCase("")) {
            ((TextView) findViewById(R.id.tvDrawerName)).setText(PreferenceClass.getStringPreferences(mContext, USER_NAME));
            ((TextView) findViewById(R.id.tvDrawerEmail)).setText(PreferenceClass.getStringPreferences(mContext, EMAIL));
        } else if (!PreferenceClass.getStringPreferences(mContext, EMAIL).equalsIgnoreCase("")) {
            ((TextView) findViewById(R.id.tvDrawerName)).setText(PreferenceClass.getStringPreferences(mContext, USER_NAME));
            ((TextView) findViewById(R.id.tvDrawerEmail)).setText(PreferenceClass.getStringPreferences(mContext, EMAIL));
        } else {
            ((TextView) findViewById(R.id.tvDrawerName)).setText("Your Name");
            ((TextView) findViewById(R.id.tvDrawerEmail)).setText("Your Email");
        }*/

    }

    private void setClickListeners() {
        ((ImageView) findViewById(R.id.imgMenu)).setOnClickListener(this);
    }

    private void setActionBar() {
        try {
            mToolbar = findViewById(R.id.toolbar_dash);
            setSupportActionBar(mToolbar);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setHomeButtonEnabled(false);
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
            bottomNav.setSelectedItemId(R.id.navigation_history);
            openHistory();
        } else if (id == R.id.menu_profile) {
            bottomNav.setSelected(false);
            openProfile();
        } else if (id == R.id.menu_history) {
            bottomNav.setSelectedItemId(R.id.navigation_history);
            openHistory();
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

    @Override
    public void onBackPressed() {
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
            case R.id.imgMenu:
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
        share.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=com.crcexam.android");
        startActivity(Intent.createChooser(share, "Share App"));
    }


    private void switchDrawer() {
        if (mdrawer.isDrawerOpen(navDrawer)) {
            mdrawer.closeDrawer(Gravity.LEFT);
        } else {
            mdrawer.openDrawer(Gravity.LEFT);
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

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void openStore() {
        if (getCurrentFragment() instanceof StoreFragment) {
            switchDrawer();
        } else {
            setToolbarTitle(getResources().getString(R.string.store));
            loadFragment(new StoreFragment());
        }

    }

    private void openHistory() {

        if (getCurrentFragment() instanceof HistoryFragment) {
            switchDrawer();
        } else {
            setToolbarTitle(getResources().getString(R.string.history));
            navDrawer.setCheckedItem(R.id.menu_history);
            loadFragment(new HistoryFragment());
        }
    }

    private void openDashboard() {
        if (getCurrentFragment() instanceof HomeFragment) {
            switchDrawer();
        } else {
            setToolbarTitle(getResources().getString(R.string.app_name));
            navDrawer.setCheckedItem(R.id.menu_dashboard);
            loadFragment(new HomeFragment());
        }
    }
}
