package org.mattvchandler.progressbars;

import android.database.Cursor;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.os.Handler;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

// struct bound to progress_bar_row layout
public final class Progress_bar_view_data extends Progress_bar_data
{
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

    private static final int[] days_in_mon = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

    private class Update implements Runnable
    {
        private final Handler handler = new Handler();
        private final int delay = 1000; // 1000ms

        // run every second. updates percentage and time remaining text
        public void run()
        {
            // get now, start and end times as unix epoch timestamps
            long now_s = System.currentTimeMillis() / 1000L;
            long start_time_s = start_time_date.getTime() / 1000L;
            long end_time_s = end_time_date.getTime() / 1000L;

            long total_interval = end_time_s - start_time_s;
            long elapsed = now_s - start_time_s;

            if(show_progress)
            {
                // convert and round percentage to the specified precision
                String dec_format = "0";

                if(precision > 0)
                    dec_format += ".";

                for(int i = 0; i < precision; ++i)
                    dec_format += "0";

                dec_format += "%";

                if(total_interval != 0 )
                {
                    double percentage_fraction = max((double)elapsed / (double)total_interval, 0.0);

                    if(terminate)
                        percentage_fraction = min(percentage_fraction, 1.0);

                    percentage_disp.set(new DecimalFormat(dec_format).format(percentage_fraction));
                    progress_disp.set((int)(percentage_fraction * 100.0));
                }
                else
                {
                    percentage_disp.set(new DecimalFormat(dec_format).format(1.0));
                    progress_disp.set(100);
                }
            }
            if(terminate && now_s > end_time_s)
            {
                time_text_disp.set(complete_text);
                // TODO: notification?
                return;
            }

            if(!show_time_text_disp.get())
                return;

            long remaining = end_time_s - now_s;
            long to_start = start_time_s - now_s;

            Calendar cal_start = Calendar.getInstance();
            Calendar cal_end = Calendar.getInstance(); cal_end.setTime(end_time_date);

            // get and format remaining time
            String remaining_str;
            if(to_start >= 0)
            {
                remaining = to_start;
                remaining_str = pre_text;
                cal_end.setTime(start_time_date);
            }
            else if(remaining >= 0)
            {
                remaining_str = countdown_text;
            }
            else
            {
                remaining_str = post_text;
                cal_end = (Calendar)cal_start.clone();
                cal_start.setTime(end_time_date);
            }

            long seconds = 0L, minutes = 0L, hours = 0L, days = 0L, weeks = 0L, months = 0L, years = 0L;

            // if not needing calendar time difference, we can do calculation from the difference in seconds (much easier)
            if(!show_years && !show_months)
            {
                weeks = abs(remaining) / (7L * 24L * 60L * 60L);
                days = abs(remaining) / (24L * 60L * 60L);
                hours = abs(remaining) / (60L * 60L);
                minutes = abs(remaining) / (60L);
                seconds = abs(remaining);

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
            }
            else
            {
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
                    months -= 1;
                    int cal_end_month = cal_end.get(Calendar.MONTH);
                    if(cal_end_month == Calendar.FEBRUARY &&
                       is_leap_year(cal_end.get(Calendar.YEAR)))
                    {
                        days += 29L;
                    } else
                    {
                        if(cal_end_month == Calendar.JANUARY)
                            days += days_in_mon[Calendar.DECEMBER];
                        else
                            days += days_in_mon[cal_end_month - 1];
                    }

                    weeks = days / 7L;
                    days %= 7L;
                }

                months += cal_end.get(Calendar.MONTH) - cal_start.get(Calendar.MONTH);
                if(months < 0L)
                {
                    years -= 1L;
                    months += 12L;
                }

                years += cal_end.get(Calendar.YEAR) - cal_start.get(Calendar.YEAR);
                if(!show_years)
                {
                    months += years * 12L;
                }
                if(!show_months)
                {
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

                        if(++curr_mon == 12)
                        {
                            curr_mon = 0;
                            curr_year = cal_end.get(Calendar.YEAR);
                        }
                    }

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
            }

