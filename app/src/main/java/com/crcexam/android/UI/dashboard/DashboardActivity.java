package com.crcexam.android.UI.dashboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import com.crcexam.android.R;
import com.crcexam.android.UI.fragments.HistoryFragment;
import com.crcexam.android.UI.fragments.HomeFragment;
import com.crcexam.android.UI.fragments.InfoFragment;
import com.crcexam.android.UI.fragments.StoreFragment;
import com.crcexam.android.helper.BottomNavigationBehavior;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static BottomNavigationView navigation;
    Context mContext;
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mContext = this;
        setActionBar();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        //  toolbar = getSupportActionBar();
        // toolbar.setTitle(mContext.getResources().getString(R.string.Home));
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
       /* CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) navigation.getLayoutParams();
        layoutParams.setBehavior(new BottomNavigationBehavior());*/
        loadFragment(new HomeFragment());
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_dashboard) {
            mToolbar.setTitle(mContext.getResources().getString(R.string.Home));
            loadFragment(new HomeFragment());
        } else if (id == R.id.menu_results) {

        } else if (id == R.id.menu_profile) {

        } else if (id == R.id.menu_history) {
            mToolbar.setTitle(mContext.getResources().getString(R.string.History));
            loadFragment(new HistoryFragment());
        } else if (id == R.id.menu_refer) {

        } else if (id == R.id.menu_info) {
            mToolbar.setTitle(mContext.getResources().getString(R.string.Info));
            loadFragment(new InfoFragment());
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mToolbar.setTitle(mContext.getResources().getString(R.string.Home));
                    loadFragment(new HomeFragment());
                    return true;
                case R.id.navigation_history:
                    mToolbar.setTitle(mContext.getResources().getString(R.string.History));
                    loadFragment(new HistoryFragment());
                    return true;
                case R.id.navigation_store:
                    mToolbar.setTitle(mContext.getResources().getString(R.string.Store));
                    loadFragment(new StoreFragment());
                    return true;
                case R.id.navigation_info:
                    mToolbar.setTitle(mContext.getResources().getString(R.string.Info));
                    loadFragment(new InfoFragment());
                    return true;
            }

            return false;
        }
    };


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            if (getCurrentFragment() instanceof HomeFragment) {
                super.onBackPressed();
            } else {
                navigation.setSelectedItemId(R.id.navigation_home);
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
}
