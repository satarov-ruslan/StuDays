package com.cit17b.studays;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;

public class TimetableActivity extends AppCompatActivity implements View.OnClickListener {

    final String LOG_TAG = "myLogs";

    ArrayList<Lesson> lessons;

    LinearLayout lessonList;

    LinearLayout[] daysOfTheWeekLayouts;

    String[] daysOfTheWeekLabels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lessons = new ArrayList<>();
        daysOfTheWeekLabels = getResources().getStringArray(R.array.days_of_the_week);

        lessonList = findViewById(R.id.lessonList);

        generateData();
        fillLessonList();

        Button refreshButton = findViewById(R.id.createLessonButton);
        refreshButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createLessonButton:
                generateData();
                fillLessonList();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    void fillLessonList() {
        lessonList.removeAllViews();

        daysOfTheWeekLayouts = new LinearLayout[7];

        int dpAsPixels = (int) (20 * getResources().getDisplayMetrics().density + 0.5f);
        LinearLayout.LayoutParams dayLabelLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dayLabelLayoutParams.setMargins(0, dpAsPixels, 0, 0);

        for (int i = 0; i < daysOfTheWeekLayouts.length; i++) {
            ArrayList<Lesson> filteredByDayOfWeek = new ArrayList<>();

            for (Lesson lesson : lessons) {
                if (lesson.getDayOfTheWeek() - 1 == i) {
                    filteredByDayOfWeek.add(lesson);
                }
            }

            if (filteredByDayOfWeek.size() != 0) {
                TextView dayLabel = new TextView(new ContextThemeWrapper(this, R.style.DayLabelTheme));
                dayLabel.setText(daysOfTheWeekLabels[i]);
                lessonList.addView(dayLabel, dayLabelLayoutParams);

                filteredByDayOfWeek.sort(new Comparator<Lesson>() {
                    @Override
                    public int compare(Lesson o1, Lesson o2) {
                        if (o1.getHourBeginning() == o2.getHourBeginning()) {
                            return o1.getMinuteBeginning() - o2.getMinuteBeginning();
                        }
                        return o1.getHourBeginning() - o2.getHourBeginning();
                    }
                });

                daysOfTheWeekLayouts[i] = new LinearLayout(this);
                daysOfTheWeekLayouts[i].setOrientation(LinearLayout.VERTICAL);

                for (int j = 0; j < filteredByDayOfWeek.size(); j++) {
                    Lesson lesson = filteredByDayOfWeek.get(j);
                    lesson.setListNum(j + 1);
                    View lessonItem = getLayoutInflater().inflate(R.layout.lesson_item, daysOfTheWeekLayouts[i], false);
                    fillLessonItem(lessonItem, lesson);
                    daysOfTheWeekLayouts[i].addView(lessonItem);
                }

                lessonList.addView(daysOfTheWeekLayouts[i]);
            }
        }
    }

    void generateData() {
        lessons.clear();
        for (int i = 1; i <= 10; i++) {
            lessons.add(new Lesson(
                    0,
                    "Lesson" + i,
                    "Hall" + i,
                    (int)(Math.random() * 5 + 8),
                    (int) (Math.random() * 60),
                    (int)(Math.random() * 5 + 13),
                    (int) (Math.random() * 60),
                    "Lecturer" + i,
                    "LessonType" + i,
                    (int) (Math.random() * 7 + 1),
                    (int) (Math.random() * 3)));
        }
    }

    void fillLessonItem(View lessonItem, Lesson lesson) {
        ((TextView) lessonItem.findViewById(R.id.tvLessonListNum)).setText(String.format(Locale.getDefault(), "%d", lesson.getListNum()));
        ((TextView) lessonItem.findViewById(R.id.tvLessonName)).setText(lesson.getName());
        ((TextView) lessonItem.findViewById(R.id.tvLessonLectureHall)).setText(lesson.getLectureHall());
        ((TextView) lessonItem.findViewById(R.id.tvLessonHourBeginning)).setText(String.format(Locale.getDefault(), "%02d", lesson.getHourBeginning()));
        ((TextView) lessonItem.findViewById(R.id.tvLessonMinuteBeginning)).setText(String.format(Locale.getDefault(), "%02d", lesson.getMinuteBeginning()));
        ((TextView) lessonItem.findViewById(R.id.tvLessonHourEnding)).setText(String.format(Locale.getDefault(), "%02d", lesson.getHourEnding()));
        ((TextView) lessonItem.findViewById(R.id.tvLessonMinuteEnding)).setText(String.format(Locale.getDefault(), "%02d", lesson.getMinuteEnding()));
        ((TextView) lessonItem.findViewById(R.id.tvLessonLecturer)).setText(lesson.getLecturer());
        ((TextView) lessonItem.findViewById(R.id.tvLessonType)).setText(lesson.getLessonType());
    }

}
