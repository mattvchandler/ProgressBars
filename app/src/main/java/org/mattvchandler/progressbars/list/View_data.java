package org.mattvchandler.progressbars.list;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.preference.PreferenceManager;

import org.mattvchandler.progressbars.db.Data;
import org.mattvchandler.progressbars.R;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

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

// struct bound to progress_bar_row layout
@SuppressWarnings("WeakerAccess") // class and fields need to be public so they can be accessed by binding
public final class View_data extends Data // contains all DB data from inherited struct
{
    // observable fields used by UI
    public final ObservableField<String>  title_disp          = new ObservableField<>();

    public final ObservableField<String>  start_date_disp     = new ObservableField<>();
    public final ObservableField<String>  start_time_disp     = new ObservableField<>();
    public final ObservableField<String>  end_date_disp       = new ObservableField<>();
    public final ObservableField<String>  end_time_disp       = new ObservableField<>();

    public final ObservableField<String>  percentage_disp     = new ObservableField<>();
    public final ObservableInt            progress_disp       = new ObservableInt();

    public final ObservableField<String>  time_text_disp      = new ObservableField<>();

    public final ObservableBoolean        show_start_disp     = new ObservableBoolean();
    public final ObservableBoolean        show_end_disp       = new ObservableBoolean();
    public final ObservableBoolean        show_progress_disp  = new ObservableBoolean();
    public final ObservableBoolean        show_time_text_disp = new ObservableBoolean();

    private final Date start_time_date = new Date();
    private final Date end_time_date = new Date();

    // is a given year a leap year?
    private static boolean is_leap_year(int year)
    {
        if(year % 4 != 0)
            return false;
        else if (year % 100 != 0)
            return true;
        else if(year % 400 != 0)
            return false;

        return true;
    }

    // number of days in each month. assumes non-leap year for Feb.
    private static final int[] days_in_mon = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private void calc_percentage(long total_interval, long elapsed)
    {
        // convert and round percentage to the specified precision
        StringBuilder dec_format = new StringBuilder("0");

        if(precision > 0)
            dec_format.append(".");

        for(int i = 0; i < precision; ++i)
            dec_format.append("0");

        dec_format.append("%");

        // if start and end are the same time, set to 100%
        if(total_interval != 0 )
        {
            double percentage_fraction = max((double)elapsed / (double)total_interval, 0.0);

            if(terminate)
                percentage_fraction = min(percentage_fraction, 1.0);

            percentage_disp.set(new DecimalFormat(dec_format.toString()).format(percentage_fraction));
            progress_disp.set((int)(percentage_fraction * 100.0));
        }
        else
        {
            percentage_disp.set(new DecimalFormat(dec_format.toString()).format(1.0));
            progress_disp.set(100);
        }
    }

    // get remaining time - time based (easy)
    private String get_remaining_easy(Resources res, long remaining, long to_start)
    {
        // get and format remaining time
        String remaining_prefix;

        if(to_start >= 0)
        {
            // now is before start, so count down to start time
            remaining = to_start;
            remaining_prefix = pre_text;
        }
        else if(remaining >= 0)
        {
            // now is between start and end, so count down to end
            remaining_prefix = countdown_text;
        }
        else
        {
            // now is after end, so count up from end
            remaining_prefix = post_text;
        }

        // get # of each unit
        long weeks = abs(remaining) / (7L * 24L * 60L * 60L);
        long days = abs(remaining) / (24L * 60L * 60L);
        long hours = abs(remaining) / (60L * 60L);
        long minutes = abs(remaining) / (60L);
        long seconds = abs(remaining);

        // for each unit shown, take its value out of the smaller unit counts
        // ex: if there is 1 minute and 90 seconds, pull 60 seconds out to give 2m 30s
        if(show_weeks)
        {
            days %= 7L;
            hours %= (7L * 24L);
            minutes %= (7L * 24L * 60L);
            seconds %= (7L * 24L * 60L * 60L);
        }

        if(show_days)
        {
            hours %= 24L;
            minutes %= (24L * 60L);
            seconds %= (24L * 60L * 60L);
        }

        if(show_hours)
        {
            minutes %= 60L;
            seconds %= (60L * 60L);
        }

        if(show_minutes)
        {
            seconds %= 60L;
        }

        return format_text(res, remaining_prefix, seconds, minutes, hours, days, weeks, 0, 0);
    }

