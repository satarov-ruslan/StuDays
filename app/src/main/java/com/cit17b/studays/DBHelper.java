package com.cit17b.studays;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Класс используется для работы с базой данных.
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * Тег логирования.
     */
    private String logTag;

    /**
     * Контекст для работы с путями к базе данных.
     */
    private Context context;

    /**
     * Версия базы данных.
     */
    private static final int DATABASE_VERSION = 4;

    public DBHelper(Context context) {
        super(context, context.getString(R.string.db_name), null, DATABASE_VERSION);
        this.context = context;
        logTag = context.getString(R.string.db_log_tag);
    }

    /**
     * Вызывается, когда база данных создается в первый раз.
     *
     * @param db База данных.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(logTag, "onCreate database");
        db.execSQL(context.getString(R.string.create_table_lessons_sql));
        db.execSQL(context.getString(R.string.create_table_notes_sql));
        db.execSQL(context.getString(R.string.create_table_notifications_sql));
    }

    /**
     * Вызывается, когда база данных открывается.
     *
     * @param db База данных.
     */
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    /**
     * Вызывается, когда версия базы данных изменяется в большую сторону.
     *
     * @param db База данных.
     * @param oldVersion Старая версия.
     * @param newVersion Новая версия.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            db.execSQL(context.getString(R.string.create_table_notifications_sql));
        }
    }
}
