package com.cit17b.studays;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.View;

import java.util.List;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    static final String LOG_TAG = "myLogs";
    static final int PAGE_COUNT = 2;

    ViewPager timetablePager;
    FragmentPagerAdapter pagerAdapter;
    PagerTabStrip timetablePagerTabStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timetablePager = findViewById(R.id.timetablePager);
        pagerAdapter = new TimetableFragmentPagerAdapter(getSupportFragmentManager());
        timetablePager.setAdapter(pagerAdapter);
        timetablePagerTabStrip = timetablePager.findViewById(R.id.timetablePagerTabStrip);

        timetablePagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);

        FloatingActionButton createLessonButton = findViewById(R.id.createLessonButton);
        createLessonButton.setOnClickListener(this);


        /*
        timetablePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createLessonButton:
                Intent intent = new Intent(this, CreateLessonActivity.class);
                intent.putExtra("requestCode", CreateLessonActivity.REQUEST_CODE_CREATE_LESSON);
                startActivityForResult(intent, CreateLessonActivity.REQUEST_CODE_CREATE_LESSON);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CreateLessonActivity.REQUEST_CODE_CREATE_LESSON:
                    List<Fragment> fragments = getSupportFragmentManager().getFragments();
                    for (Fragment fragment : fragments) {
                        getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                    }
                    break;
            }
        }
    }

    private class TimetableFragmentPagerAdapter extends FragmentPagerAdapter {

        String[] titles = getResources().getStringArray(R.array.odd_even_week);

        public TimetableFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (position <= titles.length) {
                return titles[position];
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            return TimetablePageFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }
}
