package com.cit17b.studays.note;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.cit17b.studays.DBHelper;
import com.cit17b.studays.R;
import com.cit17b.studays.note.CreateNoteActivity;
import com.cit17b.studays.note.NoteList;

/**
 * Класс представляет собой Activity, что отображает список всех всех заметок.
 *
 * @author Ruslan Satarov
 * @version 1.1
 */
public class NoteListActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Список с заметками.
     */
    private NoteList noteList;

    /**
     * Кнопка вызова меню создания заметки.
     */
    private FloatingActionButton createNoteButton;

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
        setContentView(R.layout.activity_note_list);

        noteList = (NoteList) getSupportFragmentManager().findFragmentById(R.id.noteList);
        createNoteButton = findViewById(R.id.createNoteButton);
        createNoteButton.setOnClickListener(this);
    }

    /**
     * Вызывается при создании меню Activity.
     *
     * @param menu Меню, в котором будут располагаться заданные элементы.
     * @return Должен возвращаться true, чтоб меню отображалось.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Вызывается при выборе элемента меню.
     *
     * @param item Выбранный элемент меню.
     *
     * @return Верните false, чтобы разрешить нормальную обработку меню,
     *         true, чтобы использовать ее здесь.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.deleteAllNotesButton:
                DBHelper dbHelper = new DBHelper(this);
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.delete(getString(R.string.table_notes_name), null, null);
                database.close();
                dbHelper.close();
                noteList.onUpdate();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Вызывается, когда View было нажато.
     *
     * @param v View, которое было нажато.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createNoteButton:
                Intent intent = new Intent(this, CreateNoteActivity.class);
                intent.putExtra("requestCode", CreateNoteActivity.REQUEST_CODE_CREATE_NOTE);
                startActivityForResult(intent, CreateNoteActivity.REQUEST_CODE_CREATE_NOTE);
                break;
        }
    }

    /**
     * Вызывается, когда вызванное Activity завершает работу, давая requestCode, с которым оно
     * было вызвано, resultCode и, возможно, дополнительные данные.
     *
     * @param requestCode Код, с которым было вызвано Activity.
     * @param resultCode Код, идентифицирующий результат работы дочернего Activity.
     * @param data Intent, который может содержать результирующие данные.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            noteList.onUpdate();
        }
    }
}
