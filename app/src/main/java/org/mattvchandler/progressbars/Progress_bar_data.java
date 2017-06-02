package org.mattvchandler.progressbars;

import android.databinding.ObservableField;
import android.os.Handler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

// struct bound to progress_bar_row layout
public final class Progress_bar_data
{
    public final ObservableField<String>  title      = new ObservableField<>();

    public final ObservableField<String>  start_date = new ObservableField<>();
    public final ObservableField<String>  start_time = new ObservableField<>();
    public final ObservableField<String>  end_date   = new ObservableField<>();
    public final ObservableField<String>  end_time   = new ObservableField<>();

    public final ObservableField<String>  percentage = new ObservableField<>();
    public final ObservableField<Integer> progress  = new ObservableField<>();

    public final ObservableField<String>  time_text  = new ObservableField<>();

    private Date start_time_date = new Date();
    private Date end_time_date   = new Date();

    private String pre_text         = new String();
    private String countdown_text   = new String();
    private String complete_text    = new String();
    private String post_text        = new String();

    private Integer precision       = new Integer(2);

    private Boolean show_start      = new Boolean(true);
    private Boolean show_end        = new Boolean(true);
    private Boolean show_progress   = new Boolean(true);

    private Boolean show_years      = new Boolean(true);
    private Boolean show_months     = new Boolean(true);
    private Boolean show_weeks      = new Boolean(true);
    private Boolean show_days       = new Boolean(true);
    private Boolean show_hours      = new Boolean(true);
    private Boolean show_minutes    = new Boolean(true);
    private Boolean show_seconds    = new Boolean(true);

    private Boolean terminate       = new Boolean(true);

    private class Update implements Runnable
    {
        public Handler handler = new Handler();
        public int delay = 1000; // 1000ms
        public void run()
        {
            int progress_data = (progress.get() + 1) % 100;
            progress.set(progress_data);
            percentage.set(String.valueOf(progress_data) + ".00%");

            handler.postDelayed(this, delay);

            time_text.set(countdown_text);
        }
    }

    Update updater = new Update();

    Progress_bar_data(long posix_start_time_in,
                      long posix_end_time_in,
                      String title_in,
                      String pre_text_in,
                      String countdown_text_in,
                      String complete_text_in,
                      String post_text_in,
                      Integer precision_in,
                      Boolean show_start_in,
                      Boolean show_end_in,
                      Boolean show_progress_in,
                      Boolean show_years_in,
                      Boolean show_months_in,
                      Boolean show_weeks_in,
                      Boolean show_days_in,
                      Boolean show_hours_in,
                      Boolean show_minutes_in,
                      Boolean show_seconds_in,
                      Boolean terminate_in)
    {
        start_time_date.setTime(posix_start_time_in * 1000L);
        end_time_date.setTime(posix_end_time_in * 1000L);

        title.set(title_in);

        pre_text = pre_text_in;
        countdown_text = countdown_text_in;
        complete_text = complete_text_in;
        post_text = post_text_in;

        precision = precision_in;

        show_start = show_start_in;
        show_end = show_end_in;
        show_progress = show_progress_in;

        show_years = show_years_in;
        show_months = show_months_in;
        show_weeks = show_weeks_in;
        show_days = show_days_in;
        show_hours = show_hours_in;
        show_minutes = show_minutes_in;
        show_seconds = show_seconds_in;

        terminate = terminate_in;

        SimpleDateFormat date_df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        SimpleDateFormat time_df = new SimpleDateFormat("HH:mm:SS", Locale.US);

        start_date.set(date_df.format(start_time_date));
        start_time.set(time_df.format(start_time_date));
        end_date.set(date_df.format(end_time_date));
        end_time.set(time_df.format(end_time_date));

        //percentage.set(new String());
        progress.set(0);
        //time_text.set(new String());

        updater.run();
    }

}
