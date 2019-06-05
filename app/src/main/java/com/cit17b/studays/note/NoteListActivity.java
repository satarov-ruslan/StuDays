package com.cit17b.studays.note;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.AlertDialog;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note_list, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.deleteAllNotesButton:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.question_delete_all);
                builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAllNotifications();
                        SQLiteDatabase database = dbHelper.getWritableDatabase();
                        database.delete(getString(R.string.table_notes_name), null, null);
                        database.close();
                        dbHelper.close();
                        updateList();
                    }
                });
                builder.setNegativeButton(R.string.cancel, null);
                builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotifications() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        Cursor notificationCursor = database.query(getString(R.string.table_notifications_name), null, null, null, null, null, null);
        if (notificationCursor != null && notificationCursor.moveToFirst()) {
            do {
                int noteId = notificationCursor.getInt(notificationCursor.getColumnIndex("noteId"));
                long notificationTimestamp = notificationCursor.getLong(notificationCursor.getColumnIndex("timestamp"));
                Cursor noteCursor = database.query(getString(R.string.table_notes_name), null, "id = ?", new String[]{String.valueOf(noteId)}, null, null, null);
                if (noteCursor != null && noteCursor.moveToFirst()) {
                    String title = noteCursor.getString(noteCursor.getColumnIndex("title"));
                    String text = noteCursor.getString(noteCursor.getColumnIndex("noteText"));
                    noteCursor.close();

                    Intent intent = new Intent(this, CreateNoteActivity.AlarmReceiver.class);
                    intent.putExtra("id", noteId);
                    intent.putExtra("timestamp", notificationTimestamp);
                    intent.putExtra("title", title);
                    intent.putExtra("text", text);

                    PendingIntent alarmIntent = PendingIntent.getBroadcast(this, noteId, intent, 0);
                    ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(alarmIntent);
                }
            } while (notificationCursor.moveToNext());
            notificationCursor.close();
        }

        database.delete(getString(R.string.table_notifications_name), null, null);

        database.close();
        dbHelper.close();
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK || requestCode == RESULT_CANCELED) {
            updateList();
        }
    }

    /**
     * Возвращает настроенный для заметок список.
     *
     * @return Настроенный объект SwipeMenuListView.
     */
    private SwipeMenuListView getAdjustedSwipeMenuListView() {
        final SwipeMenuListView listView = findViewById(R.id.noteSwipeListView);

        ArrayAdapter<Note> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);

        final SwipeMenuCreator creator = new SwipeMenuCreator() {
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
                final int pos = position;
                if (position != -1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NoteListActivity.this);
                    builder.setTitle(R.string.question_delete_element);
                    builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            SQLiteDatabase database = dbHelper.getWritableDatabase();
                            int noteId = dataList.get(pos).getId();
                            String[] whereArgs = new String[]{String.valueOf(noteId)};

                            Cursor cursor = database.query(getString(R.string.table_notes_name), null, "id = ?", whereArgs, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                String title = cursor.getString(cursor.getColumnIndex("title"));
                                String text = cursor.getString(cursor.getColumnIndex("noteText"));
                                cursor.close();

                                cursor = database.query(getString(R.string.table_notifications_name), null, "noteId = ?", whereArgs, null, null, null);
                                if (cursor != null && cursor.moveToFirst()) {
                                    long notificationTimestamp = cursor.getLong(cursor.getColumnIndex("timestamp"));
                                    cursor.close();

                                    Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.AlarmReceiver.class);
                                    intent.putExtra("id", noteId);
                                    intent.putExtra("timestamp", notificationTimestamp);
                                    intent.putExtra("title", title);
                                    intent.putExtra("text", text);

                                    PendingIntent alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), noteId, intent, 0);
                                    ((AlarmManager) getSystemService(Context.ALARM_SERVICE)).cancel(alarmIntent);
                                }
                            }

                            database.delete(getString(R.string.table_notes_name), "id = ?", whereArgs);
                            database.delete(getString(R.string.table_notifications_name), "noteId = ?", whereArgs);
                            database.close();
                            dbHelper.close();

                            updateList();
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            listView.smoothCloseMenu();
                        }
                    });
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            listView.smoothCloseMenu();
                        }
                    });
                    builder.create().show();
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
        if (cursor != null && cursor.moveToFirst()) {
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
            cursor.close();
        }
        database.close();
        dbHelper.close();
    }
}
