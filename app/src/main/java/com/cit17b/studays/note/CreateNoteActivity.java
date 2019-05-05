package com.cit17b.studays.note;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.cit17b.studays.DBHelper;
import com.cit17b.studays.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Этот класс представляет из себя Activity, что отвечает за создание и редактирование
 * заметки.
 *
 * @author Ruslan Satarov
 * @version 1.1
 */
public class CreateNoteActivity extends AppCompatActivity implements View.OnClickListener, NotificationDialogFragment.NotificationDialogListener {

    /**
     * Параметры используются при вызове данного Activity из других классов.
     * Параметры позволяют указать цель вызова Activity и на основе этого
     * задать начальное состояние полей Activity.
     */
    public static final int REQUEST_CODE_CREATE_NOTE = 15001;
    public static final int REQUEST_CODE_EDIT_NOTE = 15002;

    /**
     * Поле названия заметки.
     */
    private EditText titleField;

    /**
     * Поле текста заметки.
     */
    private EditText noteTextField;

    private long notificationDateTime;

    /**
     * Кнопка удаления заметки.
     */
    private ImageButton deleteButton;

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
        setContentView(R.layout.activity_create_note);

        dbHelper = new DBHelper(this);
        noteTextField = findViewById(R.id.createNoteTextField);

    }

    /**
     * Вызывается при создании меню Activity.
     *
     * @param menu Меню, в котором будут располагаться заданные элементы.
     * @return Должен возвращаться true, чтоб меню отображалось.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.menu_create_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        titleField = getSupportActionBar().getCustomView().findViewById(R.id.createNoteTitleField);
        deleteButton = getSupportActionBar().getCustomView().findViewById(R.id.createNoteDeleteButton);
        deleteButton.setOnClickListener(this);

        ImageButton notificationButton = getSupportActionBar().getCustomView().findViewById(R.id.createNoteNotificationButton);
        notificationButton.setOnClickListener(this);

        fillFields();
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Вызывается, когда View было нажато.
     *
     * @param v View, которое было нажато.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createNoteDeleteButton:
                Intent intent = getIntent();
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                if (intent.getIntExtra("requestCode", 0) == REQUEST_CODE_EDIT_NOTE
                        && intent.hasExtra("id")) {
                    database.delete(getString(R.string.table_notes_name), "id = ?", new String[]{String.valueOf(intent.getIntExtra("id", 0))});
                    setResult(RESULT_OK, intent);
                } else {
                    setResult(RESULT_CANCELED, intent);
                }

                if (notificationDateTime != 0 || notificationDateTime > System.currentTimeMillis()) {
                    Cursor cursor = database.rawQuery("SELECT MAX(id) FROM " + getString(R.string.table_notes_name), null);
                    if (cursor.getColumnCount() != 0) {
                        cursor.moveToFirst();
                        int lastNoteId = cursor.getInt(0);
                        cursor.close();
                        ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(lastNoteId);
                    }
                }

                database.close();
                dbHelper.close();
                finish();
                break;
            case R.id.createNoteNotificationButton:
                NotificationDialogFragment.newInstance(getString(R.string.notification), notificationDateTime).show(getSupportFragmentManager(), "notificationDialog");
                break;
        }
    }

    /**
     * Вызывается при нажатии аппаратной кнопки "Назад".
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = getIntent();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (titleField.getText().toString().trim().isEmpty()
                && noteTextField.getText().toString().trim().isEmpty()) {
            setResult(RESULT_CANCELED, intent);
        } else {
            if (titleField.getText().toString().trim().isEmpty()) {
                values.put("title", new SimpleDateFormat("yyyy.MM.dd - HH:mm:ss", Locale.getDefault()).format(new Date()));
            } else {
                values.put("title", titleField.getText().toString());
            }
            values.put("noteText", noteTextField.getText().toString());
            if (intent.getIntExtra("requestCode", 0) == REQUEST_CODE_EDIT_NOTE
                    && intent.hasExtra("id")) {
                int id = intent.getIntExtra("id", 0);
                database.update(getString(R.string.table_notes_name),
                        values,
                        "id = ?",
                        new String[]{String.valueOf(id)});

                if (notificationDateTime != 0 || notificationDateTime > System.currentTimeMillis()) {
                    createNoteNotification(id);
                }
            } else {
                database.insert(getString(R.string.table_notes_name), null, values);
            }

            if (notificationDateTime != 0 || notificationDateTime > System.currentTimeMillis()) {
                Cursor cursor = database.rawQuery("SELECT MAX(id) FROM notes", null);
                cursor.moveToFirst();
                int lastNoteId = cursor.getInt(0);
                cursor.close();
                createNoteNotification(lastNoteId);
            } else {
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
            }

            setResult(RESULT_OK, intent);
        }

        database.close();
        dbHelper.close();
        finish();
    }

    private void createNoteNotification(int noteId) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("id", noteId);
        intent.putExtra("dateTime", notificationDateTime);
        intent.putExtra("title", titleField.getText().toString());
        intent.putExtra("text", noteTextField.getText().toString());
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, noteId, intent, 0);
        alarmManager.setExact(AlarmManager.RTC, notificationDateTime, alarmIntent);
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
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Если Activity было открыто для редактирования существующей заметки,
     * то метод заполняет поля соответствующими значениями.
     */
    private void fillFields() {
        Intent intent = getIntent();
        if (intent.getIntExtra("requestCode", 0) == REQUEST_CODE_EDIT_NOTE
                && intent.hasExtra("id")) {
            SQLiteDatabase database = dbHelper.getReadableDatabase();
            String selection = "id = ?";
            String[] selectionArgs = new String[]{String.valueOf(intent.getIntExtra("id", 0))};
            Cursor cursor = database.query(getString(R.string.table_notes_name), null, selection, selectionArgs, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    //int idColIndex = cursor.getColumnIndex("id");
                    int titleColIndex = cursor.getColumnIndex("title");
                    int noteTextColIndex = cursor.getColumnIndex("noteText");

                    titleField.setText(cursor.getString(titleColIndex));
                    noteTextField.setText(cursor.getString(noteTextColIndex));
                }
                cursor.close();
            }
            database.close();
            dbHelper.close();
        }
    }

    @Override
    public void onFinishNotificationDialog(long input) {
        notificationDateTime = input;
        //String str = new SimpleDateFormat("yyyy.MM.dd - hh:mm.ss", Locale.getDefault()).format(new Date(notificationDateTime));
        //Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    public static class AlarmReceiver extends BroadcastReceiver {
        public AlarmReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra("id", 0);
            long dateTime = intent.getLongExtra("dateTime", 0);
            String title = intent.getStringExtra("title");
            String text = intent.getStringExtra("text");
            Intent deleteIntent = new Intent(context, DeleteNoteService.class);
            deleteIntent.putExtra("id", id);
            deleteIntent.setAction(DeleteNoteService.ACTION_DELETE_NOTE);
            PendingIntent deletePendingIntent = PendingIntent.getService(context, id, deleteIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.notification_channel_id))
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(text))
                    .setWhen(dateTime)
                    .addAction(R.drawable.ic_delete_white_24dp, context.getString(R.string.delete), deletePendingIntent);
            ((NotificationManager) context.getSystemService(NOTIFICATION_SERVICE)).notify(id, builder.build());
        }
    }
}
