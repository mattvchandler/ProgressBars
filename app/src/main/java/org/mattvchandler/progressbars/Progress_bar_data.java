package org.mattvchandler.progressbars;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.Calendar;

public class Progress_bar_data implements Serializable
{
    public long rowid;

    public long order;
    public long start_time;
    public long end_time;

    public String start_tz;
    public String end_tz;

    public String title;
    public String pre_text;
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
    public boolean notify;

    Progress_bar_data(
            long rowid_in,
            long order_in,
            long start_time_in,
            long end_time_in,
            String start_tz_in,
            String end_tz_in,
            String title_in,
            String pre_text_in,
            String countdown_text_in,
            String complete_text_in,
            String post_text_in,
            Integer precision_in,
            boolean show_progress_in,
            boolean show_start_in,
            boolean show_end_in,
            boolean show_years_in,
            boolean show_months_in,
            boolean show_weeks_in,
            boolean show_days_in,
            boolean show_hours_in,
            boolean show_minutes_in,
            boolean show_seconds_in,
            boolean terminate_in,
            boolean notify_in)
    {
        rowid = rowid_in;
        order = order_in;
        start_time = start_time_in;
        end_time = end_time_in;
        start_tz = start_tz_in;
        end_tz = end_tz_in;
        title = title_in;
        pre_text = pre_text_in;
        countdown_text = countdown_text_in;
        complete_text = complete_text_in;
        post_text = post_text_in;
        precision = precision_in;
        show_progress = show_progress_in;
        show_start = show_start_in;
        show_end = show_end_in;
        show_years = show_years_in;
        show_months = show_months_in;
        show_weeks = show_weeks_in;
        show_days = show_days_in;
        show_hours = show_hours_in;
        show_minutes = show_minutes_in;
        show_seconds = show_seconds_in;
        terminate = terminate_in;
        notify = notify_in;
    }

