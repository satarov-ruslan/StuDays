package com.cit17b.studays;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Locale;

public class CreateLessonActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_LESSON_TYPE = 101;
    public static final int REQUEST_CODE_DAY_OF_THE_WEEK = 102;

    EditText name;
    EditText lecturer;
    EditText lectureHall;

    TextView hourBeginning;
    TextView minuteBeginning;

    TextView hourEnding;
    TextView minuteEnding;

    TextView lessonType;

    int oddEvenWeekNumber;
    TextView oddEvenWeek;
    int dayOfTheWeekNumber;
    TextView dayOfTheWeek;

    LinearLayout timeBeginningLayout;
    LinearLayout timeEndingLayout;
    LinearLayout dayOfTheWeekLayout;

    Button submitButton;

    TimePickerDialog.OnTimeSetListener timeBeginningListener;
    TimePickerDialog.OnTimeSetListener timeEndingListener;

    String[] daysOfTheWeekAbridgedArray;
    String[] oddEvenWeekArray;
    String[] lessonTypesArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_lesson);

        if (getIntent().getIntExtra("requestCode", 0) == MainActivity.REQUEST_CODE_EDIT_LESSON) {
            setTitle(R.string.edit);
        }

        daysOfTheWeekAbridgedArray = getResources().getStringArray(R.array.days_of_the_week_abridged);
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
        oddEvenWeek = findViewById(R.id.createLessonOddEvenWeekField);
        dayOfTheWeek = findViewById(R.id.createLessonDayOfTheWeekField);

        timeBeginningLayout = findViewById(R.id.createLessonBeginningLayout);
        timeEndingLayout = findViewById(R.id.createLessonEndingLayout);
        dayOfTheWeekLayout = findViewById(R.id.createLessonDayOfTheWeekLayout);

        submitButton = findViewById(R.id.createLessonSubmitButton);

        timeBeginningLayout.setOnClickListener(this);
        timeEndingLayout.setOnClickListener(this);
        lessonType.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        dayOfTheWeekLayout.setOnClickListener(this);

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
    public void onClick(View v) {
        Intent intent;
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
                intent = new Intent(this, LessonTypeDialog.class);
                startActivityForResult(intent, REQUEST_CODE_LESSON_TYPE);
                break;
            case R.id.createLessonDayOfTheWeekLayout:
                intent = new Intent(this, DayOfTheWeekDialog.class);
                startActivityForResult(intent, REQUEST_CODE_DAY_OF_THE_WEEK);
                break;
            case R.id.createLessonSubmitButton:
                if (checkResult()) {
                    intent = getIntent();

                    Lesson lesson = new Lesson(
                            0,
                            name.getText().toString(),
                            lectureHall.getText().toString(),
                            Integer.parseInt(hourBeginning.getText().toString()),
                            Integer.parseInt(minuteBeginning.getText().toString()),
                            Integer.parseInt(hourEnding.getText().toString()),
                            Integer.parseInt(minuteEnding.getText().toString()),
                            lecturer.getText().toString(),
                            lessonType.getText().toString(),
                            dayOfTheWeekNumber,
                            oddEvenWeekNumber
                    );
                    if (intent.getIntExtra("requestCode", 0) == MainActivity.REQUEST_CODE_EDIT_LESSON) {
                        lesson.setId(((Lesson) intent.getSerializableExtra("lesson")).getId());
                    }
                    intent.putExtra("lesson", lesson);

                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_LESSON_TYPE:
                    int lessonTypeIndex = data.getIntExtra("lessonType", 0);
                    lessonType.setText(lessonTypesArray[lessonTypeIndex]);
                    break;
                case REQUEST_CODE_DAY_OF_THE_WEEK:
                    if (data.hasExtra("dayOfTheWeek")) {
                        dayOfTheWeekNumber = data.getIntExtra("dayOfTheWeek", 0);
                        dayOfTheWeek.setText(daysOfTheWeekAbridgedArray[dayOfTheWeekNumber]);
                    }
                    if (data.hasExtra("oddEvenWeek")) {
                        oddEvenWeekNumber = data.getIntExtra("oddEvenWeek", 0);
                        oddEvenWeek.setText(oddEvenWeekArray[oddEvenWeekNumber]);
                    }
                    break;
            }
        }
    }

    private void fillFields() {
        Intent intent = getIntent();
        int requestCode = intent.getIntExtra("requestCode", 0);
        if (requestCode == MainActivity.REQUEST_CODE_EDIT_LESSON) {
            Lesson lesson = (Lesson) intent.getSerializableExtra("lesson");
            name.setText(lesson.getName());
            lecturer.setText(lesson.getLecturer());
            lectureHall.setText(lesson.getLectureHall());
            hourBeginning.setText(String.format(Locale.getDefault(), "%02d", lesson.getHourBeginning()));
            minuteBeginning.setText(String.format(Locale.getDefault(), "%02d", lesson.getMinuteBeginning()));
            hourEnding.setText(String.format(Locale.getDefault(), "%02d", lesson.getHourEnding()));
            minuteEnding.setText(String.format(Locale.getDefault(), "%02d", lesson.getMinuteEnding()));
            lessonType.setText(lesson.getLessonType());
            oddEvenWeekNumber = lesson.getOddEvenWeek();
            oddEvenWeek.setText(oddEvenWeekArray[oddEvenWeekNumber]);
            dayOfTheWeekNumber = lesson.getDayOfTheWeek();
            dayOfTheWeek.setText(daysOfTheWeekAbridgedArray[dayOfTheWeekNumber]);
        } else {
            lessonType.setText(lessonTypesArray[0]);
            oddEvenWeekNumber = 0;
            oddEvenWeek.setText(oddEvenWeekArray[0]);
            dayOfTheWeekNumber = 0;
            dayOfTheWeek.setText(daysOfTheWeekAbridgedArray[0]);
        }
    }

    private boolean checkResult() {
        return !(name.getText().toString().isEmpty()
                || lecturer.getText().toString().isEmpty()
                || lectureHall.getText().toString().isEmpty());
    }
}
