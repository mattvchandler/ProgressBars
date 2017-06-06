package org.mattvchandler.progressbars;

import android.database.Cursor;

import java.util.Calendar;

public class Progress_bar_data
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

    public Integer precision;

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
    public  boolean notify;

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
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_START_COL))    > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_END_COL))      > 0,
            cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL)) > 0,
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

    Progress_bar_data()
    {
        Calendar start_time_cal = Calendar.getInstance();
        Calendar end_time_cal = (Calendar) start_time_cal.clone();
        end_time_cal.add(Calendar.HOUR, 1);

        // TODO replace hard-coded strings
        start_time = start_time_cal.getTimeInMillis() / 1000L;
        end_time = end_time_cal.getTimeInMillis() / 1000L;
        start_tz = start_time_cal.getTimeZone().getID();
        end_tz = end_time_cal.getTimeZone().getID();
        title = "New timer";
        pre_text = "Time until start: ";
        countdown_text = "Time remaining: ";
        complete_text = "Completed";
        post_text = "Time since completion: ";
        precision = 2;
        show_progress = true;
        show_start = true;
        show_end = true;
        show_years = true;
        show_months = true;
        show_weeks = true;
        show_days = true;
        show_hours = true;
        show_minutes = true;
        show_seconds = true;
        terminate = true;
        notify = true;
    }
}

