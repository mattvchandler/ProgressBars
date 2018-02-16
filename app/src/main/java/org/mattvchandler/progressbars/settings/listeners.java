package org.mattvchandler.progressbars.settings;

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

import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.mattvchandler.progressbars.db.Data;
import org.mattvchandler.progressbars.R;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

// listen for changes to date text
class Date_listener implements View.OnFocusChangeListener
{
    private final String date_format;
    private final Data data;

    Date_listener(String date_format, Data data)
    {
        this.date_format = date_format;
        this.data = data;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        // check format when date entry loses focus
        if(!hasFocus)
        {
            // attempt to parse current text
            String new_date = ((EditText)v).getText().toString();
            SimpleDateFormat df = new SimpleDateFormat(date_format , Locale.US);

            Date date = df.parse(new_date, new ParsePosition(0));
            if(date == null)
            {
                // couldn't parse, show message
                Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.invalid_date,
                        new_date, date_format), Toast.LENGTH_LONG).show();

                // replace with old value, so field contains valid data
                if(v.getId() == R.id.start_date_sel)
                {
                    df.setTimeZone(TimeZone.getTimeZone(data.start_tz));
                    ((EditText)v).setText(df.format(new Date(data.start_time * 1000)));
                }
                else if(v.getId() == R.id.end_date_sel)
                {
                    df.setTimeZone(TimeZone.getTimeZone(data.end_tz));
                    ((EditText)v).setText(df.format(new Date(data.end_time * 1000)));
                }
            }
            else
            {
                // new value is valid, set it.
                new_date = df.format(date);
                ((EditText)v).setText(new_date);
            }
        }
    }
}

// listen for changes to time text
class Time_listener implements View.OnFocusChangeListener
{
    private final String time_format_edit;
    private final Data data;

    Time_listener(String time_format_edit, Data data)
    {
        this.time_format_edit = time_format_edit;
        this.data = data;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        // check format when time entry loses focus
        if(!hasFocus)
        {
            // attempt to parse current text
            String new_time = ((EditText)v).getText().toString();
            SimpleDateFormat df = new SimpleDateFormat(time_format_edit, Locale.US);

            Date time = df.parse(new_time, new ParsePosition(0));
            if(time == null)
            {
                // couldn't parse, show message
                Toast.makeText(v.getContext(), v.getContext().getResources().getString(R.string.invalid_time,
                        new_time, time_format_edit),
                        Toast.LENGTH_LONG).show();

                // replace with old value, so field contains valid data
                if(v.getId() == R.id.start_time_sel)
                {
                    df.setTimeZone(TimeZone.getTimeZone(data.start_tz));
                    ((EditText) v).setText(df.format(new Date(data.start_time * 1000)));
                }
                else if(v.getId() == R.id.end_time_sel)
                {
                    df.setTimeZone(TimeZone.getTimeZone(data.end_tz));
                    ((EditText) v).setText(df.format(new Date(data.end_time * 1000)));
                }
            }
            else
            {
                // new value is valid, set it.
                new_time = df.format(time);
                ((EditText) v).setText(new_time);
            }
        }
    }
}

// listen for changes to repeat count & units
class Repeat_count_listener implements View.OnFocusChangeListener
{
    private final Data data;

    Repeat_count_listener(Data data)
    {
        this.data = data;
    }
    @Override
    public void onFocusChange(View v, boolean hasFocus)
    {
        // check value when count loses focus
        if(!hasFocus)
        {
            int count = 0;
            try
            {
                count = Integer.parseInt(((EditText)v).getText().toString());
            }
            catch(NumberFormatException ignored) {}

            if(count <= 0)
            {
                ((EditText)v).setText(String.valueOf(data.repeat_count));
                Toast.makeText(v.getContext(), R.string.invalid_repeat_count, Toast.LENGTH_LONG).show();
            }
        }
    }
}
