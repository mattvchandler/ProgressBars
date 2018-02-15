package org.mattvchandler.progressbars.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

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

// DB Table schema
public class Table implements BaseColumns // TODO: rename
{
    public static final String TABLE_NAME = "progress_bar";

    public static final String ORDER_COL = "order_ind";
    public static final String START_TIME_COL = "start_time";
    public static final String START_TZ_COL = "start_tz";
    public static final String END_TIME_COL = "end_time";
    public static final String END_TZ_COL = "end_tz";

    public static final String REPEATS_COL = "repeats";
    public static final String REPEAT_COUNT_COL = "repeat_count";
    public static final String REPEAT_UNIT_COL = "repeat_unit";
    public static final String REPEAT_DAYS_OF_WEEK_COL = "repeat_days_of_week";

    public static final String TITLE_COL = "title";
    public static final String PRE_TEXT_COL = "pre_text";
    public static final String START_TEXT_COL = "start_text";
    public static final String COUNTDOWN_TEXT_COL = "countdown_text";
    public static final String COMPLETE_TEXT_COL = "complete_text";
    public static final String POST_TEXT_COL = "post_text";

    public static final String PRECISION_COL = "precision";

    public static final String SHOW_START_COL = "show_start";
    public static final String SHOW_END_COL = "show_end";
    public static final String SHOW_PROGRESS_COL = "show_progress";

    public static final String SHOW_YEARS_COL = "show_years";
    public static final String SHOW_MONTHS_COL = "show_months";
    public static final String SHOW_WEEKS_COL = "show_weeks";
    public static final String SHOW_DAYS_COL = "show_days";
    public static final String SHOW_HOURS_COL = "show_hours";
    public static final String SHOW_MINUTES_COL = "show_minutes";
    public static final String SHOW_SECONDS_COL = "show_seconds";

    public static final String TERMINATE_COL = "terminate";
    public static final String NOTIFY_START_COL = "notify_start";
    public static final String NOTIFY_END_COL = "notify_end";

    // Select stmt to get all columns, all rows, ordered by order #
    public static final String SELECT_ALL_ROWS =
            "SELECT * FROM " + TABLE_NAME + " ORDER BY " + ORDER_COL;

    // table schema
    public static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
            _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            ORDER_COL + " INTEGER UNIQUE NOT NULL, " +
            START_TIME_COL + " INTEGER NOT_NULL, " +
            START_TZ_COL + " TEXT NOT NULL, " +
            END_TIME_COL + " INTEGER NOT_NULL, " +
            END_TZ_COL + " TEXT NOT NULL, " +

            REPEATS_COL + " INTEGER NOT NULL, " +
            REPEAT_COUNT_COL + " INTEGER NOT NULL, " +
            REPEAT_UNIT_COL + " INTEGER NOT NULL, " +
            REPEAT_DAYS_OF_WEEK_COL + " INTEGER NOT NULL, " +

            TITLE_COL + " TEXT NOT NULL, " +
            PRE_TEXT_COL + " TEXT, " +
            START_TEXT_COL + " TEXT, " +
            COUNTDOWN_TEXT_COL + " TEXT, " +
            COMPLETE_TEXT_COL + " TEXT, " +
            POST_TEXT_COL + " TEXT, " +

            PRECISION_COL + " INTEGER NOT_NULL, " +

            SHOW_START_COL + " INTEGER NOT_NULL, " +
            SHOW_END_COL + " INTEGER NOT_NULL, " +
            SHOW_PROGRESS_COL + " INTEGER NOT_NULL, " +

            SHOW_YEARS_COL + " INTEGER NOT_NULL, " +
            SHOW_MONTHS_COL + " INTEGER NOT_NULL, " +
            SHOW_WEEKS_COL + " INTEGER NOT_NULL, " +
            SHOW_DAYS_COL + " INTEGER NOT_NULL, " +
            SHOW_HOURS_COL + " INTEGER NOT_NULL, " +
            SHOW_MINUTES_COL + " INTEGER NOT_NULL, " +
            SHOW_SECONDS_COL + " INTEGER NOT_NULL, " +

            TERMINATE_COL + " INTEGER NOT_NULL, " +
            NOTIFY_START_COL + " INTEGER NOT NULL, " +
            NOTIFY_END_COL + " INTEGER NOT NULL)";

    // associated enums

    public enum Days_of_week
    {
        SUNDAY    (0, 0x01),
        MONDAY    (1, 0x02),
        TUESDAY   (2, 0x04),
        WEDNESDAY (3, 0x08),
        THURSDAY  (4, 0x10),
        FRIDAY    (5, 0x20),
        SATURDAY  (6, 0x40);

        public final int index;
        public final int mask;

        Days_of_week(int index, int mask)
        {
            this.index = index;
            this.mask = mask;
        }

