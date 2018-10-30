package org.mattvchandler.progressbars.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;

import org.mattvchandler.progressbars.R;
import org.mattvchandler.progressbars.util.Notification_handler;

import java.io.Serializable;
import java.util.Calendar;
import java.util.TimeZone;

import static org.mattvchandler.progressbars.util.Notification_handlerKt.cancel_alarm;
import static org.mattvchandler.progressbars.util.Notification_handlerKt.reset_alarm;

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

// struct w/ copy of all DB columns. Serializable so we can store the whole thing
public class Data implements Serializable
{
    public static final String DB_CHANGED_EVENT    = "Data.DB_CHANGED_EVENT";
    public static final String DB_CHANGED_TYPE     = "Data.DB_CHANGED_TYPE";
    public static final String DB_CHANGED_ROWID    = "Data.DB_CHANGED_ROWID";
    public static final String DB_CHANGED_FROM_POS = "Data.DB_CHANGED_FROM_POS";
    public static final String DB_CHANGED_TO_POS   = "Data.DB_CHANGED_TO_POS";

    public static final String INSERT = "insert";
    public static final String UPDATE = "update";
    public static final String DELETE = "delete";
    public static final String MOVE   = "move";


    public long rowid; // is -1 when not set, ie. the data doesn't exist in the DB

