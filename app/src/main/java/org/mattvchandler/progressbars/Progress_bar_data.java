package org.mattvchandler.progressbars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.Calendar;

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

// struct w/ copy of all DB columns. Serializable so we can store the whole thing
public class Progress_bar_data implements Serializable
{
    public long rowid; // is -1 when not set, ie. the data doesn't exist in the DB

    public long order; // -1 until set
    public long start_time;
    public long end_time;

    public String start_tz;
    public String end_tz;

    public boolean repeats;
    public int repeat_count;
    public int repeat_unit;
    public int repeat_days_of_week;

    public String title;
    public String pre_text;
    public String start_text;
    public String countdown_text;
    public String complete_text;
    public String post_text;

    public int precision;

    public boolean show_progress;
    public boolean show_start;
    public boolean show_end;

    public boolean show_years;
    public boolean show_months;
    public boolean show_weeks;
    public boolean show_days;
    public boolean show_hours;
    public boolean show_minutes;
    public boolean show_seconds;

    public boolean terminate;
    public boolean notify_start;
    public boolean notify_end;

    // set all fields from DB cursor
    private void set_from_cursor(Cursor cursor)
    {
        rowid               = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_table._ID));
        order               = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_table.ORDER_COL));
        start_time          = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_table.START_TIME_COL));
        end_time            = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_table.END_TIME_COL));
        start_tz            = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.START_TZ_COL));
        end_tz              = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.END_TZ_COL));
        repeats             = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.REPEATS_COL)) > 0;
        repeat_count        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.REPEAT_COUNT_COL));
        repeat_unit         = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.REPEAT_UNIT_COL));
        repeat_days_of_week = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.REPEAT_DAYS_OF_WEEK_COL));
        title               = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.TITLE_COL));
        pre_text            = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.PRE_TEXT_COL));
        start_text          = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.START_TEXT_COL));
        countdown_text      = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.COUNTDOWN_TEXT_COL));
        complete_text       = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.COMPLETE_TEXT_COL));
        post_text           = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.POST_TEXT_COL));
        precision           = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.PRECISION_COL));
        show_progress       = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_PROGRESS_COL)) > 0;
        show_start          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_START_COL))    > 0;
        show_end            = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_END_COL))      > 0;
        show_years          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_YEARS_COL))    > 0;
        show_months         = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_MONTHS_COL))   > 0;
        show_weeks          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_WEEKS_COL))    > 0;
        show_days           = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_DAYS_COL))     > 0;
        show_hours          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_HOURS_COL))    > 0;
        show_minutes        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_MINUTES_COL))  > 0;
        show_seconds        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.SHOW_SECONDS_COL))  > 0;
        terminate           = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.TERMINATE_COL))     > 0;
        notify_start        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.NOTIFY_START_COL))  > 0;
        notify_end          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_table.NOTIFY_END_COL))    > 0;
    }

    // construct from a DB cursor
    Progress_bar_data(Cursor cursor)
    {
        set_from_cursor(cursor);
    }

    // default ctor
    Progress_bar_data(Context context)
    {
        Calendar start_time_cal = Calendar.getInstance();
        Calendar end_time_cal = (Calendar) start_time_cal.clone();
        end_time_cal.add(Calendar.MINUTE, 1);

        rowid               = -1;
        order               = -1;
        start_time          = start_time_cal.getTimeInMillis() / 1000L;
        end_time            = end_time_cal.getTimeInMillis() / 1000L;
        start_tz            = start_time_cal.getTimeZone().getID();
        end_tz              = end_time_cal.getTimeZone().getID();
        repeats             = false;
        repeat_count        = 1;
        repeat_unit         = Progress_bar_table.Unit.DAY.index;
        repeat_days_of_week = Progress_bar_table.Days_of_week.all_days_mask();
        title               = context.getResources().getString(R.string.default_title);
        pre_text            = context.getResources().getString(R.string.default_pre_text);
        start_text          = context.getResources().getString(R.string.default_start_text);
        countdown_text      = context.getResources().getString(R.string.default_countdown_text);
        complete_text       = context.getResources().getString(R.string.default_complete_text);
        post_text           = context.getResources().getString(R.string.default_post_text);
        precision           = 2;
        show_progress       = true;
        show_start          = true;
        show_end            = true;
        show_years          = true;
        show_months         = true;
        show_weeks          = true;
        show_days           = true;
        show_hours          = true;
        show_minutes        = true;
        show_seconds        = true;
        terminate           = true;
        notify_start        = true;
        notify_end          = true;
    }

    // get data from DB given rowid
    public Progress_bar_data(Context context, long rowid_in)
    {
        SQLiteDatabase db = new Progress_bar_DB(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Progress_bar_table.TABLE_NAME + " WHERE " + Progress_bar_table._ID + " = ?", new String[]{ String.valueOf(rowid_in)});
        cursor.moveToFirst();

        set_from_cursor(cursor);

        cursor.close();
        db.close();
    }

    // trivial copy ctor. Because Java apparently can't figure this out on its own
    public Progress_bar_data(Progress_bar_data b)
    {
        rowid               = b.rowid;
        order               = b.order;
        start_time          = b.start_time;
        end_time            = b.end_time;
        start_tz            = b.start_tz;
        end_tz              = b.end_tz;
        repeats             = b.repeats;
        repeat_count        = b.repeat_count;
        repeat_unit         = b.repeat_unit;
        repeat_days_of_week = b.repeat_days_of_week;
        title               = b.title;
        pre_text            = b.pre_text;
        start_text          = b.start_text;
        countdown_text      = b.countdown_text;
        complete_text       = b.complete_text;
        post_text           = b.post_text;
        precision           = b.precision;
        show_progress       = b.show_progress;
        show_start          = b.show_start;
        show_end            = b.show_end;
        show_years          = b.show_years;
        show_months         = b.show_months;
        show_weeks          = b.show_weeks;
        show_days           = b.show_days;
        show_hours          = b.show_hours;
        show_minutes        = b.show_minutes;
        show_seconds        = b.show_seconds;
        terminate           = b.terminate;
        notify_start        = b.notify_start;
        notify_end          = b.notify_end;
    }

    private ContentValues build_ContentValues()
    {
        ContentValues values = new ContentValues();

        values.put(Progress_bar_table.ORDER_COL, order);
        values.put(Progress_bar_table.START_TIME_COL, start_time);
        values.put(Progress_bar_table.END_TIME_COL, end_time);
        values.put(Progress_bar_table.START_TZ_COL, start_tz);
        values.put(Progress_bar_table.END_TZ_COL, end_tz);
        values.put(Progress_bar_table.REPEATS_COL, repeats);
        values.put(Progress_bar_table.REPEAT_COUNT_COL, repeat_count);
        values.put(Progress_bar_table.REPEAT_UNIT_COL, repeat_unit);
        values.put(Progress_bar_table.REPEAT_DAYS_OF_WEEK_COL, repeat_days_of_week);
        values.put(Progress_bar_table.TITLE_COL, title);
        values.put(Progress_bar_table.PRE_TEXT_COL, pre_text);
        values.put(Progress_bar_table.START_TEXT_COL, start_text);
        values.put(Progress_bar_table.COUNTDOWN_TEXT_COL, countdown_text);
        values.put(Progress_bar_table.COMPLETE_TEXT_COL, complete_text);
        values.put(Progress_bar_table.POST_TEXT_COL, post_text);
        values.put(Progress_bar_table.PRECISION_COL, precision);
        values.put(Progress_bar_table.SHOW_START_COL, show_start);
        values.put(Progress_bar_table.SHOW_END_COL, show_end);
        values.put(Progress_bar_table.SHOW_PROGRESS_COL, show_progress);
        values.put(Progress_bar_table.SHOW_YEARS_COL, show_years);
        values.put(Progress_bar_table.SHOW_MONTHS_COL, show_months);
        values.put(Progress_bar_table.SHOW_WEEKS_COL, show_weeks);
        values.put(Progress_bar_table.SHOW_DAYS_COL, show_days);
        values.put(Progress_bar_table.SHOW_HOURS_COL, show_hours);
        values.put(Progress_bar_table.SHOW_MINUTES_COL, show_minutes);
        values.put(Progress_bar_table.SHOW_SECONDS_COL, show_seconds);
        values.put(Progress_bar_table.TERMINATE_COL, terminate);
        values.put(Progress_bar_table.NOTIFY_START_COL, notify_start);
        values.put(Progress_bar_table.NOTIFY_END_COL, notify_end);

        return values;
    }
    // insert data into the DB. rowid must not be set
    // if order is not set, it will be placed at the bottom
    public void insert(Context context)
    {
        if(rowid >= 0)
            throw new IllegalStateException("Tried to insert when rowid already set");

        SQLiteDatabase db = new Progress_bar_DB(context).getWritableDatabase();

        if(order < 0)
        {
            // get next available order #
            Cursor cursor = db.rawQuery("SELECT MAX(" + Progress_bar_table.ORDER_COL + ") + 1 FROM " + Progress_bar_table.TABLE_NAME, null);
            cursor.moveToFirst();
            order = cursor.getLong(0);
            cursor.close();
        }
        // TODO: update repeat times

        rowid = db.insert(Progress_bar_table.TABLE_NAME, null, build_ContentValues());

        db.close();
        Notification_handler.reset_notification(context, this);
    }

    // update the DB with new data
    // rowid must be set
    public void update(Context context)
    {
        if(rowid < 0)
            throw new IllegalStateException("Tried to update when rowid isn't set");

        // TODO: update repeat times

        SQLiteDatabase db = new Progress_bar_DB(context).getWritableDatabase();

        db.update(Progress_bar_table.TABLE_NAME, build_ContentValues(), Progress_bar_table._ID + " = ?", new String[]{String.valueOf(rowid)});

        db.close();

        Notification_handler.reset_notification(context, this);
    }

    // delete from DB
    // rowid must be set, and will be unset after deletion
    public void delete(Context context)
    {
        if(rowid < 0)
            throw new IllegalStateException("Tried to delete when rowid isn't set");

        Notification_handler.cancel_notification(context, this);

        SQLiteDatabase db = new Progress_bar_DB(context).getWritableDatabase();

        db.delete(Progress_bar_table.TABLE_NAME,
                Progress_bar_table._ID + " = ?",
                new String[] {String.valueOf(rowid)});
        db.close();

        rowid = -1; // unset rowid
    }
}