    // get remaining time - calendar based (not as easy)
    private String get_remaining_hard(Resources res, long to_start, long remaining)
    {
        Calendar cal_start = Calendar.getInstance();
        Calendar cal_end = Calendar.getInstance();
        cal_end.setTime(end_time_date);

        // get and format remaining time
        String remaining_prefix;

        if(to_start >= 0)
        {
            // now is before start, so count down to start time
            remaining_prefix = pre_text;
            cal_end.setTime(start_time_date);
        }
        else if(remaining >= 0)
        {
            // now is between start and end, so count down to end
            remaining_prefix = countdown_text;
        }
        else
        {
            // now is after end, so count up from end
            remaining_prefix = post_text;
            cal_end = (Calendar) cal_start.clone();
            cal_start.setTime(end_time_date);
        }

        long seconds = 0L, minutes = 0L, hours = 0L, days = 0L, weeks, months = 0L, years = 0L;

        // subtract calendar dates, with manual borrowing between units
        seconds += cal_end.get(Calendar.SECOND) - cal_start.get(Calendar.SECOND);
        if(seconds < 0L)
        {
            minutes -= 1L;
            seconds += 60L;
        }

        minutes += cal_end.get(Calendar.MINUTE) - cal_start.get(Calendar.MINUTE);
        if(minutes < 0L)
        {
            hours -= 1L;
            minutes += 60L;
        }

        hours += cal_end.get(Calendar.HOUR_OF_DAY) - cal_start.get(Calendar.HOUR_OF_DAY);
        if(hours < 0L)
        {
            days -= 1L;
            hours += 24L;
        }

        days += cal_end.get(Calendar.DAY_OF_MONTH) - cal_start.get(Calendar.DAY_OF_MONTH);
        if(days < 0L)
        {
            // borrowing from months is tricky, because the # of days / mon varies
            months -= 1;

            int cal_end_month = cal_end.get(Calendar.MONTH);

            if(cal_end_month == Calendar.MARCH && is_leap_year(cal_end.get(Calendar.YEAR)))
            {
                // for Feb on a leap year, we get 29 days
                days += 29L;
            }
            else
            {
                // otherwise get previous month's # of days
                if(cal_end_month == Calendar.JANUARY)
                    days += days_in_mon[Calendar.DECEMBER];
                else
                    days += days_in_mon[cal_end_month - 1];
            }
        }

        // recalculate weeks from # of days
        weeks = days / 7L;
        days %= 7L;

        months += cal_end.get(Calendar.MONTH) - cal_start.get(Calendar.MONTH);
        if(months < 0L)
        {
            years -= 1L;
            months += 12L;
        }

        years += cal_end.get(Calendar.YEAR) - cal_start.get(Calendar.YEAR);

        // for each unit not shown, add its value to the next smaller unit
        if(!show_years)
        {
            months += years * 12L;
        }
        if(!show_months)
        {
            // again, tricky logic for months
            // get a raw number of days, including weeks
            long tmp_days = days + 7L * weeks;

            int curr_mon = cal_start.get(Calendar.MONTH);
            int curr_year = cal_start.get(Calendar.YEAR);

            // for each month in the remaining range, add correct # of days
            for(long m = 0; m < months % 12; ++m)
            {
                if(curr_mon == Calendar.FEBRUARY && is_leap_year(curr_year))
                    tmp_days += 29L;
                else
                    tmp_days += days_in_mon[curr_mon];

                // when we wrap around the year's end, get the remaining counts from the end's year
                if(++curr_mon == 12)
                {
                    curr_mon = 0;
                    curr_year = cal_end.get(Calendar.YEAR);
                }
            }

            // recalculate weeks and days
            weeks = tmp_days / 7L;
            days = tmp_days % 7L;
        }
        if(!show_weeks)
        {
            days += weeks * 7L;
        }
        if(!show_days)
        {
            hours += days * 24L;
        }
        if(!show_hours)
        {
            minutes += hours * 60L;
        }
        if(!show_minutes)
        {
            seconds += minutes * 60L;
        }

        return format_text(res, remaining_prefix, seconds, minutes, hours, days, weeks, months, years);
    }

