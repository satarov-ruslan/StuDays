package com.cit17b.studays.lesson;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cit17b.studays.R;

public class LessonTypeDialog extends AppCompatActivity implements View.OnClickListener {

    String[] lessonTypesArray;

    Button submitButton;

    LinearLayout lessonTypeList;
    View[] lessonTypeViewArray;

    int idSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_type_dialog);

        lessonTypeList = findViewById(R.id.lessonTypeList);
        submitButton = findViewById(R.id.lessonTypeSubmitButton);

        submitButton.setOnClickListener(this);

        lessonTypesArray = getResources().getStringArray(R.array.lesson_types);

        lessonTypeViewArray = new View[lessonTypesArray.length];
        for (int i = 0; i < lessonTypeViewArray.length; i++) {
            lessonTypeViewArray[i] = getLayoutInflater().inflate(R.layout.list_dialog_item, null);
            lessonTypeViewArray[i].setId(i);
            ((TextView)lessonTypeViewArray[i].findViewById(R.id.listDialogItemField)).setText(lessonTypesArray[i]);
            lessonTypeViewArray[i].setOnClickListener(this);
            lessonTypeList.addView(lessonTypeViewArray[i]);
        }

        idSelected = -1;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.lessonTypeSubmitButton) {
            Intent intent = getIntent();
            if (idSelected != -1) {
                intent.putExtra("lessonType", idSelected);
                setResult(RESULT_OK, intent);
            } else {
                setResult(RESULT_CANCELED, intent);
            }
            finish();
        } else {
            for (View view : lessonTypeViewArray) {
                if (view.getId() == v.getId()) {
                    view.setBackgroundColor(Color.LTGRAY);
                    idSelected = v.getId();
                } else {
                    view.setBackgroundColor(Color.WHITE);
                }
            }
        }
    }
}
