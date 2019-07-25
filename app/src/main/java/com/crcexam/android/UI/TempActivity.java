package com.crcexam.android.UI;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.crcexam.android.R;
import com.crcexam.android.utils.LineMoveIndicator.DachshundTabLayout;
import com.crcexam.android.utils.LineMoveIndicator.LineMoveIndicator;

public class TempActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private DachshundTabLayout tabLayout;
    private static final String LOGIN_PAGES[] = {"Sign Up", "Sign In"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setAnimatedIndicator(new LineMoveIndicator(tabLayout));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.colorBlue));
    }
    public class PagerAdapter extends FragmentStatePagerAdapter {
        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return new PageFragment();
        }

        @Override
        public int getCount() {
            return LOGIN_PAGES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return LOGIN_PAGES[position];
        }
    }

    public static class PageFragment extends Fragment {

        public PageFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_page, container, false);
        }
    }
}
