package com.cit17b.studays;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    static final String LOG_TAG = "myLogs";
    static final int PAGE_COUNT = 2;

    ViewPager timetablePager;
    FragmentPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timetablePager = findViewById(R.id.timetablePager);
        pagerAdapter = new TimetableFragmentPagerAdapter(getSupportFragmentManager());
        timetablePager.setAdapter(pagerAdapter);

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
                    for (int i = 0; i < pagerAdapter.getCount(); i++) {
                        pagerAdapter.getItem(i);
                        //TimetablePageFragment fragment = (TimetablePageFragment) pagerAdapter.getItem(i);
                        //fragment.fillDataArrayFromDB();
                        //fragment.fillLessonList();
                    }
                    break;
            }
        }
    }

    private class TimetableFragmentPagerAdapter extends FragmentPagerAdapter {

        public TimetableFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
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
