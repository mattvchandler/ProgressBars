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

package org.mattvchandler.progressbars.db

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import android.support.v4.content.LocalBroadcastManager

import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.util.Notification_handler

import java.io.Serializable
import java.util.Calendar
import java.util.TimeZone

// struct w/ copy of all DB columns. Serializable so we can store the whole thing
open class Data: Serializable
{
    var rowid = -1L // is -1 when not set, ie. the data doesn't exist in the DB

    var order      = -1L // -1 until set
    var start_time = 0L
    var end_time   = 0L

    var start_tz = ""
    var end_tz   = ""

    var repeats             = false
    var repeat_count        = 1
    var repeat_unit         = Progress_bars_table.Unit.DAY.index
    var repeat_days_of_week = Progress_bars_table.Days_of_week.all_days_mask()

    var title          = ""
    var pre_text       = ""
    var start_text     = ""
    var countdown_text = ""
    var complete_text  = ""
    var post_text      = ""

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
    constructor(context: Context)
    {
        val start_time_cal = Calendar.getInstance()
        val end_time_cal = start_time_cal.clone() as Calendar
        end_time_cal.add(Calendar.MINUTE, 1)

        start_time          = start_time_cal.timeInMillis / 1000L
        end_time            = end_time_cal.timeInMillis / 1000L
        start_tz            = start_time_cal.timeZone.id
        end_tz              = end_time_cal.timeZone.id

        title          = context.resources.getString(R.string.default_title)
        pre_text       = context.resources.getString(R.string.default_pre_text)
        start_text     = context.resources.getString(R.string.default_start_text)
        countdown_text = context.resources.getString(R.string.default_countdown_text)
        complete_text  = context.resources.getString(R.string.default_complete_text)
        post_text      = context.resources.getString(R.string.default_post_text)

    }

    // set all fields from DB cursor
    // construct from a DB cursor
    constructor(cursor: Cursor)
    {
        set_from_cursor(cursor)
    }

    // get data from DB given rowid
    constructor(context: Context, rowid_in: Long)
    {
        val db = DB(context).readableDatabase
        val cursor = db.rawQuery("SELECT * FROM " + Progress_bars_table.TABLE_NAME + " WHERE " + BaseColumns._ID + " = ?", arrayOf(rowid_in.toString()))
        cursor.moveToFirst()

        set_from_cursor(cursor)

        cursor.close()
        db.close()
    }

    // verbose ctor
    constructor(
            order: Long,
            start_time: Long,
            end_time: Long,
            start_tz: String,
            end_tz: String,
            repeats: Boolean,
            repeat_count: Int,
            repeat_unit: Int,
            repeat_days_of_week: Int,
            title: String,
            pre_text: String,
            start_text: String,
            countdown_text: String,
            complete_text: String,
            post_text: String,
            precision: Int,
            show_progress: Boolean,
            show_start: Boolean,
            show_end: Boolean,
            show_years: Boolean,
            show_months: Boolean,
            show_weeks: Boolean,
            show_days: Boolean,
            show_hours: Boolean,
            show_minutes: Boolean,
            show_seconds: Boolean,
            terminate: Boolean,
            notify_start: Boolean,
            notify_end: Boolean)
    {
        this.rowid               = -1
        this.order               = order
        this.start_time          = start_time
        this.end_time            = end_time
        this.start_tz            = start_tz
        this.end_tz              = end_tz
        this.repeats             = repeats
        this.repeat_count        = repeat_count
        this.repeat_unit         = repeat_unit
        this.repeat_days_of_week = repeat_days_of_week
        this.title               = title
        this.pre_text            = pre_text
        this.start_text          = start_text
        this.countdown_text      = countdown_text
        this.complete_text       = complete_text
        this.post_text           = post_text
        this.precision           = precision
        this.show_progress       = show_progress
        this.show_start          = show_start
        this.show_end            = show_end
        this.show_years          = show_years
        this.show_months         = show_months
        this.show_weeks          = show_weeks
        this.show_days           = show_days
        this.show_hours          = show_hours
        this.show_minutes        = show_minutes
        this.show_seconds        = show_seconds
        this.terminate           = terminate
        this.notify_start        = notify_start
        this.notify_end          = notify_end
    }

