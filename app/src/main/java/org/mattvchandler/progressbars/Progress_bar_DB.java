package org.mattvchandler.progressbars;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Progress_bar_DB extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 1;
    public static final String DB_NAME = "progress_bar_db";

    public Progress_bar_DB(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL(Progress_bar_contract.Progress_bar_table.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Progress_bar_contract.Progress_bar_table.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}