    @SuppressWarnings("ConstantConditions")
    private String format_text(Resources res, String remaining_prefix, long seconds, long minutes, long hours, long days, long weeks, long months, long years)
    {
        String remaining_str = remaining_prefix;

        // figure out which units to display, based on user selection and omitting any w/ 0 values
        boolean seconds_shown = show_seconds;
        boolean minutes_shown = show_minutes && (minutes > 0 || !seconds_shown);
        boolean hours_shown = show_hours && (hours > 0 || (!minutes_shown && !seconds_shown));
        boolean days_shown = show_days && (days > 0 || (!hours_shown && !minutes_shown && !seconds_shown));
        boolean weeks_shown = show_weeks && (weeks > 0 || (!days_shown && !hours_shown && !minutes_shown && !seconds_shown));
        boolean months_shown = show_months && (months > 0 || (!weeks_shown && !days_shown && !hours_shown && !minutes_shown && !seconds_shown));
        boolean years_shown = show_years && (years > 0 || (!months_shown && !weeks_shown && !days_shown && !hours_shown && !minutes_shown && !seconds_shown));

        String and = res.getString(R.string.and);

        // figure out plurality and which unit to add 'and' to
        if(years_shown)
        {
            remaining_str += String.valueOf(years) + " " + res.getString(years == 1 ? R.string.year: R.string.years);

            int trailing = (months_shown ? 1 : 0) +
                    (weeks_shown ? 1 : 0) +
                    (days_shown ? 1 : 0) +
                    (hours_shown ? 1 : 0) +
                    (minutes_shown ? 1 : 0) +
                    (seconds_shown ? 1 : 0);
            if(trailing > 1)
                remaining_str += ", ";
            else if(trailing == 1)
                remaining_str += ", " + and + " ";
        }

        if(months_shown)
        {
            remaining_str += months + " " + res.getString(months == 1 ? R.string.month : R.string.months);

            int trailing = (weeks_shown ? 1 : 0) +
                    (days_shown ? 1 : 0) +
                    (hours_shown ? 1 : 0) +
                    (minutes_shown ? 1 : 0) +
                    (seconds_shown ? 1 : 0);
            if(trailing > 1)
                remaining_str += ", ";
            else if(trailing == 1)
                remaining_str += ", " + and + " ";
        }

        if(weeks_shown)
        {
            remaining_str += weeks + " " + res.getString(weeks == 1 ? R.string.week : R.string.weeks);

            int trailing = (days_shown ? 1 : 0) +
                    (hours_shown ? 1 : 0) +
                    (minutes_shown ? 1 : 0) +
                    (seconds_shown ? 1 : 0);
            if(trailing > 1)
                remaining_str += ", ";
            else if(trailing == 1)
                remaining_str += ", " + and + " ";
        }

        if(days_shown)
        {
            remaining_str += days + " " + res.getString(days == 1 ? R.string.day : R.string.days);

            int trailing = (hours_shown ? 1 : 0) +
                    (minutes_shown ? 1 : 0) +
                    (seconds_shown ? 1 : 0);
            if(trailing > 1)
                remaining_str += ", ";
            else if(trailing == 1)
                remaining_str += ", " + and + " ";
        }

        if(hours_shown)
        {
            remaining_str += hours + " " + res.getString(hours == 1 ? R.string.hour : R.string.hours);

            int trailing = (minutes_shown ? 1 : 0) +
                    (seconds_shown ? 1 : 0);
            if(trailing > 1)
                remaining_str += ", ";
            else if(trailing == 1)
                remaining_str += ", " + and + " ";
        }

        if(minutes_shown)
        {
            remaining_str += minutes + " " + res.getString(minutes == 1 ? R.string.minute : R.string.minutes);

            if(seconds_shown)
                remaining_str += ", " + and + " ";
        }

        if(seconds_shown)
        {
            remaining_str += seconds + " " + res.getString(seconds == 1 ? R.string.second : R.string.seconds);
        }

        return remaining_str;
    }

    // run every second. updates percentage and time remaining text
    void update(Resources res)
    {
        // get now, start and end times as unix epoch timestamps
        long now_s = System.currentTimeMillis() / 1000L;

        long total_interval = end_time - start_time;
        long elapsed = now_s - start_time;

        // only calculate percentage if is being shown
        if(show_progress)
        {
            calc_percentage(total_interval, elapsed);
        }

        // if we are at the start, show started text
        if(now_s == start_time)
        {
            time_text_disp.set(start_text);
        }
        // if we are at the end (or past when terminate is set) show completed text
        else if(terminate && now_s > end_time || now_s == end_time)
        {
            time_text_disp.set(complete_text);

            // stop if we are done and terminate is set
            if(terminate && now_s > end_time)
                return;
        }

        // only calculate time remaining if start or complete text shouldn't be shown
        if(show_time_text_disp.get() || now_s != start_time || now_s != end_time)
        {
            // time from now to end
            long remaining = end_time - now_s;
            // time from now to start
            long to_start = start_time - now_s;

            String remaining_str;

            // if not needing calendar time difference, we can do calculation from the difference in seconds (much easier)
            if(!show_years && !show_months)
            {
                remaining_str = get_remaining_easy(res, to_start, remaining);
            }
            else
            {
                remaining_str = get_remaining_hard(res, to_start, remaining);
            }

            time_text_disp.set(remaining_str);
        }
    }

    View_data(Context context, Cursor cursor)
    {
        // get the DB data
        super(cursor);

        title_disp.set(title);

        // set up visibility
        show_start_disp.set(show_start);
        show_end_disp.set(show_end);
        show_progress_disp.set(show_progress);
        // only show countdown when there is something to show
        show_time_text_disp.set((show_years || show_months || show_weeks || show_days || show_hours || show_minutes || show_seconds));

        boolean hour_24 = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("hour_24", true);

        // format start and end dates and times
        SimpleDateFormat date_df = new SimpleDateFormat(
                PreferenceManager.getDefaultSharedPreferences(context)
                .getString("date_format", context.getResources().getString(R.string.pref_date_format_default)),
                Locale.US);
        SimpleDateFormat time_df = new SimpleDateFormat(context.getResources().getString(hour_24 ? R.string.time_format_24 : R.string.time_format_12), Locale.US);

        start_time_date.setTime(start_time * 1000L);
        end_time_date.setTime(end_time * 1000L);

        start_date_disp.set(date_df.format(start_time_date));
        start_time_disp.set(time_df.format(start_time_date));
        end_date_disp.set(date_df.format(end_time_date));
        end_time_disp.set(time_df.format(end_time_date));

        // set initial percentage to 0
        progress_disp.set(0);

        // do a run now, to set up remaining display data
        update(context.getResources());
    }
}