            boolean seconds_shown = show_seconds;
            boolean minutes_shown = show_minutes && (minutes > 0 || !seconds_shown);
            boolean hours_shown = show_hours && (hours > 0 || (!minutes_shown && !seconds_shown));
            boolean days_shown = show_days && (days > 0 || (!hours_shown && !minutes_shown && !seconds_shown));
            boolean weeks_shown = show_weeks && (weeks > 0 || (!days_shown && !hours_shown && !minutes_shown && !seconds_shown));
            boolean months_shown = show_months && (months > 0 || (!weeks_shown && !days_shown && !hours_shown && !minutes_shown && !seconds_shown));
            boolean years_shown = show_years && (years > 0 || (!months_shown && !weeks_shown && !days_shown && !hours_shown && !minutes_shown && !seconds_shown));

            if(years_shown)
            {
                remaining_str += String.valueOf(years) + " year" + (years == 1 ? "" : "s");

                int trailing = (months_shown ? 1 : 0) +
                               (weeks_shown ? 1 : 0) +
                               (days_shown ? 1 : 0) +
                               (hours_shown ? 1 : 0) +
                               (minutes_shown ? 1 : 0) +
                               (seconds_shown ? 1 : 0);
                if(trailing > 1)
                    remaining_str += ", ";
                else if(trailing == 1)
                    remaining_str += ", and ";
            }

            if(months_shown)
            {
                remaining_str += months + " month" + (months == 1 ? "" : "s");

                int trailing = (weeks_shown ? 1 : 0) +
                               (days_shown ? 1 : 0) +
                               (hours_shown ? 1 : 0) +
                               (minutes_shown ? 1 : 0) +
                               (seconds_shown ? 1 : 0);
                if(trailing > 1)
                    remaining_str += ", ";
                else if(trailing == 1)
                    remaining_str += ", and ";
            }

            if(weeks_shown)
            {
                remaining_str += weeks + " week" + (weeks == 1 ? "" : "s");

                int trailing = (days_shown ? 1 : 0) +
                               (hours_shown ? 1 : 0) +
                               (minutes_shown ? 1 : 0) +
                               (seconds_shown ? 1 : 0);
                if(trailing > 1)
                    remaining_str += ", ";
                else if(trailing == 1)
                    remaining_str += ", and ";
            }

            if(days_shown)
            {
                remaining_str += days + " day" + (days == 1 ? "" : "s");

                int trailing = (hours_shown ? 1 : 0) +
                               (minutes_shown ? 1 : 0) +
                               (seconds_shown ? 1 : 0);
                if(trailing > 1)
                    remaining_str += ", ";
                else if(trailing == 1)
                    remaining_str += ", and ";
            }

            if(hours_shown)
            {
                remaining_str += hours + " hour" + (hours == 1 ? "" : "s");

                int trailing = (minutes_shown ? 1 : 0) +
                               (seconds_shown ? 1 : 0);
                if(trailing > 1)
                    remaining_str += ", ";
                else if(trailing == 1)
                    remaining_str += ", and ";
            }

            if(minutes_shown)
            {
                remaining_str += minutes + " minute" + (minutes == 1 ? "" : "s");

                if(seconds_shown)
                    remaining_str += ", and ";
            }

            if(seconds_shown)
            {
                remaining_str += seconds + " second" + (seconds == 1 ? "" : "s");
            }

            time_text_disp.set(remaining_str);

            handler.postDelayed(this, delay);
        }
    }

    Progress_bar_view_data(Cursor cursor)
    {
        super(cursor);

        start_time_date.setTime(start_time * 1000L);
        end_time_date.setTime(end_time * 1000L);

        title_disp.set(title);

        show_start_disp.set(show_start);
        show_end_disp.set(show_end);
        show_progress_disp.set(show_progress);

        SimpleDateFormat date_df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat time_df = new SimpleDateFormat("HH:mm:ss", Locale.US);

        start_date_disp.set(date_df.format(start_time_date));
        start_time_disp.set(time_df.format(start_time_date));
        end_date_disp.set(date_df.format(end_time_date));
        end_time_disp.set(time_df.format(end_time_date));

        show_time_text_disp.set((show_years || show_months || show_weeks || show_days || show_hours || show_minutes || show_seconds));

        progress_disp.set(0);

        Update updater = new Update();
        updater.run();
    }

}
