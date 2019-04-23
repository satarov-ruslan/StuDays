package com.cit17b.studays;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private String logTag;

    private Context context;

    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, context.getString(R.string.db_name), null, DATABASE_VERSION);
        this.context = context;
        logTag = context.getString(R.string.db_log_tag);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(logTag, "onCreate database");
        db.execSQL(context.getString(R.string.create_table_lessons_sql));
        db.execSQL(context.getString(R.string.create_table_notes_sql));
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
