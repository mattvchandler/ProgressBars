package org.mattvchandler.progressbars;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// DB container
public class Progress_bar_DB extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "progress_bar_db";

    public Progress_bar_DB(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // build the tables / whatever else when new
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(Progress_bar_table.CREATE_TABLE);
    }

    // if DB schema changes, put logic to migrate data here
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Progress_bar_table.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
