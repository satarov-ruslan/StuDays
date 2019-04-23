package com.cit17b.studays;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

public class NoteListActivity extends AppCompatActivity implements View.OnClickListener {

    NoteList noteList;
    FloatingActionButton createNoteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        noteList = (NoteList) getSupportFragmentManager().findFragmentById(R.id.noteList);
        createNoteButton = findViewById(R.id.createNoteButton);
        createNoteButton.setOnClickListener(this);
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
                DBHelper dbHelper = new DBHelper(this);
                SQLiteDatabase database = dbHelper.getWritableDatabase();
                database.delete(getString(R.string.table_notes_name), null, null);
                database.close();
                dbHelper.close();
                noteList.onUpdate();
        }
        return super.onOptionsItemSelected(item);
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
        if (resultCode == RESULT_OK) {
            noteList.onUpdate();
        }
    }
}
