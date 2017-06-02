package org.mattvchandler.progressbars;

import android.databinding.ObservableField;

// struct bound to progress_bar_row layout
public final class Progress_bar_data
{
    public final ObservableField<String> title = new ObservableField<>();

    public final ObservableField<String> start_date = new ObservableField<>();
    public final ObservableField<String> start_time = new ObservableField<>();
    public final ObservableField<String> end_date = new ObservableField<>();
    public final ObservableField<String> end_time = new ObservableField<>();

    public final ObservableField<String> percentage = new ObservableField<>();
    public final ObservableField<Integer> progress = new ObservableField<>();

    public final ObservableField<String> time_text = new ObservableField<>();

    Progress_bar_data(String title_in,
                      String start_date_in,
                      String start_time_in,
                      String end_date_in,
                      String end_time_in,
                      String percentage_in,
                      Integer progress_in,
                      String time_text_in)
    {
        title.set(title_in);
        start_date.set(start_date_in);
        start_time.set(start_time_in);
        end_date.set(end_date_in);
        end_time.set(end_time_in);
        percentage.set(percentage_in);
        progress.set(progress_in);
        time_text.set(time_text_in);
    }
}
