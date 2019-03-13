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

package org.mattvchandler.progressbars.list

import android.content.Context
import android.content.res.Resources
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.util.Log

import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.settings.Settings.Companion.get_date_format
import org.mattvchandler.progressbars.settings.Settings.Companion.get_time_format

import java.text.DecimalFormat
import java.util.Calendar
import java.util.Date

import java.lang.Math.abs
import java.lang.Math.max
import java.lang.Math.min

// is a given year a leap year?
private fun is_leap_year(year: Int): Boolean
{
    return when
    {
        year % 4 != 0   -> false
        year % 100 != 0 -> true
        year % 400 != 0 -> false
        else            -> true
    }
}

// number of days in each month. assumes non-leap year for Feb.
private val days_in_mon = intArrayOf(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

// struct bound to progress_bar_row layout
// class and fields need to be public so they can be accessed by binding
class View_data (context: Context, data: Data): Data(data) // contains all DB data from inherited struct
{
    // fields used by UI
    lateinit var start_date_disp: String
    lateinit var start_time_disp: String
    lateinit var end_date_disp: String
    lateinit var end_time_disp: String

    val progress_disp = ObservableInt()
    val percentage_disp = ObservableField<String>()
    val time_text_disp = ObservableField<String>()

    // only show countdown when there is something to show
    var show_time_text: Boolean = true

    private val start_time_date = Date()
    private val end_time_date = Date()

    private fun calc_percentage(total_interval: Long, elapsed: Long, now_s: Long)
    {
        // convert and round percentage to the specified precision
        var dec_format = "0"

        if(precision > 0)
            dec_format += "."

        for(i in 0 until precision)
            dec_format += "0"

        dec_format += "%"

        // if start and end are the same time, set to 100 if in the past, 0 if in the future
        when
        {
            total_interval != 0L ->
            {
                val percentage_fraction = min(max(elapsed.toDouble() / total_interval.toDouble(), 0.0), 1.0)

                percentage_disp.set(DecimalFormat(dec_format).format(percentage_fraction))
                progress_disp.set((percentage_fraction * 100.0).toInt())
            }
            now_s >= end_time ->
            {
                percentage_disp.set(DecimalFormat(dec_format).format(1.0))
                progress_disp.set(100)
            }
            else ->
            {
                percentage_disp.set(DecimalFormat(dec_format).format(0.0))
                progress_disp.set(0)
            }
        }
    }

    // get remaining time - time based (easy)
    private fun get_remaining_easy(res: Resources, to_start: Long, remaining: Long): String
    {
        // get and format remaining time
        var revised_remaining = remaining // work around for lack of pass by copy
        val remaining_prefix: String

        when
        {
            to_start >= 0 ->
            {
                // now is before start, so count down to start time
                revised_remaining = to_start
                remaining_prefix = if(separate_time) pre_text else single_pre_text
            }
            revised_remaining >= 0 ->
            {
                // now is between start and end, so count down to end
                remaining_prefix = countdown_text
                if(!separate_time)
                    Log.e("get_remaining_easy", "countdown on single time: to_start $to_start, remaining: $remaining, revised_remaining: $revised_remaining")
            }
            else ->
            {
                // now is after end, so count up from end
                remaining_prefix = if(separate_time) post_text else single_post_text
            }
        }

        // get # of each unit
        val weeks = abs(revised_remaining) / (7L * 24L * 60L * 60L)
        var days = abs(revised_remaining) / (24L * 60L * 60L)
        var hours = abs(revised_remaining) / (60L * 60L)
        var minutes = abs(revised_remaining) / 60L
        var seconds = abs(revised_remaining)

        // for each unit shown, take its value out of the smaller unit counts
        // ex: if there is 1 minute and 90 seconds, pull 60 seconds out to give 2m 30s
        if(show_weeks)
        {
            days %= 7L
            hours %= 7L * 24L
            minutes %= 7L * 24L * 60L
            seconds %= 7L * 24L * 60L * 60L
        }

        if(show_days)
        {
            hours %= 24L
            minutes %= 24L * 60L
            seconds %= 24L * 60L * 60L
        }

        if(show_hours)
        {
            minutes %= 60L
            seconds %= 60L * 60L
        }

        if(show_minutes)
        {
            seconds %= 60L
        }

        return format_text(res, remaining_prefix, seconds, minutes, hours, days, weeks, 0, 0)
    }

    // get remaining time - calendar based (not as easy)
    private fun get_remaining_hard(res: Resources, to_start: Long, remaining: Long): String
    {
        val cal_start = Calendar.getInstance()
        var cal_end = Calendar.getInstance()
        cal_end.time = end_time_date

        // get and format remaining time
        val remaining_prefix: String

        when
        {
            to_start >= 0 ->
            {
                // now is before start, so count down to start time
                remaining_prefix = if(separate_time) pre_text else single_pre_text
                cal_end.time = start_time_date
            }
            remaining >= 0 ->
            {
                // now is between start and end, so count down to end
                remaining_prefix = countdown_text
                if(!separate_time)
                    Log.e("get_remaining_hard", "countdown on single time: to_start $to_start, remaining: $remaining, cal_start: $cal_start, cal_end: $cal_end")
            }
            else ->
            {
                // now is after end, so count up from end
                remaining_prefix = if(separate_time) post_text else single_post_text
                cal_end = cal_start.clone() as Calendar

                cal_start.time = end_time_date
            }
        }

        var seconds = 0L
        var minutes = 0L
        var hours = 0L
        var days = 0L
        var weeks: Long
        var months = 0L
        var years = 0L

        // subtract calendar dates, with manual borrowing between units
        seconds += (cal_end.get(Calendar.SECOND) - cal_start.get(Calendar.SECOND)).toLong()
        if(seconds < 0L)
        {
            minutes -= 1L
            seconds += 60L
        }

        minutes += (cal_end.get(Calendar.MINUTE) - cal_start.get(Calendar.MINUTE)).toLong()
        if(minutes < 0L)
        {
            hours -= 1L
            minutes += 60L
        }

        hours += (cal_end.get(Calendar.HOUR_OF_DAY) - cal_start.get(Calendar.HOUR_OF_DAY)).toLong()
        if(hours < 0L)
        {
            days -= 1L
            hours += 24L
        }

        days += (cal_end.get(Calendar.DAY_OF_MONTH) - cal_start.get(Calendar.DAY_OF_MONTH)).toLong()
        if(days < 0L)
        {
            // borrowing from months is tricky, because the # of days / mon varies
            months -= 1

            val cal_end_month = cal_end.get(Calendar.MONTH)

            days += if(cal_end_month == Calendar.MARCH && is_leap_year(cal_end.get(Calendar.YEAR)))
            {
                // for Feb on a leap year, we get 29 days
                29L
            }
            else
            {
                // otherwise get previous month's # of days
                if(cal_end_month == Calendar.JANUARY)
                    days_in_mon[Calendar.DECEMBER].toLong()
                else
                    days_in_mon[cal_end_month - 1].toLong()
            }
        }

        // recalculate weeks from # of days
        weeks = days / 7L
        days %= 7L

        months += (cal_end.get(Calendar.MONTH) - cal_start.get(Calendar.MONTH)).toLong()
        if(months < 0L)
        {
            years -= 1L
            months += 12L
        }

        years += (cal_end.get(Calendar.YEAR) - cal_start.get(Calendar.YEAR)).toLong()

        // for each unit not shown, add its value to the next smaller unit
        if(!show_years)
        {
            months += years * 12L
        }
        if(!show_months)
        {
            // again, tricky logic for months
            // get a raw number of days, including weeks
            var tmp_days = days + 7L * weeks

            var curr_mon = cal_start.get(Calendar.MONTH)
            var curr_year = cal_start.get(Calendar.YEAR)

            // for each month in the remaining range, add correct # of days
            for(m in 0 until months % 12)
            {
                tmp_days += if(curr_mon == Calendar.FEBRUARY && is_leap_year(curr_year))
                    29L
                else
                    days_in_mon[curr_mon].toLong()

                // when we wrap around the year's end, get the remaining counts from the end's year
                if(++curr_mon == 12)
                {
                    curr_mon = 0
                    curr_year = cal_end.get(Calendar.YEAR)
                }
            }

            // recalculate weeks and days
            weeks = tmp_days / 7L
            days = tmp_days % 7L
        }
        if(!show_weeks)
        {
            days += weeks * 7L
        }
        if(!show_days)
        {
            hours += days * 24L
        }
        if(!show_hours)
        {
            minutes += hours * 60L
        }
        if(!show_minutes)
        {
            seconds += minutes * 60L
        }

        return format_text(res, remaining_prefix, seconds, minutes, hours, days, weeks, months, years)
    }

    private fun format_text(res: Resources, remaining_prefix: String, seconds: Long, minutes: Long, hours: Long, days: Long, weeks: Long, months: Long, years: Long): String
    {
        var remaining_str = remaining_prefix

        // figure out which units to display, based on user selection and omitting any w/ 0 values
        val seconds_shown = show_seconds
        val minutes_shown = show_minutes && (minutes > 0 || !seconds_shown)
        val hours_shown = show_hours && (hours > 0 || !minutes_shown && !seconds_shown)
        val days_shown = show_days && (days > 0 || !hours_shown && !minutes_shown && !seconds_shown)
        val weeks_shown = show_weeks && (weeks > 0 || !days_shown && !hours_shown && !minutes_shown && !seconds_shown)
        val months_shown = show_months && (months > 0 || !weeks_shown && !days_shown && !hours_shown && !minutes_shown && !seconds_shown)
        val years_shown = show_years && (years > 0 || !months_shown && !weeks_shown && !days_shown && !hours_shown && !minutes_shown && !seconds_shown)

        val and = res.getString(R.string.and)

        // figure out plurality and which unit to add 'and' to
        if(years_shown)
        {
            remaining_str += res.getQuantityString(R.plurals.year, years.toInt(), years.toInt())

            val trailing = (if(months_shown) 1 else 0) +
                    (if(weeks_shown) 1 else 0) +
                    (if(days_shown) 1 else 0) +
                    (if(hours_shown) 1 else 0) +
                    (if(minutes_shown) 1 else 0) +
                    if(seconds_shown) 1 else 0
            if(trailing > 1)
                remaining_str += ", "
            else if(trailing == 1)
                remaining_str += ", $and "
        }

        if(months_shown)
        {
            remaining_str += res.getQuantityString(R.plurals.month, months.toInt(), months.toInt())

            val trailing = (if(weeks_shown) 1 else 0) +
                    (if(days_shown) 1 else 0) +
                    (if(hours_shown) 1 else 0) +
                    (if(minutes_shown) 1 else 0) +
                    if(seconds_shown) 1 else 0
            if(trailing > 1)
                remaining_str += ", "
            else if(trailing == 1)
                remaining_str += ", $and "
        }

        if(weeks_shown)
        {
            remaining_str += res.getQuantityString(R.plurals.week, weeks.toInt(), weeks.toInt())

            val trailing = (if(days_shown) 1 else 0) +
                    (if(hours_shown) 1 else 0) +
                    (if(minutes_shown) 1 else 0) +
                    if(seconds_shown) 1 else 0
            if(trailing > 1)
                remaining_str += ", "
            else if(trailing == 1)
                remaining_str += ", $and "
        }

        if(days_shown)
        {
            remaining_str += res.getQuantityString(R.plurals.day, days.toInt(), days.toInt())

            val trailing = (if(hours_shown) 1 else 0) +
                    (if(minutes_shown) 1 else 0) +
                    if(seconds_shown) 1 else 0
            if(trailing > 1)
                remaining_str += ", "
            else if(trailing == 1)
                remaining_str += ", $and "
        }

        if(hours_shown)
        {
            remaining_str += res.getQuantityString(R.plurals.hour, hours.toInt(), hours.toInt())

            val trailing = (if(minutes_shown) 1 else 0) + if(seconds_shown) 1 else 0
            if(trailing > 1)
                remaining_str += ", "
            else if(trailing == 1)
                remaining_str += ", $and "
        }

        if(minutes_shown)
        {
            remaining_str += res.getQuantityString(R.plurals.minute, minutes.toInt(), minutes.toInt())

            if(seconds_shown)
                remaining_str += ", $and "
        }

        if(seconds_shown)
        {
            remaining_str += res.getQuantityString(R.plurals.second, seconds.toInt(), seconds.toInt())
        }

        return remaining_str
    }

    // run every second. updates percentage and time remaining text
    fun update_display(res: Resources)
    {
        // get now, start and end times as unix epoch timestamps
        val now_s = System.currentTimeMillis() / 1000L

        // only calculate percentage if is being shown
        if(show_progress && separate_time)
        {
            val total_interval = end_time - start_time
            val elapsed = now_s - start_time

            calc_percentage(total_interval, elapsed, now_s)
        }

        // if we are at the start, show started text
        if(separate_time && now_s == start_time)
        {
            time_text_disp.set(start_text)
            return
        }
        // if we are at the end (or past when terminate is set) show completed text
        else if((terminate && now_s > end_time)  || now_s == end_time)
        {
            if(separate_time)
                time_text_disp.set(complete_text)
            else
                time_text_disp.set(single_complete_text)
            return
        }

        // only calculate time remaining if start or complete text shouldn't be shown
        if(show_time_text || now_s != start_time || now_s != end_time)
        {
            // time from now to end
            val remaining = end_time - now_s
            // time from now to start
            val to_start = start_time - now_s

            // if not needing calendar time difference, we can do calculation from the difference in seconds (much easier)
            val remaining_str = if(!show_years && !show_months)
                get_remaining_easy(res, to_start, remaining)
            else
                get_remaining_hard(res, to_start, remaining)

            time_text_disp.set(remaining_str)
        }
    }

    fun reinit(context: Context)
    {
        // format start and end dates and times
        val date_df = get_date_format(context)
        val time_df = get_time_format()

        start_time_date.time = start_time * 1000L
        end_time_date.time = end_time * 1000L

        start_date_disp = date_df.format(start_time_date)
        start_time_disp = time_df.format(start_time_date)
        end_date_disp   = date_df.format(end_time_date)
        end_time_disp   = time_df.format(end_time_date)

        // set initial percentage to 0
        progress_disp.set(0)

        show_time_text = show_years || show_months || show_weeks || show_days || show_hours || show_minutes || show_seconds

        // do a run now, to set up remaining display data
        update_display(context.resources)
    }
    init
    {
        reinit(context)
    }
}
