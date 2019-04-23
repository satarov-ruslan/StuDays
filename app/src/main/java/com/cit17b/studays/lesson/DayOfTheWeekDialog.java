package com.cit17b.studays.lesson;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

import com.cit17b.studays.R;

public class DayOfTheWeekDialog extends AppCompatActivity implements View.OnClickListener {

    LinearLayout dayOfTheWeekLayout;
    LinearLayout oddEvenWeekLayout;

    View[] dayOfTheWeekViewArray;
    View[] oddEvenWeekViewArray;

    Button submitButton;

    String[] oddEvenWeekArray;
    String[] dayOfTheWeekAbridgedArray;

    int dayOfTheWeekIdSelected;
    int oddEvenWeekIdSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day_of_the_week_dialog);

        oddEvenWeekArray = getResources().getStringArray(R.array.odd_even_week);
        dayOfTheWeekAbridgedArray = getResources().getStringArray(R.array.days_of_the_week_abridged);

        dayOfTheWeekLayout = findViewById(R.id.dayOfTheWeekLayout);
        oddEvenWeekLayout = findViewById(R.id.oddEvenWeekLayout);
        submitButton = findViewById(R.id.dayOfTheWeekSubmitButton);

        submitButton.setOnClickListener(this);

        dayOfTheWeekViewArray = new View[dayOfTheWeekAbridgedArray.length];
        for (int i = 0; i < dayOfTheWeekViewArray.length; i++) {
            if (i != 0) {
                dayOfTheWeekLayout.addView(new Space(this), new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            }
            dayOfTheWeekViewArray[i] = getLayoutInflater().inflate(R.layout.day_of_the_week_item, null);
            dayOfTheWeekViewArray[i].setId(1000 + i);((TextView) dayOfTheWeekViewArray[i].findViewById(R.id.tvDayOfTheWeek)).setText(dayOfTheWeekAbridgedArray[i]);
            dayOfTheWeekViewArray[i].setOnClickListener(this);
            dayOfTheWeekLayout.addView(dayOfTheWeekViewArray[i]);
        }

        oddEvenWeekViewArray = new View[oddEvenWeekArray.length];
        for (int i = 0; i < oddEvenWeekViewArray.length; i++) {
            oddEvenWeekViewArray[i] = getLayoutInflater().inflate(R.layout.list_dialog_item, null);
            oddEvenWeekViewArray[i].setId(2000 + i);
            ((TextView)oddEvenWeekViewArray[i].findViewById(R.id.listDialogItemField)).setText(oddEvenWeekArray[i]);
            oddEvenWeekViewArray[i].setOnClickListener(this);
            oddEvenWeekLayout.addView(oddEvenWeekViewArray[i]);
        }

        dayOfTheWeekIdSelected = -1;
        oddEvenWeekIdSelected = -1;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.dayOfTheWeekSubmitButton) {
            Intent intent = getIntent();
            setResult(RESULT_CANCELED, intent);
            if (dayOfTheWeekIdSelected != -1) {
                intent.putExtra("dayOfTheWeek", dayOfTheWeekIdSelected - 1000);
                setResult(RESULT_OK, intent);
            }
            if (oddEvenWeekIdSelected != -1) {
                intent.putExtra("oddEvenWeek", oddEvenWeekIdSelected - 2000);
                setResult(RESULT_OK, intent);
            }
            finish();
        } else if (id >= 1000 && id < 2000) {
            for (View view : dayOfTheWeekViewArray) {
                if (view.getId() == id) {
                    view.setBackgroundColor(Color.LTGRAY);
                    dayOfTheWeekIdSelected = id;
                } else {
                    view.setBackgroundColor(Color.WHITE);
                }
            }
        } else if (id >= 2000 && id < 3000) {
            for (View view : oddEvenWeekViewArray) {
                if (view.getId() == id) {
                    view.setBackgroundColor(Color.LTGRAY);
                    oddEvenWeekIdSelected = id;
                } else {
                    view.setBackgroundColor(Color.WHITE);
                }
            }
        }
    }
}
