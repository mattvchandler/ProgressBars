package org.mattvchandler.progressbars.util;

import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.mattvchandler.progressbars.db.DB;
import org.mattvchandler.progressbars.db.Data;
import org.mattvchandler.progressbars.db.Progress_bars_table;
import org.mattvchandler.progressbars.db.Undo;

/*
Copyright (C) 2018 Matthew Chandler

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

public class Resetting_application extends Application
{
    @Override
    public void onCreate() // runs on App startup
    {
        super.onCreate();

        // reset undo / redo table
        Undo.delete_undo_history(this);
        Undo.delete_redo_history(this);

        // insert a new row when no others exist
        SQLiteDatabase db = new DB(this).getReadableDatabase();
        Cursor cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null);
        if(cursor.getCount() == 0)
        {
            new Data(this).insert(this);
        }
        else
        {
            // clean up existing orders. make them sequential
            Progress_bars_table.cleanup_order(this);
        }
        cursor.close();
        db.close();

        // register notification handler
        Notification_handler.setup_notification_channel(this);
    }
}