    // trivial copy ctor. Because, although we could call this a data class and have this auto generated, Kotlin won't let us inherit from it
    constructor(b: Data)
    {
        rowid               = b.rowid
        order               = b.order
        start_time          = b.start_time
        end_time            = b.end_time
        start_tz            = b.start_tz
        end_tz              = b.end_tz
        repeats             = b.repeats
        repeat_count        = b.repeat_count
        repeat_unit         = b.repeat_unit
        repeat_days_of_week = b.repeat_days_of_week
        title               = b.title
        pre_text            = b.pre_text
        start_text          = b.start_text
        countdown_text      = b.countdown_text
        complete_text       = b.complete_text
        post_text           = b.post_text
        precision           = b.precision
        show_progress       = b.show_progress
        show_start          = b.show_start
        show_end            = b.show_end
        show_years          = b.show_years
        show_months         = b.show_months
        show_weeks          = b.show_weeks
        show_days           = b.show_days
        show_hours          = b.show_hours
        show_minutes        = b.show_minutes
        show_seconds        = b.show_seconds
        terminate           = b.terminate
        notify_start        = b.notify_start
        notify_end          = b.notify_end
    }

    private fun set_from_cursor(cursor: Cursor)
    {
        rowid               = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        order               = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table.ORDER_COL))
        start_time          = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table.START_TIME_COL))
        end_time            = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table.END_TIME_COL))
        start_tz            = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.START_TZ_COL))
        end_tz              = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.END_TZ_COL))
        repeats             = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.REPEATS_COL)) > 0
        repeat_count        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.REPEAT_COUNT_COL))
        repeat_unit         = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.REPEAT_UNIT_COL))
        repeat_days_of_week = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.REPEAT_DAYS_OF_WEEK_COL))
        title               = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.TITLE_COL))
        pre_text            = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.PRE_TEXT_COL))
        start_text          = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.START_TEXT_COL))
        countdown_text      = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.COUNTDOWN_TEXT_COL))
        complete_text       = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.COMPLETE_TEXT_COL))
        post_text           = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bars_table.POST_TEXT_COL))
        precision           = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.PRECISION_COL))
        show_progress       = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_PROGRESS_COL)) > 0
        show_start          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_START_COL))    > 0
        show_end            = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_END_COL))      > 0
        show_years          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_YEARS_COL))    > 0
        show_months         = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_MONTHS_COL))   > 0
        show_weeks          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_WEEKS_COL))    > 0
        show_days           = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_DAYS_COL))     > 0
        show_hours          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_HOURS_COL))    > 0
        show_minutes        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_MINUTES_COL))  > 0
        show_seconds        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.SHOW_SECONDS_COL))  > 0
        terminate           = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.TERMINATE_COL))     > 0
        notify_start        = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.NOTIFY_START_COL))  > 0
        notify_end          = cursor.getInt(cursor.getColumnIndexOrThrow(Progress_bars_table.NOTIFY_END_COL))    > 0
    }

    private fun build_ContentValues(): ContentValues
    {
        val values = ContentValues()

        values.put(Progress_bars_table.ORDER_COL, order)
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

    // insert data into the DB. rowid must not be set
    // if order is not set, it will be placed at the bottom
    fun insert(context: Context)
    {
        insert(context, Undo.UNDO)
        Undo.delete_redo_history(context)
    }

    fun insert(context: Context, undo_redo: String)
    {
        apply_repeat()

        val db = DB(context).writableDatabase

        if(order < 0)
        {
            // get next available order #
            val cursor = db.rawQuery("SELECT MAX(" + Progress_bars_table.ORDER_COL + ") + 1 FROM " + Progress_bars_table.TABLE_NAME, null)
            cursor.moveToFirst()
            order = cursor.getLong(0)
            cursor.close()
        }

        val values = build_ContentValues()
        if(rowid > 0)
            values.put(BaseColumns._ID, rowid)
        rowid = db.insert(Progress_bars_table.TABLE_NAME, null, values)

        val undo_columns = ContentValues()
        undo_columns.put(Undo.ACTION_COL, INSERT)
        undo_columns.put(Undo.UNDO_REDO_COL, undo_redo)
        undo_columns.put(Undo.TABLE_ROWID_COL, rowid)
        db.insert(Undo.TABLE_NAME, null, undo_columns)

        db.close()

        Notification_handler.reset_alarm(context, this)

        val intent = Intent(DB_CHANGED_EVENT)
        intent.putExtra(DB_CHANGED_TYPE, INSERT)
        intent.putExtra(DB_CHANGED_ROWID, rowid)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    // update the DB with new data
    // rowid must be set
    fun update(context: Context)
    {
        update(context, Undo.UNDO)
        Undo.delete_redo_history(context)
    }

    fun update(context: Context, undo_redo: String)
    {
        if(rowid < 0)
            throw IllegalStateException("Tried to update when rowid isn't set")

        apply_repeat()

        val db = DB(context).writableDatabase

        val old_data = Data(context, rowid)
        val undo_columns = old_data.build_ContentValues()
        undo_columns.put(Undo.ACTION_COL, UPDATE)
        undo_columns.put(Undo.UNDO_REDO_COL, undo_redo)
        undo_columns.put(Undo.TABLE_ROWID_COL, rowid)
        db.insert(Undo.TABLE_NAME, null, undo_columns)

        db.update(Progress_bars_table.TABLE_NAME, build_ContentValues(), BaseColumns._ID + " = ?", arrayOf(rowid.toString()))
        db.close()

        Notification_handler.reset_alarm(context, this)

        val intent = Intent(DB_CHANGED_EVENT)
        intent.putExtra(DB_CHANGED_TYPE, UPDATE)
        intent.putExtra(DB_CHANGED_ROWID, rowid)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }

    // delete from DB
    // rowid must be set, and will be unset after deletion
    fun delete(context: Context)
    {
        delete(context, Undo.UNDO)
        Undo.delete_redo_history(context)
    }

    fun delete(context: Context, undo_redo: String)
    {
        if(rowid < 0)
            throw IllegalStateException("Tried to delete when rowid isn't set")

        Notification_handler.cancel_alarm(context, this)

        val db = DB(context).writableDatabase

        val undo_columns = build_ContentValues()
        undo_columns.put(Undo.ACTION_COL, DELETE)
        undo_columns.put(Undo.UNDO_REDO_COL, undo_redo)
        undo_columns.put(Undo.TABLE_ROWID_COL, rowid)
        db.insert(Undo.TABLE_NAME, null, undo_columns)

        db.delete(Progress_bars_table.TABLE_NAME,
                BaseColumns._ID + " = ?",
                arrayOf(rowid.toString()))
        db.close()

        val intent = Intent(DB_CHANGED_EVENT)
        intent.putExtra(DB_CHANGED_TYPE, DELETE)
        intent.putExtra(DB_CHANGED_ROWID, rowid)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        rowid = -1 // unset rowid
    }

    fun reorder(context: Context, from_pos: Int, to_pos: Int)
    {
        reorder(context, from_pos, to_pos, Undo.UNDO)
        Undo.delete_redo_history(context)
    }

    fun reorder(context: Context, from_pos: Int, to_pos: Int, undo_redo: String)
    {
        if(from_pos == to_pos)
            return
        val db = DB(context).writableDatabase
        val cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null)

        var to_order = -1L

        if(from_pos < to_pos)
        {
            for(i in from_pos..to_pos)
                to_order = shift_row(i, to_order, cursor, db)

        }
        else
        // from_pos > to_pos
        {
            var i = from_pos + 1
            while(i-- > to_pos)
                to_order = shift_row(i, to_order, cursor, db)
        }
        cursor.close()

        val values = ContentValues()
        values.put(Progress_bars_table.ORDER_COL, to_order)
        db.update(Progress_bars_table.TABLE_NAME, values, BaseColumns._ID + " = ?", arrayOf(rowid.toString()))

        val undo_columns = ContentValues()
        undo_columns.put(Undo.ACTION_COL, MOVE)
        undo_columns.put(Undo.UNDO_REDO_COL, undo_redo)
        undo_columns.put(Undo.TABLE_ROWID_COL, rowid)
        undo_columns.put(Undo.SWAP_FROM_POS_COL, from_pos)
        undo_columns.put(Undo.SWAP_TO_POS_COL, to_pos)
        db.insert(Undo.TABLE_NAME, null, undo_columns)
        db.close()

        val intent = Intent(DB_CHANGED_EVENT)
        intent.putExtra(DB_CHANGED_TYPE, MOVE)
        intent.putExtra(DB_CHANGED_FROM_POS, from_pos)
        intent.putExtra(DB_CHANGED_TO_POS, to_pos)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)

        order = to_order
    }

    // if repeat is set, update start and end times as needed
    private fun apply_repeat()
    {
        if(!repeats)
            return

        val now_s = System.currentTimeMillis() / 1000L

        while(now_s >= end_time)
        {
            // convert to calendar, add month/year, convert back
            val start_cal = Calendar.getInstance(TimeZone.getTimeZone(start_tz))
            val end_cal = Calendar.getInstance(TimeZone.getTimeZone(start_tz))

            start_cal.timeInMillis = start_time * 1000
            end_cal.timeInMillis = end_time * 1000

            if(repeat_unit == Progress_bars_table.Unit.SECOND.index)
            {
                start_cal.add(Calendar.SECOND, repeat_count)
                end_cal.add(Calendar.SECOND, repeat_count)
            }
            else if(repeat_unit == Progress_bars_table.Unit.MINUTE.index)
            {
                start_cal.add(Calendar.MINUTE, repeat_count)
                end_cal.add(Calendar.MINUTE, repeat_count)
            }
            else if(repeat_unit == Progress_bars_table.Unit.HOUR.index)
            {
                start_cal.add(Calendar.HOUR, repeat_count)
                end_cal.add(Calendar.HOUR, repeat_count)
            }
            else if(repeat_unit == Progress_bars_table.Unit.DAY.index)
            {
                start_cal.add(Calendar.DAY_OF_MONTH, repeat_count)
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
                    end_cal.add(Calendar.DAY_OF_MONTH, increment_days)
                }
            }
            else if(repeat_unit == Progress_bars_table.Unit.MONTH.index)
            {
                start_cal.add(Calendar.MONTH, repeat_count)
                end_cal.add(Calendar.MONTH, repeat_count)
            }
            else if(repeat_unit == Progress_bars_table.Unit.YEAR.index)
            {
                start_cal.add(Calendar.YEAR, repeat_count)
                end_cal.add(Calendar.YEAR, repeat_count)
            }

            start_time = start_cal.timeInMillis / 1000
            end_time = end_cal.timeInMillis / 1000
        }
    }

    companion object
    {
        const val DB_CHANGED_EVENT    = "Data.DB_CHANGED_EVENT"
        const val DB_CHANGED_TYPE     = "Data.DB_CHANGED_TYPE"
        const val DB_CHANGED_ROWID    = "Data.DB_CHANGED_ROWID"
        const val DB_CHANGED_FROM_POS = "Data.DB_CHANGED_FROM_POS"
        const val DB_CHANGED_TO_POS   = "Data.DB_CHANGED_TO_POS"

        const val INSERT = "insert"
        const val UPDATE = "update"
        const val DELETE = "delete"
        const val MOVE   = "move"

        fun apply_all_repeats(context: Context)
        {
            val db = DB(context).readableDatabase
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

    private fun shift_row(i: Int, to_order: Long, cursor: Cursor, db: SQLiteDatabase): Long
    {
        cursor.moveToPosition(i)
        val from_order = cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table.ORDER_COL))
        val i_rowid = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))

        val values = ContentValues()
        values.put(Progress_bars_table.ORDER_COL, to_order)
        db.update(Progress_bars_table.TABLE_NAME, values, BaseColumns._ID + " = ?", arrayOf(i_rowid.toString()))

        return from_order
    }
}

