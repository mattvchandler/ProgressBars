/*
Copyright (C) 2019 Matthew Chandler

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

package org.mattvchandler.progressbars.util

import android.app.Application
import androidx.preference.PreferenceManager
import org.mattvchandler.progressbars.R

import org.mattvchandler.progressbars.db.DB
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.db.Progress_bars_table

class Resetting_application: Application()
{
    override fun onCreate() // runs on App startup
    {
        super.onCreate()

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)

        val first_run = prefs.getBoolean(resources.getString(R.string.pref_first_run_key), true)

        if(first_run)
        {
            val db = DB(this).writableDatabase
            val cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS_NO_WIDGET, null)
            if(cursor.count == 0)
            {
                Data(this).insert(db,0, null)
            }

            cursor.close()
            db.close()
        }

        // register notification handler
        Notification_handler.setup_notification_channel(this)
        // TODO: send a broadcast to the widgets to wake them up, if needed
    }
}
