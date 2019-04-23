package com.cit17b.studays.note;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.cit17b.studays.DBHelper;
import com.cit17b.studays.R;
import com.cit17b.studays.note.CreateNoteActivity;
import com.cit17b.studays.note.Note;

import java.util.ArrayList;

public class NoteList extends ListFragment {

    ArrayList<Note> dataList;

    ArrayAdapter<Note> adapter;

    DBHelper dbHelper;

    int noteIdSelected;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataList = new ArrayList<>();
        dbHelper = new DBHelper(getActivity());

        onUpdate();
        //fillDataListFromDB();
        //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        //setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        int noteIdSelected = dataList.get(position).getId();
        Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
        intent.putExtra("requestCode", CreateNoteActivity.REQUEST_CODE_EDIT_NOTE);
        intent.putExtra("id", noteIdSelected);
        startActivityForResult(intent, CreateNoteActivity.REQUEST_CODE_EDIT_NOTE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            onUpdate();
        }
    }

    public void onUpdate() {
        fillDataListFromDB();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        setListAdapter(adapter);
    }

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