    @SuppressWarnings("WeakerAccess")
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
        rowid               = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table._ID));
        order               = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table.ORDER_COL));
        start_time          = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table.START_TIME_COL));
        end_time            = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table.END_TIME_COL));
        start_tz            = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.START_TZ_COL));
        end_tz              = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.END_TZ_COL));
        repeats             = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.REPEATS_COL)) > 0;
        repeat_count        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.REPEAT_COUNT_COL));
        repeat_unit         = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.REPEAT_UNIT_COL));
        repeat_days_of_week = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.REPEAT_DAYS_OF_WEEK_COL));
        title               = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.TITLE_COL));
        pre_text            = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.PRE_TEXT_COL));
        start_text          = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.START_TEXT_COL));
        countdown_text      = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.COUNTDOWN_TEXT_COL));
        complete_text       = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.COMPLETE_TEXT_COL));
        post_text           = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.POST_TEXT_COL));
        precision           = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.PRECISION_COL));
        show_progress       = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_PROGRESS_COL)) > 0;
        show_start          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_START_COL))    > 0;
        show_end            = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_END_COL))      > 0;
        show_years          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_YEARS_COL))    > 0;
        show_months         = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_MONTHS_COL))   > 0;
        show_weeks          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_WEEKS_COL))    > 0;
        show_days           = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_DAYS_COL))     > 0;
        show_hours          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_HOURS_COL))    > 0;
        show_minutes        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_MINUTES_COL))  > 0;
        show_seconds        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_SECONDS_COL))  > 0;
        terminate           = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.TERMINATE_COL))     > 0;
        notify_start        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.NOTIFY_START_COL))  > 0;
        notify_end          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.NOTIFY_END_COL))    > 0;
    }

    // construct from a DB cursor
    public Data(Cursor cursor)
    {
        set_from_cursor(cursor);
    }

    // default ctor
    public Data(Context context)
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
        repeat_unit         = Progress_bars_table.Unit.DAY.index;
        repeat_days_of_week = Progress_bars_table.Days_of_week.all_days_mask();
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
    public Data(Context context, long rowid_in)
    {
        SQLiteDatabase db = new DB(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Progress_bars_table.TABLE_NAME + " WHERE " + Progress_bars_table._ID + " = ?", new String[]{ String.valueOf(rowid_in)});
        cursor.moveToFirst();

        set_from_cursor(cursor);

        cursor.close();
        db.close();
    }

    // verbose ctor
    public Data(
            long order,
            long start_time,
            long end_time,
            String start_tz,
            String end_tz,
            boolean repeats,
            int repeat_count,
            int repeat_unit,
            int repeat_days_of_week,
            String title,
            String pre_text,
            String start_text,
            String countdown_text,
            String complete_text,
            String post_text,
            int precision,
            boolean show_progress,
            boolean show_start,
            boolean show_end,
            boolean show_years,
            boolean show_months,
            boolean show_weeks,
            boolean show_days,
            boolean show_hours,
            boolean show_minutes,
            boolean show_seconds,
            boolean terminate,
            boolean notify_start,
            boolean notify_end)
    {
        this.rowid = -1;
        this.order = order;
        this.start_time = start_time;
        this.end_time = end_time;
        this.start_tz = start_tz;
        this.end_tz = end_tz;
        this.repeats = repeats;
        this.repeat_count = repeat_count;
        this.repeat_unit = repeat_unit;
        this.repeat_days_of_week = repeat_days_of_week;
        this.title = title;
        this.pre_text = pre_text;
        this.start_text = start_text;
        this.countdown_text = countdown_text;
        this.complete_text = complete_text;
        this.post_text = post_text;
        this.precision = precision;
        this.show_progress = show_progress;
        this.show_start = show_start;
        this.show_end = show_end;
        this.show_years = show_years;
        this.show_months = show_months;
        this.show_weeks = show_weeks;
        this.show_days = show_days;
        this.show_hours = show_hours;
        this.show_minutes = show_minutes;
        this.show_seconds = show_seconds;
        this.terminate = terminate;
        this.notify_start = notify_start;
        this.notify_end = notify_end;
    }

    // trivial copy ctor. Because Java apparently can't figure this out on its own
    public Data(Data b)
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

        values.put(Progress_bars_table.ORDER_COL, order);
        values.put(Progress_bars_table.START_TIME_COL, start_time);
        values.put(Progress_bars_table.END_TIME_COL, end_time);
        values.put(Progress_bars_table.START_TZ_COL, start_tz);
        values.put(Progress_bars_table.END_TZ_COL, end_tz);
        values.put(Progress_bars_table.REPEATS_COL, repeats);
        values.put(Progress_bars_table.REPEAT_COUNT_COL, repeat_count);
        values.put(Progress_bars_table.REPEAT_UNIT_COL, repeat_unit);
        values.put(Progress_bars_table.REPEAT_DAYS_OF_WEEK_COL, repeat_days_of_week);
        values.put(Progress_bars_table.TITLE_COL, title);
        values.put(Progress_bars_table.PRE_TEXT_COL, pre_text);
        values.put(Progress_bars_table.START_TEXT_COL, start_text);
        values.put(Progress_bars_table.COUNTDOWN_TEXT_COL, countdown_text);
        values.put(Progress_bars_table.COMPLETE_TEXT_COL, complete_text);
        values.put(Progress_bars_table.POST_TEXT_COL, post_text);
        values.put(Progress_bars_table.PRECISION_COL, precision);
        values.put(Progress_bars_table.SHOW_START_COL, show_start);
        values.put(Progress_bars_table.SHOW_END_COL, show_end);
        values.put(Progress_bars_table.SHOW_PROGRESS_COL, show_progress);
        values.put(Progress_bars_table.SHOW_YEARS_COL, show_years);
        values.put(Progress_bars_table.SHOW_MONTHS_COL, show_months);
        values.put(Progress_bars_table.SHOW_WEEKS_COL, show_weeks);
        values.put(Progress_bars_table.SHOW_DAYS_COL, show_days);
        values.put(Progress_bars_table.SHOW_HOURS_COL, show_hours);
        values.put(Progress_bars_table.SHOW_MINUTES_COL, show_minutes);
        values.put(Progress_bars_table.SHOW_SECONDS_COL, show_seconds);
        values.put(Progress_bars_table.TERMINATE_COL, terminate);
        values.put(Progress_bars_table.NOTIFY_START_COL, notify_start);
        values.put(Progress_bars_table.NOTIFY_END_COL, notify_end);

        return values;
    }

    // insert data into the DB. rowid must not be set
    // if order is not set, it will be placed at the bottom
    public void insert(Context context)
    {
        insert(context, Undo.UNDO);
        Undo.delete_redo_history(context);
    }
    public void insert(Context context, String undo_redo)
    {
        apply_repeat();

        SQLiteDatabase db = new DB(context).getWritableDatabase();

        if(order < 0)
        {
            // get next available order #
            Cursor cursor = db.rawQuery("SELECT MAX(" + Progress_bars_table.ORDER_COL + ") + 1 FROM " + Progress_bars_table.TABLE_NAME, null);
            cursor.moveToFirst();
            order = cursor.getLong(0);
            cursor.close();
        }

        ContentValues values = build_ContentValues();
        if(rowid > 0)
            values.put(Progress_bars_table._ID, rowid);
        rowid = db.insert(Progress_bars_table.TABLE_NAME, null, values);

        ContentValues undo_columns = new ContentValues();
        undo_columns.put(Undo.ACTION_COL, INSERT);
        undo_columns.put(Undo.UNDO_REDO_COL, undo_redo);
        undo_columns.put(Undo.TABLE_ROWID_COL, rowid);
        db.insert(Undo.TABLE_NAME, null, undo_columns);

        db.close();

        reset_alarm(context, this);

        Intent intent = new Intent(DB_CHANGED_EVENT);
        intent.putExtra(DB_CHANGED_TYPE, INSERT);
        intent.putExtra(DB_CHANGED_ROWID, rowid);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // update the DB with new data
    // rowid must be set
    public void update(Context context)
    {
        update(context, Undo.UNDO);
        Undo.delete_redo_history(context);
    }
    public void update(Context context, String undo_redo)
    {
        if(rowid < 0)
            throw new IllegalStateException("Tried to update when rowid isn't set");

        apply_repeat();

        SQLiteDatabase db = new DB(context).getWritableDatabase();

        Data old_data = new Data(context, rowid);
        ContentValues undo_columns = old_data.build_ContentValues();
        undo_columns.put(Undo.ACTION_COL, UPDATE);
        undo_columns.put(Undo.UNDO_REDO_COL, undo_redo);
        undo_columns.put(Undo.TABLE_ROWID_COL, rowid);
        db.insert(Undo.TABLE_NAME, null, undo_columns);

        db.update(Progress_bars_table.TABLE_NAME, build_ContentValues(), Progress_bars_table._ID + " = ?", new String[]{String.valueOf(rowid)});
        db.close();

        reset_alarm(context, this);

        Intent intent = new Intent(DB_CHANGED_EVENT);
        intent.putExtra(DB_CHANGED_TYPE, UPDATE);
        intent.putExtra(DB_CHANGED_ROWID, rowid);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    // delete from DB
    // rowid must be set, and will be unset after deletion
    public void delete(Context context)
    {
        delete(context, Undo.UNDO);
        Undo.delete_redo_history(context);
    }
    public void delete(Context context, String undo_redo)
    {
        if(rowid < 0)
            throw new IllegalStateException("Tried to delete when rowid isn't set");

        cancel_alarm(context, this);

        SQLiteDatabase db = new DB(context).getWritableDatabase();

        ContentValues undo_columns = build_ContentValues();
        undo_columns.put(Undo.ACTION_COL, DELETE);
        undo_columns.put(Undo.UNDO_REDO_COL, undo_redo);
        undo_columns.put(Undo.TABLE_ROWID_COL, rowid);
        db.insert(Undo.TABLE_NAME, null, undo_columns);

        db.delete(Progress_bars_table.TABLE_NAME,
                Progress_bars_table._ID + " = ?",
                new String[] {String.valueOf(rowid)});
        db.close();

        Intent intent = new Intent(DB_CHANGED_EVENT);
        intent.putExtra(DB_CHANGED_TYPE, DELETE);
        intent.putExtra(DB_CHANGED_ROWID, rowid);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        rowid = -1; // unset rowid
    }

    private static long shift_row(int i, long to_order, Cursor cursor, SQLiteDatabase db)
    {
        cursor.moveToPosition(i);
        long from_order = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table.ORDER_COL));
        long i_rowid = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table._ID));

        ContentValues values = new ContentValues();
        values.put(Progress_bars_table.ORDER_COL, to_order);
        db.update(Progress_bars_table.TABLE_NAME, values, Progress_bars_table._ID + " = ?", new String[] {String.valueOf(i_rowid)});

        return from_order;
    }
    public void reorder(Context context, int from_pos, int to_pos)
    {
        reorder(context, from_pos, to_pos, Undo.UNDO);
        Undo.delete_redo_history(context);
    }
    public void reorder(Context context, int from_pos, int to_pos, String undo_redo)
    {
        if(from_pos == to_pos)
            return;
        SQLiteDatabase db = new DB(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null);

        long to_order = -1;

        if(from_pos < to_pos)
        {
            for(int i = from_pos; i <= to_pos; ++i)
                to_order = shift_row(i, to_order, cursor, db);

        }
        else if(from_pos > to_pos)
        {
            for(int i = from_pos + 1; i-- > to_pos; )
                to_order = shift_row(i, to_order, cursor, db);
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(Progress_bars_table.ORDER_COL, to_order);
        db.update(Progress_bars_table.TABLE_NAME, values, Progress_bars_table._ID + " = ?", new String[] {String.valueOf(rowid)});

        ContentValues undo_columns = new ContentValues();
        undo_columns.put(Undo.ACTION_COL, MOVE);
        undo_columns.put(Undo.UNDO_REDO_COL, undo_redo);
        undo_columns.put(Undo.TABLE_ROWID_COL, rowid);
        undo_columns.put(Undo.SWAP_FROM_POS_COL, from_pos);
        undo_columns.put(Undo.SWAP_TO_POS_COL, to_pos);
        db.insert(Undo.TABLE_NAME, null, undo_columns);
        db.close();

        Intent intent = new Intent(DB_CHANGED_EVENT);
        intent.putExtra(DB_CHANGED_TYPE, MOVE);
        intent.putExtra(DB_CHANGED_FROM_POS, from_pos);
        intent.putExtra(DB_CHANGED_TO_POS, to_pos);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

        order = to_order;
    }

    // if repeat is set, update start and end times as needed
    private void apply_repeat()
    {
        if(!repeats)
            return;

        long now_s = System.currentTimeMillis() / 1000L;

        while(now_s >= end_time)
        {
            // convert to calendar, add month/year, convert back
            Calendar start_cal = Calendar.getInstance(TimeZone.getTimeZone(start_tz));
            Calendar end_cal = Calendar.getInstance(TimeZone.getTimeZone(start_tz));

            start_cal.setTimeInMillis(start_time * 1000);
            end_cal.setTimeInMillis(end_time * 1000);

            if(repeat_unit == Progress_bars_table.Unit.SECOND.index)
            {
                start_cal.add(Calendar.SECOND, repeat_count);
                end_cal.add(Calendar.SECOND, repeat_count);
            }
            else if(repeat_unit == Progress_bars_table.Unit.MINUTE.index)
            {
                start_cal.add(Calendar.MINUTE, repeat_count);
                end_cal.add(Calendar.MINUTE, repeat_count);
            }
            else if(repeat_unit == Progress_bars_table.Unit.HOUR.index)
            {
                start_cal.add(Calendar.HOUR, repeat_count);
                end_cal.add(Calendar.HOUR, repeat_count);
            }
            else if(repeat_unit == Progress_bars_table.Unit.DAY.index)
            {
                start_cal.add(Calendar.DAY_OF_MONTH, repeat_count);
                end_cal.add(Calendar.DAY_OF_MONTH, repeat_count);
            }
            else if(repeat_unit == Progress_bars_table.Unit.WEEK.index)
            {
                if(repeat_days_of_week != 0)
                {
                    int day_of_week = start_cal.get(Calendar.DAY_OF_WEEK) - 1;
                    int increment_days = 0;
                    do
                    {
                        ++increment_days;
                        if(++day_of_week >= 7)
                        {
                            increment_days += 7 * (repeat_count - 1);
                            day_of_week = 0;
                        }
                    } while((repeat_days_of_week & (1 << day_of_week)) == 0);

                    start_cal.add(Calendar.DAY_OF_MONTH, increment_days);
                    end_cal.add(Calendar.DAY_OF_MONTH, increment_days);
                }
            }

            else if(repeat_unit == Progress_bars_table.Unit.MONTH.index)
            {
                start_cal.add(Calendar.MONTH, repeat_count);
                end_cal.add(Calendar.MONTH, repeat_count);
            }
            else if(repeat_unit == Progress_bars_table.Unit.YEAR.index)
            {
                start_cal.add(Calendar.YEAR, repeat_count);
                end_cal.add(Calendar.YEAR, repeat_count);
            }

            start_time = start_cal.getTimeInMillis() / 1000;
            end_time = end_cal.getTimeInMillis() / 1000;
        }
    }

    public static void apply_all_repeats(Context context)
    {
        SQLiteDatabase db = new DB(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null);

        // for every timer
        for(int i = 0; i < cursor.getCount(); ++i)
        {
            cursor.moveToPosition(i);
            Data data = new Data(cursor);

            data.apply_repeat();
            db.update(Progress_bars_table.TABLE_NAME, data.build_ContentValues(), Progress_bars_table._ID + " = ?", new String[]{String.valueOf(data.rowid)});
        }
        cursor.close();
        db.close();
    }
}
