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

import java.util.ArrayList;

/**
 * Класс используется как список для отображения данных.
 *
 * @author Ruslan Satatov
 * @version 1.0
 */
public class NoteList extends ListFragment {

    /**
     * Список, содержащий заметки, что будут отображены.
     */
    private ArrayList<Note> dataList;

    /**
     * Объект, что адаптирует список для вывода на экран.
     */
    private ArrayAdapter<Note> adapter;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        dataList = new ArrayList<>();
        dbHelper = new DBHelper(getActivity());

        onUpdate();
    }

    /**
     * Вызывается когда нажат элемент из списка.
     *
     * @param l ListView, в котором произошло нажатие.
     * @param v View, которое было нажато в ListView.
     * @param position Позиция View в списке.
     * @param id ID строки, что была нажата.
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        int noteIdSelected = dataList.get(position).getId();
        Intent intent = new Intent(getActivity(), CreateNoteActivity.class);
        intent.putExtra("requestCode", CreateNoteActivity.REQUEST_CODE_EDIT_NOTE);
        intent.putExtra("id", noteIdSelected);
        startActivityForResult(intent, CreateNoteActivity.REQUEST_CODE_EDIT_NOTE);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            onUpdate();
        }
    }

    /**
     * Обновляет содержимое списка.
     */
    public void onUpdate() {
        fillDataListFromDB();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        setListAdapter(adapter);
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
