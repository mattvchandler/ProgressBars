package org.mattvchandler.progressbars;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
Copyright (C) 2017 Matthew Chandler

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

// DB container
public class Progress_bar_DB extends SQLiteOpenHelper
{
    private static final int DB_VERSION = 2;
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
    public void onUpgrade(SQLiteDatabase db, int old_version, int new_version)
    {
        if(new_version != DB_VERSION)
            throw new IllegalStateException("DB version mismatch");

        Progress_bar_table.upgrade(db, old_version);
    }
}
