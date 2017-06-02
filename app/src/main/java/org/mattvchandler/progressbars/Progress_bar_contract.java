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
        public static final String END_TIME_COL       = "end_time";

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

        // TODO: Most of these should be NOT NULL
        public static final String CREATE_TABLE =
                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
                _ID                + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ORDER_COL          + " INTEGER NOT NULL, " +
                START_TIME_COL     + " INTEGER, " +
                END_TIME_COL       + " INTEGER, " +

                TITLE_COL          + " TEXT NOT NULL, " +
                PRE_TEXT_COL       + " TEXT, " +
                COUNTDOWN_TEXT_COL + " TEXT, " +
                COMPLETE_TEXT_COL  + " TEXT, " +
                POST_TEXT_COL      + " TEXT, " +

                PRECISION_COL      + " INTEGER, " +

                SHOW_START_COL     + " INTEGER, " +
                SHOW_END_COL       + " INTEGER, " +
                SHOW_PROGRESS_COL  + " INTEGER, " +

                SHOW_YEARS_COL     + " INTEGER, " +
                SHOW_MONTHS_COL    + " INTEGER, " +
                SHOW_WEEKS_COL     + " INTEGER, " +
                SHOW_DAYS_COL      + " INTEGER, " +
                SHOW_HOURS_COL     + " INTEGER, " +
                SHOW_MINUTES_COL   + " INTEGER, " +
                SHOW_SECONDS_COL   + " INTEGER, " +

                TERMINATE_COL      + " INTEGER)";
    }

}
