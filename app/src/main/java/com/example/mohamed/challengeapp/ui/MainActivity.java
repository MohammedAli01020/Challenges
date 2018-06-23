package com.example.mohamed.challengeapp.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.mohamed.challengeapp.R;
import com.example.mohamed.challengeapp.fragment.BelovedMotherFragment;
import com.example.mohamed.challengeapp.fragment.FitnessFragment;
import com.example.mohamed.challengeapp.fragment.HealthFragment;
import com.example.mohamed.challengeapp.fragment.HeavyTalksFragment;
import com.example.mohamed.challengeapp.fragment.MountainSun;
import com.example.mohamed.challengeapp.fragment.SportFragment;


public class MainActivity extends AppCompatActivity {

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    private TextView[] mDotesTextViews;
    private LinearLayout mDotesLayout;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            mDotesTextViews[position].setTextColor(getResources().getColor(R.color.fragmentSectionColor));
        }

        @Override
        public void onPageSelected(int position) {
            addDtoes(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDotesLayout = findViewById(R.id.linear_layout_dotes);
        setUpDotes();

        // Create the adapter that will3 return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new BelovedMotherFragment(),
                    new FitnessFragment(),
                    new HealthFragment(),
                    new HeavyTalksFragment(),
                    new MountainSun(),
                    new SportFragment()
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }


        };

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mPagerAdapter);

        mViewPager.addOnPageChangeListener(mPageChangeListener);
    }


    private void setUpDotes() {
        mDotesTextViews = new TextView[6];

        for (int i = 0; i < mDotesTextViews.length; i++) {
            mDotesTextViews[i] = new TextView(this);
            mDotesTextViews[i].setText(Html.fromHtml("&#8226;"));
            mDotesTextViews[i].setTextSize(35);
            mDotesTextViews[i].setTextColor(getResources().getColor(R.color.fragmentSectionColor));

            mDotesLayout.addView(mDotesTextViews[i]);
        }
    }


    private void addDtoes(int position) {
        if (mDotesTextViews.length > 0) {
            mDotesTextViews[position].setTextColor(getResources().getColor(R.color.fragmentSectionColorWight));
        }
    }


}