        public static int all_days_mask()
        {
            return SUNDAY.mask | MONDAY.mask | TUESDAY.mask | WEDNESDAY.mask | THURSDAY.mask | FRIDAY.mask | SATURDAY.mask;
        }
    }

    public enum Unit
    {
        SECOND(0),
        MINUTE(1),
        HOUR(2),
        DAY(3),
        WEEK(4),
        MONTH(5),
        YEAR(6);

        public final int index;

        Unit(int index)
        {
            this.index = index;
        }
    }

    public static void upgrade(SQLiteDatabase db, int old_version)
    {
        if(old_version == 1)
        {
            Cursor table_exists = db.query("sqlite_master", new String[] {"name"}, "type = 'table' AND name = ?", new String[] {TABLE_NAME}, null, null, null);
            if(table_exists.getCount() == 1)
            {
                db.execSQL("ALTER TABLE " + TABLE_NAME + " RENAME TO TMP_" + TABLE_NAME);
                db.execSQL(CREATE_TABLE);
                db.execSQL("INSERT INTO " + TABLE_NAME +
                    "(" +
                        ORDER_COL + ", " +
                        START_TIME_COL + ", " +
                        END_TIME_COL + ", " +
                        START_TZ_COL + ", " +
                        END_TZ_COL + ", " +
                        REPEATS_COL + ", " +
                        REPEAT_COUNT_COL + ", " +
                        REPEAT_UNIT_COL + ", " +
                        REPEAT_DAYS_OF_WEEK_COL + ", " +
                        TITLE_COL + ", " +
                        PRE_TEXT_COL + ", " +
                        START_TEXT_COL + ", " +
                        COUNTDOWN_TEXT_COL + ", " +
                        COMPLETE_TEXT_COL + ", " +
                        POST_TEXT_COL + ", " +
                        PRECISION_COL + ", " +
                        SHOW_START_COL + ", " +
                        SHOW_END_COL + ", " +
                        SHOW_PROGRESS_COL + ", " +
                        SHOW_YEARS_COL + ", " +
                        SHOW_MONTHS_COL + ", " +
                        SHOW_WEEKS_COL + ", " +
                        SHOW_DAYS_COL + ", " +
                        SHOW_HOURS_COL + ", " +
                        SHOW_MINUTES_COL + ", " +
                        SHOW_SECONDS_COL + ", " +
                        TERMINATE_COL + ", " +
                        NOTIFY_START_COL + ", " +
                        NOTIFY_END_COL +
                    ")" +
                    " SELECT " +
                        ORDER_COL + ", " +
                        START_TIME_COL + ", " +
                        END_TIME_COL + ", " +
                        START_TZ_COL + ", " +
                        END_TZ_COL + ", " +
                        "0, " +
                        "1, " +
                        String.valueOf(Unit.DAY.index)+ ", " +
                        String.valueOf(Days_of_week.all_days_mask()) + ", " +
                        TITLE_COL + ", " +
                        PRE_TEXT_COL + ", " +
                        START_TEXT_COL + ", " +
                        COUNTDOWN_TEXT_COL + ", " +
                        COMPLETE_TEXT_COL + ", " +
                        POST_TEXT_COL + ", " +
                        PRECISION_COL + ", " +
                        SHOW_START_COL + ", " +
                        SHOW_END_COL + ", " +
                        SHOW_PROGRESS_COL + ", " +
                        SHOW_YEARS_COL + ", " +
                        SHOW_MONTHS_COL + ", " +
                        SHOW_WEEKS_COL + ", " +
                        SHOW_DAYS_COL + ", " +
                        SHOW_HOURS_COL + ", " +
                        SHOW_MINUTES_COL + ", " +
                        SHOW_SECONDS_COL + ", " +
                        TERMINATE_COL + ", " +
                        NOTIFY_START_COL + ", " +
                        NOTIFY_END_COL + " " +
                    "FROM TMP_" + TABLE_NAME);

                db.execSQL("DROP TABLE TMP_" + TABLE_NAME);
            }
            else
            {
                db.execSQL(CREATE_TABLE);
            }
            table_exists.close();
        }
        else
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            db.execSQL(CREATE_TABLE);
        }
    }
    // redo order column to remove gaps, etc. Order #s will be sequential, from 0 to count
    public static void cleanup_order(Context context)
    {
        SQLiteDatabase db = new DB(context).getWritableDatabase();
        Cursor cursor = db.rawQuery(SELECT_ALL_ROWS, null);

        ContentValues values = new ContentValues();

        for(int i = 0; i < cursor.getCount(); ++i)
        {
            values.clear();
            values.put(ORDER_COL, i);
            cursor.moveToPosition(i);
            db.update(TABLE_NAME, values, _ID + " = ?", new String[] {cursor.getString(cursor.getColumnIndexOrThrow(_ID))});
        }

        cursor.close();
        db.close();
    }
}