    Progress_bar_data(Cursor cursor)
    {
        this(
            cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table._ID)),
            cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.ORDER_COL)),
            cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.START_TIME_COL)),
            cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.END_TIME_COL)),
            cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.START_TZ_COL)),
            cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.END_TZ_COL)),
            cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.TITLE_COL)),
            cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL)),
            cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL)),
            cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL)),
            cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL)),
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.PRECISION_COL)),
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL)) > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_START_COL))    > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_END_COL))      > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL))    > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL))   > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL))    > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL))     > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL))    > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL))  > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL))  > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.TERMINATE_COL))     > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.NOTIFY_COL))        > 0
        );
    }

    Progress_bar_data(Context context)
    {
        Calendar start_time_cal = Calendar.getInstance();
        Calendar end_time_cal = (Calendar) start_time_cal.clone();
        end_time_cal.add(Calendar.HOUR, 1);

        rowid          = -1;
        order          = -1;
        start_time     = start_time_cal.getTimeInMillis() / 1000L;
        end_time       = end_time_cal.getTimeInMillis() / 1000L;
        start_tz       = start_time_cal.getTimeZone().getID();
        end_tz         = end_time_cal.getTimeZone().getID();
        title          = context.getResources().getString(R.string.default_title);
        pre_text       = context.getResources().getString(R.string.default_pre_text);
        countdown_text = context.getResources().getString(R.string.default_countdown_text);
        complete_text  = context.getResources().getString(R.string.default_complete_text);
        post_text      = context.getResources().getString(R.string.default_post_text);
        precision      = 2;
        show_progress  = true;
        show_start     = true;
        show_end       = true;
        show_years     = true;
        show_months    = true;
        show_weeks     = true;
        show_days      = true;
        show_hours     = true;
        show_minutes   = true;
        show_seconds   = true;
        terminate      = true;
        notify         = true;
    }

    public Progress_bar_data(Context context, String title_in)
    {
        this(context);
        title = title_in;
    }

    public Progress_bar_data(Context context, long rowid_in)
    {
        SQLiteDatabase db = new Progress_bar_DB(context).getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + Progress_bar_contract.Progress_bar_table.TABLE_NAME + " WHERE " + Progress_bar_contract.Progress_bar_table._ID + " = ?", new String[]{ String.valueOf(rowid_in)});
        cursor.moveToFirst();

        rowid          = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table._ID));
        order          = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.ORDER_COL));
        start_time     = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.START_TIME_COL));
        end_time       = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.END_TIME_COL));
        start_tz       = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.START_TZ_COL));
        end_tz         = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.END_TZ_COL));
        title          = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.TITLE_COL));
        pre_text       = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL));
        countdown_text = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL));
        complete_text  = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL));
        post_text      = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL));
        precision      = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.PRECISION_COL));
        show_progress  = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL))    > 0;
        show_start     = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_START_COL))      > 0;
        show_end       = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_END_COL)) > 0;
        show_years     = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL))    > 0;
        show_months    = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL))   > 0;
        show_weeks     = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL))    > 0;
        show_days      = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL))     > 0;
        show_hours     = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL))    > 0;
        show_minutes   = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL))  > 0;
        show_seconds   = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL))  > 0;
        terminate      = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.TERMINATE_COL))     > 0;
        notify         = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.NOTIFY_COL))        > 0;

        cursor.close();
        db.close();
    }

    public Progress_bar_data(Progress_bar_data b)
    {
        rowid          = b.rowid;
        order          = b.order;
        start_time     = b.start_time;
        end_time       = b.end_time;
        start_tz       = b.start_tz;
        end_tz         = b.end_tz;
        title          = b.title;
        pre_text       = b.pre_text;
        countdown_text = b.countdown_text;
        complete_text  = b.complete_text;
        post_text      = b.post_text;
        precision      = b.precision;
        show_progress  = b.show_progress;
        show_start     = b.show_start;
        show_end       = b.show_end;
        show_years     = b.show_years;
        show_months    = b.show_months;
        show_weeks     = b.show_weeks;
        show_days      = b.show_days;
        show_hours     = b.show_hours;
        show_minutes   = b.show_minutes;
        show_seconds   = b.show_seconds;
        terminate      = b.terminate;
        notify         = b.notify;
    }

    public void insert(Context context)
    {
        if(rowid >= 0)
            throw new IllegalStateException("Tried to insert when rowid already set");

        SQLiteDatabase db = new Progress_bar_DB(context).getWritableDatabase();

        if(order < 0)
        {
            Cursor cursor = db.rawQuery("SELECT MAX(" + Progress_bar_contract.Progress_bar_table.ORDER_COL + ") + 1 FROM " + Progress_bar_contract.Progress_bar_table.TABLE_NAME, null);
            cursor.moveToFirst();
            order = cursor.getLong(0);
            cursor.close();
        }

        ContentValues values = new ContentValues();

        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, order);
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, start_time);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, end_time);
        values.put(Progress_bar_contract.Progress_bar_table.START_TZ_COL, start_tz);
        values.put(Progress_bar_contract.Progress_bar_table.END_TZ_COL, end_tz);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, title);
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, pre_text);
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, countdown_text);
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, complete_text);
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, post_text);
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, precision);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, show_start);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, show_end);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, show_progress);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, show_years);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, show_months);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, show_weeks);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, show_days);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, show_hours);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, show_minutes);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, show_seconds);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, terminate);
        values.put(Progress_bar_contract.Progress_bar_table.NOTIFY_COL, notify);

        rowid = db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        db.close();
    }

    public void update(Context context)
    {
        if(rowid < 0)
            throw new IllegalStateException("Tried to update when rowid isn't set");

        SQLiteDatabase db = new Progress_bar_DB(context).getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(Progress_bar_contract.Progress_bar_table._ID, rowid);
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, order);
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, start_time);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, end_time);
        values.put(Progress_bar_contract.Progress_bar_table.START_TZ_COL, start_tz);
        values.put(Progress_bar_contract.Progress_bar_table.END_TZ_COL, end_tz);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, title);
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, pre_text);
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, countdown_text);
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, complete_text);
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, post_text);
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, precision);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, show_start);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, show_end);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, show_progress);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, show_years);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, show_months);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, show_weeks);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, show_days);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, show_hours);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, show_minutes);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, show_seconds);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, terminate);
        values.put(Progress_bar_contract.Progress_bar_table.NOTIFY_COL, notify);

        db.update(Progress_bar_contract.Progress_bar_table.TABLE_NAME, values, Progress_bar_contract.Progress_bar_table._ID + " = ?", new String[]{String.valueOf(rowid)});

        db.close();
    }

    public void delete(Context context)
    {
        if(rowid < 0)
            throw new IllegalStateException("Tried to delete when rowid isn't set");

        SQLiteDatabase db = new Progress_bar_DB(context).getWritableDatabase();

        db.delete(Progress_bar_contract.Progress_bar_table.TABLE_NAME,
                Progress_bar_contract.Progress_bar_table._ID + " = ?",
                new String[] {String.valueOf(rowid)});
        db.close();

        rowid = -1;
    }
}

