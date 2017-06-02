package org.mattvchandler.progressbars;

import java.util.Calendar;

public final class Progress_bar_data
{
    // TODO: only a few (if any) need to be exposed after setup
    public Calendar start_time;
    public Calendar end_time;

    public String title;
    public String pre_text;
    public String countdown_text;
    public String complete_text;
    public String post_text;

    public int precision;

    public boolean show_start;
    public boolean show_end;
    public boolean show_progress;

    public boolean show_years;
    public boolean show_months;
    public boolean show_weeks;
    public boolean show_days;
    public boolean show_hours;
    public boolean show_minutes;
    public boolean show_seconds;

    public boolean terminate;

    float percentage;
    String display_text;

    // TODO: do we actually need a ctor once we're using a DB?
    // if so, flesh this out. It's going to be gross
    Progress_bar_data(String title_in, String countdown_text_in)
    {
        title = title_in;
        countdown_text = countdown_text_in;
    }

    // TODO: this is probably a good place to calculate percentage and generate countdown text
}
