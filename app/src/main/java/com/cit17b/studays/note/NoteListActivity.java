package com.cit17b.studays.note;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cit17b.studays.DBHelper;
import com.cit17b.studays.R;

import java.util.ArrayList;

/**
 * Класс представляет собой Activity, что отображает список всех заметок.
 *
 * @author Ruslan Satarov
 * @version 1.2
 */
public class NoteListActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Список, содержащий заметки, что будут отображены.
     */
    private ArrayList<Note> dataList;

    /**
     * Список, что отображает на єкране заметки.
     */
    private SwipeMenuListView listView;

    /**
     * Объект для работы с базой данных.
     */
    private DBHelper dbHelper;

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
        setContentView(R.layout.activity_swipe_note_list);

        FloatingActionButton createNoteButton = findViewById(R.id.createNoteButton);
        createNoteButton.setOnClickListener(this);

        dbHelper = new DBHelper(this);

        dataList = new ArrayList<>();
        listView = getAdjustedSwipeMenuListView();
        updateList();
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
     * @return Верните false, чтобы разрешить нормальную обработку меню,
     * true, чтобы использовать ее здесь.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.deleteAllNotesButton:
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.delete(getString(R.string.table_notes_name), null, null);
                database.close();
                dbHelper.close();
                updateList();
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
     * @param resultCode  Код, идентифицирующий результат работы дочернего Activity.
     * @param data        Intent, который может содержать результирующие данные.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            updateList();
        }
    }

    /**
     * Возвращает настроенный для заметок список.
     *
     * @return Настроенный объект SwipeMenuListView.
     */
    private SwipeMenuListView getAdjustedSwipeMenuListView() {
        SwipeMenuListView listView = findViewById(R.id.noteSwipeListView);

        ArrayAdapter<Note> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setBackground(R.color.colorPrimary);
                deleteItem.setWidth((int) (50 * getResources().getDisplayMetrics().density));
                deleteItem.setIcon(R.drawable.ic_delete_white_24dp);
                menu.addMenuItem(deleteItem);
            }
        };

        listView.setMenuCreator(creator);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int noteIdSelected = dataList.get(position).getId();
                Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
                intent.putExtra("requestCode", CreateNoteActivity.REQUEST_CODE_EDIT_NOTE);
                intent.putExtra("id", noteIdSelected);
                startActivityForResult(intent, CreateNoteActivity.REQUEST_CODE_EDIT_NOTE);
            }
        });

        listView.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                // doing nothing here
            }

            @Override
            public void onSwipeEnd(int position) {
                if (position != -1) {
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    database.delete(getString(R.string.table_notes_name), "id = ?", new String[]{String.valueOf(dataList.get(position).getId())});
                    database.close();
                    dbHelper.close();
                    updateList();
                }
            }
        });

        return listView;
    }

    /**
     * Обновляет содержимое списка.
     */
    private void updateList() {
        fillDataListFromDB();
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList));
    }

    /**
     * Заполняет список данными из базы данных.
     */
    private void fillDataListFromDB() {
        dataList.clear();
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(getString(R.string.table_notes_name), null, null, null, null, null, "id DESC");
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int titleColIndex = cursor.getColumnIndex("title");
            int noteTextColIndex = cursor.getColumnIndex("noteText");
            do {
                Note note = new Note();
                note.setId(cursor.getInt(idColIndex));
                note.setTitle(cursor.getString(titleColIndex));
                note.setNoteText(cursor.getString(noteTextColIndex));
                dataList.add(note);
            } while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        dbHelper.close();
    }
}
