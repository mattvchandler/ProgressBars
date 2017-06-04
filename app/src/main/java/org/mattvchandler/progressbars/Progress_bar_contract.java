package org.mattvchandler.progressbars;

import android.provider.BaseColumns;

public final class Progress_bar_contract
{
    private Progress_bar_contract(){};

    public static class Progress_bar_table implements BaseColumns
    {
        public static final String TABLE_NAME = "progress_bar";

        public static final String ORDER_COL          = "order_ind";
        public static final String START_TIME_COL     = "start_time";
        public static final String START_TZ_COL       = "start_tz";
        public static final String END_TIME_COL       = "end_time";
        public static final String END_TZ_COL         = "end_tz";

        public static final String TITLE_COL          = "title";
        public static final String PRE_TEXT_COL       = "pre_text";
        public static final String COUNTDOWN_TEXT_COL = "countdown_text";
        public static final String COMPLETE_TEXT_COL  = "complete_text";
        public static final String POST_TEXT_COL      = "post_text";

        public static final String PRECISION_COL      = "precision";

        public static final String SHOW_START_COL     = "show_start";
        public static final String SHOW_END_COL       = "show_end";
        public static final String SHOW_PROGRESS_COL  = "show_progress";

        public static final String SHOW_YEARS_COL     = "show_years";
        public static final String SHOW_MONTHS_COL    = "show_months";
        public static final String SHOW_WEEKS_COL     = "show_weeks";
        public static final String SHOW_DAYS_COL      = "show_days";
        public static final String SHOW_HOURS_COL     = "show_hours";
        public static final String SHOW_MINUTES_COL   = "show_minutes";
        public static final String SHOW_SECONDS_COL   = "show_seconds";

        public static final String TERMINATE_COL      = "terminate";
        public static final String NOTIFY_COL         = "notify";

        public static final String SELECT_ALL_ROWS =
                "SELECT * FROM " + TABLE_NAME + " ORDER BY " + ORDER_COL;

        public static final String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                _ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ORDER_COL          + " INTEGER NOT NULL, " +
                START_TIME_COL     + " INTEGER NOT_NULL, " +
                START_TZ_COL       + " TEXT NOT NULL, " +
                END_TIME_COL       + " INTEGER NOT_NULL, " +
                END_TZ_COL         + " TEXT NOT NULL, " +

                TITLE_COL          + " TEXT NOT NULL, " +
                PRE_TEXT_COL       + " TEXT, " +
                COUNTDOWN_TEXT_COL + " TEXT, " +
                COMPLETE_TEXT_COL  + " TEXT, " +
                POST_TEXT_COL      + " TEXT, " +

                PRECISION_COL      + " INTEGER NOT_NULL, " +

                SHOW_START_COL     + " INTEGER NOT_NULL, " +
                SHOW_END_COL       + " INTEGER NOT_NULL, " +
                SHOW_PROGRESS_COL  + " INTEGER NOT_NULL, " +

                SHOW_YEARS_COL     + " INTEGER NOT_NULL, " +
                SHOW_MONTHS_COL    + " INTEGER NOT_NULL, " +
                SHOW_WEEKS_COL     + " INTEGER NOT_NULL, " +
                SHOW_DAYS_COL      + " INTEGER NOT_NULL, " +
                SHOW_HOURS_COL     + " INTEGER NOT_NULL, " +
                SHOW_MINUTES_COL   + " INTEGER NOT_NULL, " +
                SHOW_SECONDS_COL   + " INTEGER NOT_NULL, " +

                TERMINATE_COL      + " INTEGER NOT_NULL," +
                NOTIFY_COL         + " INTEGER NOT NULL)";
    }

}
