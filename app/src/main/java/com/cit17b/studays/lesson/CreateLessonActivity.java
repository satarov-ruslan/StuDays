package com.cit17b.studays.lesson;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cit17b.studays.DBHelper;
import com.cit17b.studays.R;

import java.util.Locale;

/**
 * Этот класс представляет из себя Activity, что отвечает за создание и редактирование
 * занятия.
 *
 * @author Ruslan Satarov
 * @version 1.2.3
 */
public class CreateLessonActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Параметры используются при вызове данного Activity из других классов.
     * Параметры позволяют указать цель вызова Activity и на основе этого
     * задать начальное состояние полей Activity.
     */
    public static final int REQUEST_CODE_CREATE_LESSON = 1001;
    public static final int REQUEST_CODE_EDIT_LESSON = 1002;

    /**
     * Поле "Предмет".
     */
    private EditText name;

    /**
     * Поле "Преподаватель".
     */
    private EditText lecturer;

    /**
     * Поле "Аудитория".
     */
    private EditText lectureHall;

    /**
     * Поле часов начала занятия.
     */
    private TextView hourBeginning;

    /**
     * Поле минут начала занятия.
     */
    private TextView minuteBeginning;

    /**
     * Поле часов конца занятия.
     */
    private TextView hourEnding;

    /**
     * Поле минут конца занятия.
     */
    private TextView minuteEnding;

    /**
     * Поле "Тип занятия".
     */
    private TextView lessonType;

    /**
     * Числовое представление очередности повторений, когда проходит занятие.
     * 0 - по нечетным, 1 - по четным, 2 - каждую неделю.
     */
    private int oddEvenWeekNumber;

    /**
     * Поле очередности повторений.
     */
    private TextView oddEvenWeek;

    /**
     * Числовое представление дня недели.
     * 0 - Понедельник, ..., 6 - Воскресенье.
     */
    private int dayOfTheWeekNumber;

    /**
     * Поле дня недели.
     */
    private TextView dayOfTheWeek;

    /**
     * Listener для диалога выбора времени начала занятия.
     */
    private TimePickerDialog.OnTimeSetListener timeBeginningListener;

    /**
     * Listener для диалога выбора времени конца занятия.
     */
    private TimePickerDialog.OnTimeSetListener timeEndingListener;

    /**
     * Массив, хранящий дни недели в текстовом виде в сокращенном формате.
     */
    private String[] daysOfTheWeekArray;

    /**
     * Массив, хранящий череды недели в текстовом формате.
     */
    private String[] oddEvenWeekArray;

    /**
     * Массив, хранящий типы уроков в текстовом формате.
     */
    private String[] lessonTypesArray;

    /**
     * Объект для работы с базой данных.
     */
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        if (getIntent().getIntExtra("requestCode", 0) == REQUEST_CODE_EDIT_LESSON) {
            setTitle(R.string.edit);
        }

        dbHelper = new DBHelper(this);

        daysOfTheWeekArray = getResources().getStringArray(R.array.days_of_the_week);
        lessonTypesArray = getResources().getStringArray(R.array.lesson_types);
        oddEvenWeekArray = getResources().getStringArray(R.array.odd_even_week);

        name = findViewById(R.id.createLessonNameField);
        lecturer = findViewById(R.id.createLessonLecturerField);
        lectureHall = findViewById(R.id.createLessonLectureHallField);
        hourBeginning = findViewById(R.id.createLessonHourBeginning);
        minuteBeginning = findViewById(R.id.createLessonMinuteBeginning);
        hourEnding = findViewById(R.id.createLessonHourEnding);
        minuteEnding = findViewById(R.id.createLessonMinuteEnding);
        lessonType = findViewById(R.id.createLessonTypeField);
        dayOfTheWeek = findViewById(R.id.createLessonDayOfTheWeekField);
        oddEvenWeek = findViewById(R.id.createLessonOddEvenWeekField);

        LinearLayout timeBeginningLayout = findViewById(R.id.createLessonBeginningLayout);
        LinearLayout timeEndingLayout = findViewById(R.id.createLessonEndingLayout);

        timeBeginningLayout.setOnClickListener(this);
        timeEndingLayout.setOnClickListener(this);
        lessonType.setOnClickListener(this);
        dayOfTheWeek.setOnClickListener(this);
        oddEvenWeek.setOnClickListener(this);

        fillFields();

        timeBeginningListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourBeginning.setText(String.format(Locale.getDefault(), "%02d", hourOfDay));
                minuteBeginning.setText(String.format(Locale.getDefault(), "%02d", minute));
            }
        };

        timeEndingListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                hourEnding.setText(String.format(Locale.getDefault(), "%02d", hourOfDay));
                minuteEnding.setText(String.format(Locale.getDefault(), "%02d", minute));
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_lesson, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = getIntent();
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.createLessonSubmitButton:
                if (checkTime()) {
                    if (!name.getText().toString().trim().isEmpty()) {
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();

                        values.put("name", name.getText().toString());
                        values.put("lectureHall", lectureHall.getText().toString());
                        values.put("hourBeginning", Integer.parseInt(hourBeginning.getText().toString()));
                        values.put("minuteBeginning", Integer.parseInt(minuteBeginning.getText().toString()));
                        values.put("hourEnding", Integer.parseInt(hourEnding.getText().toString()));
                        values.put("minuteEnding", Integer.parseInt(minuteEnding.getText().toString()));
                        values.put("lecturer", lecturer.getText().toString());
                        values.put("lessonType", lessonType.getText().toString());
                        values.put("dayOfTheWeek", dayOfTheWeekNumber);
                        values.put("oddEvenWeek", oddEvenWeekNumber);

                        if (intent.getIntExtra("requestCode", 0) == REQUEST_CODE_EDIT_LESSON
                                && intent.hasExtra("id")) {
                            long rowID = database.update(
                                    getString(R.string.table_lessons_name),
                                    values,
                                    "id = ?",
                                    new String[]{String.valueOf(intent.getIntExtra("id", 0))});
                        } else {
                            long rowID = database.insert(getString(R.string.table_lessons_name), null, values);
                        }

                        database.close();
                        dbHelper.close();

                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(this, getString(R.string.please_fill_name_field), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.time_order_error_message), Toast.LENGTH_LONG).show();
                }
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = getIntent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder;
        switch (v.getId()) {
            case R.id.createLessonBeginningLayout:
                new TimePickerDialog(this,
                        R.style.TimePickerTheme,
                        timeBeginningListener,
                        Integer.parseInt(getString(R.string.lesson_beginning_hour_default)),
                        Integer.parseInt(getString(R.string.lesson_beginning_minute_default)),
                        true).show();
                break;
            case R.id.createLessonEndingLayout:
                new TimePickerDialog(this,
                        R.style.TimePickerTheme,
                        timeEndingListener,
                        Integer.parseInt(getString(R.string.lesson_ending_hour_default)),
                        Integer.parseInt(getString(R.string.lesson_ending_minute_default)),
                        true).show();
                break;
            case R.id.createLessonTypeField:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.lesson_type)
                        .setItems(R.array.lesson_types, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                lessonType.setText(lessonTypesArray[which]);
                            }
                        })
                        .show();
                break;
            case R.id.createLessonDayOfTheWeekField:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.day_of_the_week)
                        .setItems(R.array.days_of_the_week, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dayOfTheWeekNumber = which;
                                dayOfTheWeek.setText(daysOfTheWeekArray[which]);
                            }
                        })
                        .show();
                break;
            case R.id.createLessonOddEvenWeekField:
                builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.repeating)
                        .setItems(R.array.odd_even_week, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                oddEvenWeekNumber = which;
                                oddEvenWeek.setText(oddEvenWeekArray[which]);
                            }
                        })
                        .show();
                break;
        }
    }

    /**
     * Заполняет поля Activity начальными данными.
     * Если Activity вызывалось для создания занятия, то поля будут содержать значения по умолчанию.
     * Если Activity вызывалось для редактирования существующего занятия, то поля будут
     * содержать информацию об этом занятии.
     */
    private void fillFields() {
        Intent intent = getIntent();
        int requestCode = intent.getIntExtra("requestCode", 0);
        if (requestCode == REQUEST_CODE_EDIT_LESSON
                && intent.hasExtra("id")) {
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            String selection = "id = ?";
            String[] selectionArgs = new String[]{String.valueOf(intent.getIntExtra("id", 0))};
            Cursor cursor = database.query(getString(R.string.table_lessons_name), null, selection, selectionArgs, null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
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

                    name.setText(cursor.getString(nameColIndex));
                    lecturer.setText(cursor.getString(lecturerColIndex));
                    lectureHall.setText(cursor.getString(lectureHallColIndex));
                    hourBeginning.setText(String.format(Locale.getDefault(), "%02d", cursor.getInt(hourBeginningColIndex)));
                    minuteBeginning.setText(String.format(Locale.getDefault(), "%02d", cursor.getInt(minuteBeginningColIndex)));
                    hourEnding.setText(String.format(Locale.getDefault(), "%02d", cursor.getInt(hourEndingColIndex)));
                    minuteEnding.setText(String.format(Locale.getDefault(), "%02d", cursor.getInt(minuteEndingColIndex)));
                    lessonType.setText(cursor.getString(lessonTypeColIndex));
                    oddEvenWeekNumber = cursor.getInt(oddEvenWeekColIndex);
                    oddEvenWeek.setText(oddEvenWeekArray[oddEvenWeekNumber]);
                    dayOfTheWeekNumber = cursor.getInt(dayOfTheWeekColIndex);
                    dayOfTheWeek.setText(daysOfTheWeekArray[dayOfTheWeekNumber]);
                }

                cursor.close();
            }
            database.close();
            dbHelper.close();
        } else {
            lessonType.setText(lessonTypesArray[0]);
            oddEvenWeekNumber = 2;
            oddEvenWeek.setText(oddEvenWeekArray[2]);
            dayOfTheWeekNumber = 0;
            dayOfTheWeek.setText(daysOfTheWeekArray[0]);
        }
    }

    /**
     * Проверяет время начала и конца занятия на последовательность.
     *
     * @return true, если время начала занятия раньше времени конца занятия.
     */
    private boolean checkTime() {
        int hBegin = Integer.parseInt(hourBeginning.getText().toString());
        int hEnd = Integer.parseInt(hourEnding.getText().toString());

        if (hBegin == hEnd) {
            int mBegin = Integer.parseInt(minuteBeginning.getText().toString());
            int mEnd = Integer.parseInt(minuteEnding.getText().toString());
            return mBegin < mEnd;
        }

        return hBegin < hEnd;
    }
}
