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

/**
 * Класс представляет собой диалоговое меню выбора типа занятия.
 *
 * @author Ruslan Satarov
 * @version 1.0
 */
public class LessonTypeDialog extends AppCompatActivity implements View.OnClickListener {

    /**
     * Массив, содержащий View с типами занятия.
     */
    private View[] lessonTypeViewArray;

    /**
     * Переменная, хранящая id выбранного типа занятия.
     */
    private int idSelected;

    /**
     * Вызывается при создании Activity.
     *
     * @param savedInstanceState Если Activity было заново инициализировано после того, как
     *                           было закрыто, тогда этот Bundle содержит, которые он получил
     *                           в onSaveInstanceState. В другом случае это null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson_type_dialog);

        LinearLayout lessonTypeList = findViewById(R.id.lessonTypeList);
        Button submitButton = findViewById(R.id.lessonTypeSubmitButton);

        submitButton.setOnClickListener(this);

        String[] lessonTypesArray = getResources().getStringArray(R.array.lesson_types);

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

    /**
     * Вызывается, когда View было нажато.
     *
     * @param v View, которое было нажато.
     */
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
