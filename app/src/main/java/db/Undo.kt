package org.mattvchandler.progressbars.db

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

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns

class Undo: Progress_bars_table()
{
    companion object
    {
        const val TABLE_NAME = "undo"

        const val ACTION_COL = "action"
        const val UNDO_REDO_COL = "undo_redo"

        const val TABLE_ROWID_COL = "table_rowid"
        const val SWAP_FROM_POS_COL = "swap_from_pos"
        const val SWAP_TO_POS_COL = "swap_to_pos"

        const val UNDO = "undo"
        const val REDO = "redo"

        private const val SELECT_NEXT = "SELECT * FROM $TABLE_NAME WHERE $UNDO_REDO_COL = ? AND ${BaseColumns._ID} = (SELECT MAX(${BaseColumns._ID}) FROM $TABLE_NAME WHERE $UNDO_REDO_COL = ?)"

        // table schema
        const val CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACTION_COL + " TEXT NOT NULL, " +
                UNDO_REDO_COL + " TEXT NOT NULL, " +

                TABLE_ROWID_COL + " INTEGER NOT NULL, " +
                SWAP_FROM_POS_COL + " INTEGER, " +
                SWAP_TO_POS_COL + " INTEGER, " +

                Progress_bars_table.ORDER_COL + " INTEGER, " +
                Progress_bars_table.START_TIME_COL + " INTEGER, " +
                Progress_bars_table.START_TZ_COL + " TEXT, " +
                Progress_bars_table.END_TIME_COL + " INTEGER, " +
                Progress_bars_table.END_TZ_COL + " TEXT, " +

                Progress_bars_table.REPEATS_COL + " INTEGER, " +
                Progress_bars_table.REPEAT_COUNT_COL + " INTEGER, " +
                Progress_bars_table.REPEAT_UNIT_COL + " INTEGER, " +
                Progress_bars_table.REPEAT_DAYS_OF_WEEK_COL + " INTEGER, " +

                Progress_bars_table.TITLE_COL + " TEXT, " +
                Progress_bars_table.PRE_TEXT_COL + " TEXT, " +
                Progress_bars_table.START_TEXT_COL + " TEXT, " +
                Progress_bars_table.COUNTDOWN_TEXT_COL + " TEXT, " +
                Progress_bars_table.COMPLETE_TEXT_COL + " TEXT, " +
                Progress_bars_table.POST_TEXT_COL + " TEXT, " +

                Progress_bars_table.PRECISION_COL + " INTEGER, " +

                Progress_bars_table.SHOW_START_COL + " INTEGER, " +
                Progress_bars_table.SHOW_END_COL + " INTEGER, " +
                Progress_bars_table.SHOW_PROGRESS_COL + " INTEGER, " +

                Progress_bars_table.SHOW_YEARS_COL + " INTEGER, " +
                Progress_bars_table.SHOW_MONTHS_COL + " INTEGER, " +
                Progress_bars_table.SHOW_WEEKS_COL + " INTEGER, " +
                Progress_bars_table.SHOW_DAYS_COL + " INTEGER, " +
                Progress_bars_table.SHOW_HOURS_COL + " INTEGER, " +
                Progress_bars_table.SHOW_MINUTES_COL + " INTEGER, " +
                Progress_bars_table.SHOW_SECONDS_COL + " INTEGER, " +

                Progress_bars_table.TERMINATE_COL + " INTEGER, " +
                Progress_bars_table.NOTIFY_START_COL + " INTEGER, " +
                Progress_bars_table.NOTIFY_END_COL + " INTEGER)"

        fun upgrade(db: SQLiteDatabase, old_version: Int)
        {
            if(old_version < 3)
            {
                db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
                db.execSQL(CREATE_TABLE)
            }
        }

        private fun data_from_cursor(cursor: Cursor): Data
        {
            val data = Data()

            data.order               = cursor.get_nullable_long(Progress_bars_table.ORDER_COL)              ?: data.order
            data.start_time          = cursor.get_nullable_long(Progress_bars_table.START_TIME_COL)         ?: data.start_time
            data.end_time            = cursor.get_nullable_long(Progress_bars_table.END_TIME_COL)           ?: data.end_time
            data.start_tz            = cursor.get_nullable_string(Progress_bars_table.START_TZ_COL)         ?: data.start_tz
            data.end_tz              = cursor.get_nullable_string(Progress_bars_table.END_TZ_COL)           ?: data.end_tz
            data.repeats             = cursor.get_nullable_bool(Progress_bars_table.REPEATS_COL)            ?: data.repeats
            data.repeat_count        = cursor.get_nullable_int(Progress_bars_table.REPEAT_COUNT_COL)        ?: data.repeat_count
            data.repeat_unit         = cursor.get_nullable_int(Progress_bars_table.REPEAT_UNIT_COL)         ?: data.repeat_unit
            data.repeat_days_of_week = cursor.get_nullable_int(Progress_bars_table.REPEAT_DAYS_OF_WEEK_COL) ?: data.repeat_days_of_week
            data.title               = cursor.get_nullable_string(Progress_bars_table.TITLE_COL)            ?: data.title
            data.pre_text            = cursor.get_nullable_string(Progress_bars_table.PRE_TEXT_COL)         ?: data.pre_text
            data.start_text          = cursor.get_nullable_string(Progress_bars_table.START_TEXT_COL)       ?: data.start_text
            data.countdown_text      = cursor.get_nullable_string(Progress_bars_table.COUNTDOWN_TEXT_COL)   ?: data.countdown_text
            data.complete_text       = cursor.get_nullable_string(Progress_bars_table.COMPLETE_TEXT_COL)    ?: data.complete_text
            data.post_text           = cursor.get_nullable_string(Progress_bars_table.POST_TEXT_COL)        ?: data.post_text
            data.precision           = cursor.get_nullable_int(Progress_bars_table.PRECISION_COL)           ?: data.precision
            data.show_progress       = cursor.get_nullable_bool(Progress_bars_table.SHOW_PROGRESS_COL)      ?: data.show_progress
            data.show_start          = cursor.get_nullable_bool(Progress_bars_table.SHOW_START_COL)         ?: data.show_start
            data.show_end            = cursor.get_nullable_bool(Progress_bars_table.SHOW_END_COL)           ?: data.show_end
            data.show_years          = cursor.get_nullable_bool(Progress_bars_table.SHOW_YEARS_COL)         ?: data.show_years
            data.show_months         = cursor.get_nullable_bool(Progress_bars_table.SHOW_MONTHS_COL)        ?: data.show_months
            data.show_weeks          = cursor.get_nullable_bool(Progress_bars_table.SHOW_WEEKS_COL)         ?: data.show_weeks
            data.show_days           = cursor.get_nullable_bool(Progress_bars_table.SHOW_DAYS_COL)          ?: data.show_days
            data.show_hours          = cursor.get_nullable_bool(Progress_bars_table.SHOW_HOURS_COL)         ?: data.show_hours
            data.show_minutes        = cursor.get_nullable_bool(Progress_bars_table.SHOW_MINUTES_COL)       ?: data.show_minutes
            data.show_seconds        = cursor.get_nullable_bool(Progress_bars_table.SHOW_SECONDS_COL)       ?: data.show_seconds
            data.terminate           = cursor.get_nullable_bool(Progress_bars_table.TERMINATE_COL)          ?: data.terminate
            data.notify_start        = cursor.get_nullable_bool(Progress_bars_table.NOTIFY_START_COL)       ?: data.notify_start
            data.notify_end          = cursor.get_nullable_bool(Progress_bars_table.NOTIFY_END_COL)         ?: data.notify_end

            return data
        }

        fun apply(context: Context, undo_redo: String)
        {
            if(undo_redo != UNDO && undo_redo != REDO)
                throw IllegalArgumentException("undo_redo must be $UNDO or $REDO")

            val inverse_undo_redo = if(undo_redo == UNDO) REDO else UNDO

            val db = DB(context).writableDatabase

            val cursor = db.rawQuery(SELECT_NEXT, arrayOf(undo_redo, undo_redo))
            if(cursor.count == 0)
                return
            cursor.moveToFirst()

            val action = cursor.get_nullable_string(ACTION_COL)!!
            val rowid = cursor.get_nullable_long(TABLE_ROWID_COL)!!

            when(action)
            {
                Data.INSERT ->
                {
                    val data = Data(context, rowid)
                    data.delete(context, inverse_undo_redo)
                }
                Data.UPDATE ->
                {
                    val data = data_from_cursor(cursor)
                    data.rowid = rowid
                    data.update(context, inverse_undo_redo)
                }
                Data.DELETE ->
                {
                    val data = data_from_cursor(cursor)
                    data.rowid = rowid
                    data.insert(context, inverse_undo_redo)
                }
                Data.MOVE ->
                {
                    val data = data_from_cursor(cursor)
                    data.rowid = rowid
                    val from_pos = cursor.get_nullable_int(SWAP_FROM_POS_COL)!!
                    val to_pos = cursor.get_nullable_int(SWAP_TO_POS_COL)!!
                    data.reorder(context, to_pos, from_pos, inverse_undo_redo)
                }
            }

            db.delete(TABLE_NAME, BaseColumns._ID + " = ?", arrayOf(cursor.get_nullable_string(BaseColumns._ID)!!))
            cursor.close()

            db.close()
        }

        private fun can_apply(context: Context, undo_redo: String): Boolean
        {
            val db = DB(context).writableDatabase
            val cursor = db.rawQuery(SELECT_NEXT, arrayOf(undo_redo, undo_redo))
            val count = cursor.count
            cursor.close()
            db.close()

            return count != 0
        }

        fun can_undo(context: Context): Boolean
        {
            return can_apply(context, UNDO)
        }

        fun can_redo(context: Context): Boolean
        {
            return can_apply(context, REDO)
        }

        fun delete_undo_history(context: Context)
        {
            val db = DB(context).writableDatabase
            db.delete(Undo.TABLE_NAME, Undo.UNDO_REDO_COL + " = ?", arrayOf(Undo.UNDO))
            db.close()
        }

        fun delete_redo_history(context: Context)
        {
            val db = DB(context).writableDatabase
            db.delete(Undo.TABLE_NAME, Undo.UNDO_REDO_COL + " = ?", arrayOf(Undo.REDO))
            db.close()
        }
    }
}
