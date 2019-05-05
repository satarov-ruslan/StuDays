package com.cit17b.studays.note;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.cit17b.studays.DBHelper;
import com.cit17b.studays.R;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DeleteNoteService extends IntentService {
    public static final String ACTION_DELETE_NOTE = "com.cit17b.studays.note.action.delete_note";

    public DeleteNoteService() {
        super("DeleteNoteService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_DELETE_NOTE.equals(action)) {
                final int id = intent.getIntExtra("id", 0);
                if (id != 0) {
                    DBHelper dbHelper = new DBHelper(getApplicationContext());
                    SQLiteDatabase database = dbHelper.getWritableDatabase();
                    database.delete(getString(R.string.table_notes_name), "id = ?", new String[]{String.valueOf(id)});
                    database.close();
                    dbHelper.close();

                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(id);
                }
            }
        }
    }
}
