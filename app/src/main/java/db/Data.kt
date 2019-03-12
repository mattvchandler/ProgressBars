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

package org.mattvchandler.progressbars.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.util.Log
import org.mattvchandler.progressbars.R
import java.io.Serializable
import java.util.*
import java.util.concurrent.atomic.AtomicLong

// struct w/ copy of all DB columns. Serializable so we can store the whole thing
open class Data(): Serializable
{
    var rowid = -1L // is -1 when not set, ie. the data doesn't exist in the DB // TODO: probably don't need this

    var separate_time = true
    var start_time    = 0L
    var end_time      = 0L

    var start_tz = ""
    var end_tz   = ""

    var repeats             = false
    var repeat_count        = 1
    var repeat_unit         = Progress_bars_table.Unit.DAY.index
    var repeat_days_of_week = Progress_bars_table.Days_of_week.all_days_mask()

    var title                 = ""
    var pre_text              = ""
    var start_text            = ""
    var countdown_text        = ""
    var complete_text         = ""
    var post_text             = ""
    var single_pre_text       = ""
    var single_complete_text  = ""
    var single_post_text      = ""

    var precision = 2

    var show_progress = true
    var show_start    = true
    var show_end      = true

    var show_years   = true
    var show_months  = true
    var show_weeks   = true
    var show_days    = true
    var show_hours   = true
    var show_minutes = true
    var show_seconds = true

    var terminate    = true
    var notify_start = true
    var notify_end   = true

    // default ctor
    constructor(context: Context): this()
    {
        val start_time_cal = Calendar.getInstance()
        val end_time_cal = start_time_cal.clone() as Calendar
        end_time_cal.add(Calendar.MINUTE, 1)

        start_time = start_time_cal.timeInMillis / 1000L
        end_time   = end_time_cal.timeInMillis / 1000L
        start_tz   = start_time_cal.timeZone.id
        end_tz     = end_time_cal.timeZone.id

        title                = context.resources.getString(R.string.default_title)
        pre_text             = context.resources.getString(R.string.default_pre_text)
        start_text           = context.resources.getString(R.string.default_start_text)
        countdown_text       = context.resources.getString(R.string.default_countdown_text)
        complete_text        = context.resources.getString(R.string.default_complete_text)
        post_text            = context.resources.getString(R.string.default_post_text)
        single_pre_text      = context.resources.getString(R.string.default_single_pre_text)
        single_complete_text = context.resources.getString(R.string.default_single_complete_text)
        single_post_text     = context.resources.getString(R.string.default_single_post_text)

    }

    // set all fields from DB cursor
    // construct from a DB cursor
    constructor(cursor: Cursor): this()
    {
        set_from_cursor(cursor)
    }

