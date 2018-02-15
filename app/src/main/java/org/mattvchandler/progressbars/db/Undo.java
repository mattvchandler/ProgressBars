package org.mattvchandler.progressbars.db;

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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Undo extends Table
{
    public static final String TABLE_NAME = "undo";

    public static final String ACTION_COL = "action";
    public static final String UNDO_REDO_COL = "undo_redo";

    public static final String TABLE_ROWID_COL = "table_rowid";
    public static final String SWAP_FROM_POS_COL = "swap_from_pos";
    public static final String SWAP_TO_POS_COL = "swap_to_pos";

    // Select stmt to get all columns, all rows, ordered by order #
    public static final String SELECT_ALL_ROWS =
            "SELECT * FROM " + TABLE_NAME + " ORDER BY " + Table.ORDER_COL;

    private static final String SELECT_NEXT =
            String.format("SELECT * FROM %1$s WHERE %2$s = ? AND %3$s = (SELECT MAX(%3$s) FROM %1$s WHERE %2$s = ?)", TABLE_NAME, UNDO_REDO_COL, _ID);

    // table schema
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                    _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ACTION_COL + " TEXT NOT NULL, " +
                    UNDO_REDO_COL + " TEXT NOT NULL, " +

                    TABLE_ROWID_COL + " INTEGER NOT NULL, " +
                    SWAP_FROM_POS_COL + " INTEGER, " +
                    SWAP_TO_POS_COL + " INTEGER, " +

                    Table.ORDER_COL + " INTEGER, " +
                    Table.START_TIME_COL + " INTEGER, " +
                    Table.START_TZ_COL + " TEXT, " +
                    Table.END_TIME_COL + " INTEGER, " +
                    Table.END_TZ_COL + " TEXT, " +

                    Table.REPEATS_COL + " INTEGER, " +
                    Table.REPEAT_COUNT_COL + " INTEGER, " +
                    Table.REPEAT_UNIT_COL + " INTEGER, " +
                    Table.REPEAT_DAYS_OF_WEEK_COL + " INTEGER, " +

                    Table.TITLE_COL + " TEXT, " +
                    Table.PRE_TEXT_COL + " TEXT, " +
                    Table.START_TEXT_COL + " TEXT, " +
                    Table.COUNTDOWN_TEXT_COL + " TEXT, " +
                    Table.COMPLETE_TEXT_COL + " TEXT, " +
                    Table.POST_TEXT_COL + " TEXT, " +

                    Table.PRECISION_COL + " INTEGER, " +

                    Table.SHOW_START_COL + " INTEGER, " +
                    Table.SHOW_END_COL + " INTEGER, " +
                    Table.SHOW_PROGRESS_COL + " INTEGER, " +

                    Table.SHOW_YEARS_COL + " INTEGER, " +
                    Table.SHOW_MONTHS_COL + " INTEGER, " +
                    Table.SHOW_WEEKS_COL + " INTEGER, " +
                    Table.SHOW_DAYS_COL + " INTEGER, " +
                    Table.SHOW_HOURS_COL + " INTEGER, " +
                    Table.SHOW_MINUTES_COL + " INTEGER, " +
                    Table.SHOW_SECONDS_COL + " INTEGER, " +

                    Table.TERMINATE_COL + " INTEGER, " +
                    Table.NOTIFY_START_COL + " INTEGER, " +
                    Table.NOTIFY_END_COL + " INTEGER)";

    public static void upgrade(SQLiteDatabase db, int old_version)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(CREATE_TABLE);
    }

}
