package com.cit17b.studays;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    final String LOG_TAG = "myLogs";

    public static final int REQUEST_CODE_CREATE_LESSON = 1001;
    public static final int REQUEST_CODE_EDIT_LESSON = 1002;

    public static final int CONTEXT_MENU_EDIT = 10001;
    public static final int CONTEXT_MENU_DELETE = 10002;

    ArrayList<Lesson> lessons;

    LinearLayout lessonList;

    LinearLayout[] daysOfTheWeekLayouts;

    String[] daysOfTheWeekLabels;

    TextView oddWeekTab;
    TextView evenWeekTab;

    int tabSelected;
    int lessonIdSelected;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        lessons = new ArrayList<>();
        daysOfTheWeekLabels = getResources().getStringArray(R.array.days_of_the_week);

        lessonList = findViewById(R.id.lessonList);

        oddWeekTab = findViewById(R.id.oddWeekTab);
        oddWeekTab.setOnClickListener(this);

        evenWeekTab = findViewById(R.id.evenWeekTab);
        evenWeekTab.setOnClickListener(this);

        oddWeekTab.setBackgroundResource(R.color.colorPrimary);
        tabSelected = 1;

        fillDataArrayFromDB();
        fillLessonList();

        FloatingActionButton createLessonButton = findViewById(R.id.createLessonButton);
        createLessonButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createLessonButton:
                Intent intent = new Intent(this, CreateLessonActivity.class);
                intent.putExtra("requestCode", REQUEST_CODE_CREATE_LESSON);
                startActivityForResult(intent, REQUEST_CODE_CREATE_LESSON);
                break;
            case R.id.oddWeekTab:
                if (tabSelected == 2) {
                    oddWeekTab.setBackgroundResource(R.color.colorPrimary);
                    evenWeekTab.setBackgroundColor(Color.WHITE);
                    tabSelected = 1;
                    fillLessonList();
                }
                break;
            case R.id.evenWeekTab:
                if (tabSelected == 1) {
                    oddWeekTab.setBackgroundColor(Color.WHITE);
                    evenWeekTab.setBackgroundResource(R.color.colorPrimary);
                    tabSelected = 2;
                    fillLessonList();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            SQLiteDatabase database = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            Lesson lesson;
            switch (requestCode) {
                case REQUEST_CODE_CREATE_LESSON:
                    if (data.hasExtra("lesson")) {
                        lesson = ((Lesson) data.getSerializableExtra("lesson"));

                        values.put("id", lesson.getId());
                        values.put("name", lesson.getName());
                        values.put("lectureHall", lesson.getLectureHall());
                        values.put("hourBeginning", lesson.getHourBeginning());
                        values.put("minuteBeginning", lesson.getMinuteBeginning());
                        values.put("hourEnding", lesson.getHourEnding());
                        values.put("minuteEnding", lesson.getMinuteEnding());
                        values.put("lecturer", lesson.getLecturer());
                        values.put("lessonType", lesson.getLessonType());
                        values.put("dayOfTheWeek", lesson.getDayOfTheWeek());
                        values.put("oddEvenWeek", lesson.getOddEvenWeek());
                        long rowID = database.insert(getString(R.string.table_lessons_name), null, values);
                        Log.d(getString(R.string.db_log_tag), "row inserted, ID = " + rowID);

                        lessons.add(lesson);
                        //fillDataArrayFromDB();
                    }
                    fillLessonList();
                    break;
                case REQUEST_CODE_EDIT_LESSON:
                    if (data.hasExtra("lesson")) {
                        lesson = (Lesson) data.getSerializableExtra("lesson");

                        values.put("name", lesson.getName());
                        values.put("lectureHall", lesson.getLectureHall());
                        values.put("hourBeginning", lesson.getHourBeginning());
                        values.put("minuteBeginning", lesson.getMinuteBeginning());
                        values.put("hourEnding", lesson.getHourEnding());
                        values.put("minuteEnding", lesson.getMinuteEnding());
                        values.put("lecturer", lesson.getLecturer());
                        values.put("lessonType", lesson.getLessonType());
                        values.put("dayOfTheWeek", lesson.getDayOfTheWeek());
                        values.put("oddEvenWeek", lesson.getOddEvenWeek());
                        long rowID = database.update(getString(R.string.table_lessons_name), values, "id = ?", new String[] {String.valueOf(lesson.getId())});
                        Log.d(getString(R.string.db_log_tag), "row updated, ID = " + rowID);

                        for (int i = 0; i < lessons.size(); i++) {
                            if (lessons.get(i).getId() == lessonIdSelected) {
                                lessons.set(i, lesson);
                                break;
                            }
                        }
                        ////fillDataArrayFromDB();
                    }
                    fillLessonList();
                    break;
            }
            database.close();
            dbHelper.close();
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CONTEXT_MENU_EDIT, 0, getString(R.string.edit));
        menu.add(0, CONTEXT_MENU_DELETE, 0, getString(R.string.delete));
        lessonIdSelected = v.getId();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONTEXT_MENU_EDIT:
                Lesson lesson = null;
                for (Lesson lessonIter : lessons) {
                    if (lessonIter.getId() == lessonIdSelected) {
                        lesson = lessonIter;
                        break;
                    }
                }

                Intent intent = new Intent(this, CreateLessonActivity.class);
                intent.putExtra("requestCode", REQUEST_CODE_EDIT_LESSON);
                intent.putExtra("lesson", lesson);

                startActivityForResult(intent, REQUEST_CODE_EDIT_LESSON);
                break;
            case CONTEXT_MENU_DELETE:
                for (Iterator<Lesson> iterator = lessons.iterator(); iterator.hasNext(); ) {
                    lesson = iterator.next();
                    if (lesson.getId() == lessonIdSelected) {
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        database.delete(getString(R.string.table_lessons_name), "id = ?", new String[]{String.valueOf(lesson.getId())});
                        database.close();
                        dbHelper.close();
                        iterator.remove();
                        fillLessonList();
                        break;
                    }
                }
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void fillLessonList() {
        lessonList.removeAllViews();

        daysOfTheWeekLayouts = new LinearLayout[7];

        for (int i = 0; i < daysOfTheWeekLayouts.length; i++) {
            ArrayList<Lesson> filteredByDayOfWeek = new ArrayList<>();

            for (Lesson lesson : lessons) {
                if (lesson.getOddEvenWeek() == tabSelected || lesson.getOddEvenWeek() == Lesson.BOTH_WEEKS) {
                    if (lesson.getDayOfTheWeek() == i) {
                        filteredByDayOfWeek.add(lesson);
                    }
                }
            }

            if (filteredByDayOfWeek.size() != 0) {
                TextView dayLabel = (TextView) getLayoutInflater().inflate(R.layout.day_label_item, null);
                dayLabel.setText(daysOfTheWeekLabels[i]);
                lessonList.addView(dayLabel);

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
                    lessonItem.setId(lesson.getId());
                    fillLessonItem(lessonItem, lesson);
                    registerForContextMenu(lessonItem);
                    daysOfTheWeekLayouts[i].addView(lessonItem);
                }

                lessonList.addView(daysOfTheWeekLayouts[i]);
            }
        }
    }

    private void fillDataArrayFromDB() {
        lessons.clear();

        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(getString(R.string.table_lessons_name), null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
                       int idColIndex = cursor.getColumnIndex("id");
            int nameColIndex = cursor.getColumnIndex("name");
            int lectureHallColIndex = cursor.getColumnIndex("lectureHall");
            int hourBeginningColIndex = cursor.getColumnIndex("hourBeginning");
            int minuteBeginningColIndex = cursor.getColumnIndex("minuteBeginning");
            int hourEndingColIndex = cursor.getColumnIndex("hourEnding");
            int minuteEndingColIndex = cursor.getColumnIndex("minuteEnding");
            int lecturerColIndex = cursor.getColumnIndex("lecturer");
            int lessonTypeColIndex = cursor.getColumnIndex("lessonType");
            int dayOfTheWeekColIndex = cursor.getColumnIndex("dayOfTheWeek");
            int oddEvenWeekColIndex = cursor.getColumnIndex("oddEvenWeek");

            do {
                Lesson lesson = new Lesson();

                lesson.setId(cursor.getInt(idColIndex));
                lesson.setName(cursor.getString(nameColIndex));
                lesson.setLectureHall(cursor.getString(lectureHallColIndex));
                lesson.setHourBeginning(cursor.getInt(hourBeginningColIndex));
                lesson.setMinuteBeginning(cursor.getInt(minuteBeginningColIndex));
                lesson.setHourEnding(cursor.getInt(hourEndingColIndex));
                lesson.setMinuteEnding(cursor.getInt(minuteEndingColIndex));
                lesson.setLecturer(cursor.getString(lecturerColIndex));
                lesson.setLessonType(cursor.getString(lessonTypeColIndex));
                lesson.setDayOfTheWeek(cursor.getInt(dayOfTheWeekColIndex));
                lesson.setOddEvenWeek(cursor.getInt(oddEvenWeekColIndex));

                lessons.add(lesson);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        dbHelper.close();
        //for (int i = 1; i <= 10; i++) {
        //    lessons.add(new Lesson(
        //            0,
        //            "Lesson" + i,
        //            "Hall" + i,
        //            (int)(Math.random() * 5 + 8),
        //            (int) (Math.random() * 60),
        //            (int)(Math.random() * 5 + 13),
        //            (int) (Math.random() * 60),
        //            "Lecturer" + i,
        //            "LessonType" + i,
        //            (int) (Math.random() * 7),
        //            (int) (Math.random() * 3)));
        //}
    }

    private void fillLessonItem(View lessonItem, Lesson lesson) {
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