    // get data from DB given rowid
    constructor(context: Context, rowid_in: Long): this()
    {
        val db = DB(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM ${Progress_bars_table.TABLE_NAME} WHERE ${BaseColumns._ID} = ?", arrayOf(rowid_in.toString()))
        cursor.moveToFirst()

        set_from_cursor(cursor)

        cursor.close()
        db.close()
    }

    // trivial copy ctor. Because, although we could call this a data class and have this auto generated, Kotlin won't let us inherit from it
    constructor(b: Data): this()
    {
        rowid                = b.rowid
        separate_time        = b.separate_time
        start_time           = b.start_time
        end_time             = b.end_time
        start_tz             = b.start_tz
        end_tz               = b.end_tz
        repeats              = b.repeats
        repeat_count         = b.repeat_count
        repeat_unit          = b.repeat_unit
        repeat_days_of_week  = b.repeat_days_of_week
        title                = b.title
        pre_text             = b.pre_text
        start_text           = b.start_text
        countdown_text       = b.countdown_text
        complete_text        = b.complete_text
        post_text            = b.post_text
        single_pre_text      = b.single_pre_text
        single_complete_text = b.single_complete_text
        single_post_text     = b.single_post_text
        precision            = b.precision
        show_progress        = b.show_progress
        show_start           = b.show_start
        show_end             = b.show_end
        show_years           = b.show_years
        show_months          = b.show_months
        show_weeks           = b.show_weeks
        show_days            = b.show_days
        show_hours           = b.show_hours
        show_minutes         = b.show_minutes
        show_seconds         = b.show_seconds
        terminate            = b.terminate
        notify_start         = b.notify_start
        notify_end           = b.notify_end
    }

    private fun set_from_cursor(cursor: Cursor)
    {
        rowid                = cursor.get_nullable_long(BaseColumns._ID)!!
        separate_time        = cursor.get_nullable_bool(Progress_bars_table.SEPARATE_TIME_COL)!!
        start_time           = cursor.get_nullable_long(Progress_bars_table.START_TIME_COL)!!
        end_time             = cursor.get_nullable_long(Progress_bars_table.END_TIME_COL)!!
        start_tz             = cursor.get_nullable_string(Progress_bars_table.START_TZ_COL)!!
        end_tz               = cursor.get_nullable_string(Progress_bars_table.END_TZ_COL)!!
        repeats              = cursor.get_nullable_bool(Progress_bars_table.REPEATS_COL)!!
        repeat_count         = cursor.get_nullable_int(Progress_bars_table.REPEAT_COUNT_COL)!!
        repeat_unit          = cursor.get_nullable_int(Progress_bars_table.REPEAT_UNIT_COL)!!
        repeat_days_of_week  = cursor.get_nullable_int(Progress_bars_table.REPEAT_DAYS_OF_WEEK_COL)!!
        title                = cursor.get_nullable_string(Progress_bars_table.TITLE_COL)!!
        pre_text             = cursor.get_nullable_string(Progress_bars_table.PRE_TEXT_COL)!!
        start_text           = cursor.get_nullable_string(Progress_bars_table.START_TEXT_COL)!!
        countdown_text       = cursor.get_nullable_string(Progress_bars_table.COUNTDOWN_TEXT_COL)!!
        complete_text        = cursor.get_nullable_string(Progress_bars_table.COMPLETE_TEXT_COL)!!
        post_text            = cursor.get_nullable_string(Progress_bars_table.POST_TEXT_COL)!!
        single_pre_text      = cursor.get_nullable_string(Progress_bars_table.SINGLE_PRE_TEXT_COL)!!
        single_complete_text = cursor.get_nullable_string(Progress_bars_table.SINGLE_COMPLETE_TEXT_COL)!!
        single_post_text     = cursor.get_nullable_string(Progress_bars_table.SINGLE_POST_TEXT_COL)!!
        precision            = cursor.get_nullable_int(Progress_bars_table.PRECISION_COL)!!
        show_progress        = cursor.get_nullable_bool(Progress_bars_table.SHOW_PROGRESS_COL)!!
        show_start           = cursor.get_nullable_bool(Progress_bars_table.SHOW_START_COL)!!
        show_end             = cursor.get_nullable_bool(Progress_bars_table.SHOW_END_COL)!!
        show_years           = cursor.get_nullable_bool(Progress_bars_table.SHOW_YEARS_COL)!!
        show_months          = cursor.get_nullable_bool(Progress_bars_table.SHOW_MONTHS_COL)!!
        show_weeks           = cursor.get_nullable_bool(Progress_bars_table.SHOW_WEEKS_COL)!!
        show_days            = cursor.get_nullable_bool(Progress_bars_table.SHOW_DAYS_COL)!!
        show_hours           = cursor.get_nullable_bool(Progress_bars_table.SHOW_HOURS_COL)!!
        show_minutes         = cursor.get_nullable_bool(Progress_bars_table.SHOW_MINUTES_COL)!!
        show_seconds         = cursor.get_nullable_bool(Progress_bars_table.SHOW_SECONDS_COL)!!
        terminate            = cursor.get_nullable_bool(Progress_bars_table.TERMINATE_COL)!!
        notify_start         = cursor.get_nullable_bool(Progress_bars_table.NOTIFY_START_COL)!!
        notify_end           = cursor.get_nullable_bool(Progress_bars_table.NOTIFY_END_COL)!!
    }

    private fun build_ContentValues(): ContentValues
    {
        val values = ContentValues()

        values.put(Progress_bars_table.SEPARATE_TIME_COL, separate_time)
        values.put(Progress_bars_table.START_TIME_COL, start_time)
        values.put(Progress_bars_table.END_TIME_COL, end_time)
        values.put(Progress_bars_table.START_TZ_COL, start_tz)
        values.put(Progress_bars_table.END_TZ_COL, end_tz)
        values.put(Progress_bars_table.REPEATS_COL, repeats)
        values.put(Progress_bars_table.REPEAT_COUNT_COL, repeat_count)
        values.put(Progress_bars_table.REPEAT_UNIT_COL, repeat_unit)
        values.put(Progress_bars_table.REPEAT_DAYS_OF_WEEK_COL, repeat_days_of_week)
        values.put(Progress_bars_table.TITLE_COL, title)
        values.put(Progress_bars_table.PRE_TEXT_COL, pre_text)
        values.put(Progress_bars_table.START_TEXT_COL, start_text)
        values.put(Progress_bars_table.COUNTDOWN_TEXT_COL, countdown_text)
        values.put(Progress_bars_table.COMPLETE_TEXT_COL, complete_text)
        values.put(Progress_bars_table.POST_TEXT_COL, post_text)
        values.put(Progress_bars_table.SINGLE_PRE_TEXT_COL, single_pre_text)
        values.put(Progress_bars_table.SINGLE_COMPLETE_TEXT_COL, single_complete_text)
        values.put(Progress_bars_table.SINGLE_POST_TEXT_COL, single_post_text)
        values.put(Progress_bars_table.PRECISION_COL, precision)
        values.put(Progress_bars_table.SHOW_START_COL, show_start)
        values.put(Progress_bars_table.SHOW_END_COL, show_end)
        values.put(Progress_bars_table.SHOW_PROGRESS_COL, show_progress)
        values.put(Progress_bars_table.SHOW_YEARS_COL, show_years)
        values.put(Progress_bars_table.SHOW_MONTHS_COL, show_months)
        values.put(Progress_bars_table.SHOW_WEEKS_COL, show_weeks)
        values.put(Progress_bars_table.SHOW_DAYS_COL, show_days)
        values.put(Progress_bars_table.SHOW_HOURS_COL, show_hours)
        values.put(Progress_bars_table.SHOW_MINUTES_COL, show_minutes)
        values.put(Progress_bars_table.SHOW_SECONDS_COL, show_seconds)
        values.put(Progress_bars_table.TERMINATE_COL, terminate)
        values.put(Progress_bars_table.NOTIFY_START_COL, notify_start)
        values.put(Progress_bars_table.NOTIFY_END_COL, notify_end)

        return values
    }

    fun insert(db: SQLiteDatabase, order_ind: Long)
    {
        val values = build_ContentValues()

        if(rowid > 0)
            values.put(BaseColumns._ID, rowid)

        values.put(Progress_bars_table.ORDER_COL, order_ind)
        rowid = db.insert(Progress_bars_table.TABLE_NAME, null, values)
    }

    // if repeat is set, update start and end times as needed
    fun apply_repeat()
    {
        Log.d("apply_repeat", "$rowid")
        if(!repeats)
            return

        val now_s = System.currentTimeMillis() / 1000L

        while(now_s >= if(separate_time) end_time else start_time)
        {
            // convert to calendar, add month/year, convert back
            val start_cal = Calendar.getInstance(TimeZone.getTimeZone(start_tz))
            val end_cal = Calendar.getInstance(TimeZone.getTimeZone(end_tz))

            start_cal.timeInMillis = start_time * 1000
            if(separate_time)
                end_cal.timeInMillis = end_time * 1000

            if(repeat_unit == Progress_bars_table.Unit.SECOND.index)
            {
                start_cal.add(Calendar.SECOND, repeat_count)
                if(separate_time)
                    end_cal.add(Calendar.SECOND, repeat_count)
            }
            else if(repeat_unit == Progress_bars_table.Unit.MINUTE.index)
            {
                start_cal.add(Calendar.MINUTE, repeat_count)
                if(separate_time)
                    end_cal.add(Calendar.MINUTE, repeat_count)
            }
            else if(repeat_unit == Progress_bars_table.Unit.HOUR.index)
            {
                start_cal.add(Calendar.HOUR, repeat_count)
                if(separate_time)
                    end_cal.add(Calendar.HOUR, repeat_count)
            }
            else if(repeat_unit == Progress_bars_table.Unit.DAY.index)
            {
                start_cal.add(Calendar.DAY_OF_MONTH, repeat_count)
                if(separate_time)
                    end_cal.add(Calendar.DAY_OF_MONTH, repeat_count)
            }
            else if(repeat_unit == Progress_bars_table.Unit.WEEK.index)
            {
                if(repeat_days_of_week != 0)
                {
                    var day_of_week = start_cal.get(Calendar.DAY_OF_WEEK) - 1
                    var increment_days = 0
                    do
                    {
                        ++increment_days
                        if(++day_of_week >= 7)
                        {
                            increment_days += 7 * (repeat_count - 1)
                            day_of_week = 0
                        }
                    }
                    while(repeat_days_of_week and (1 shl day_of_week) == 0)

                    start_cal.add(Calendar.DAY_OF_MONTH, increment_days)
                    if(separate_time)
                        end_cal.add(Calendar.DAY_OF_MONTH, increment_days)
                }
            }
            else if(repeat_unit == Progress_bars_table.Unit.MONTH.index)
            {
                start_cal.add(Calendar.MONTH, repeat_count)
                if(separate_time)
                    end_cal.add(Calendar.MONTH, repeat_count)
            }
            else if(repeat_unit == Progress_bars_table.Unit.YEAR.index)
            {
                start_cal.add(Calendar.YEAR, repeat_count)
                if(separate_time)
                    end_cal.add(Calendar.YEAR, repeat_count)
            }

            start_time = start_cal.timeInMillis / 1000
            if(separate_time)
                end_time = end_cal.timeInMillis / 1000
        }
    }

    companion object
    {
        // only to be run on boot
        fun apply_all_repeats(context: Context)
        {
            val db = DB(context).writableDatabase
            val cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null)

            // for every timer
            for(i in 0 until cursor.count)
            {
                cursor.moveToPosition(i)
                val data = Data(cursor)

                data.apply_repeat()
                db.update(Progress_bars_table.TABLE_NAME, data.build_ContentValues(), BaseColumns._ID + " = ?", arrayOf(data.rowid.toString()))
            }
            cursor.close()
            db.close()
        }
    }
}
