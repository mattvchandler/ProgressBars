package org.mattvchandler.progressbars.Settings;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import org.mattvchandler.progressbars.R;

import java.security.InvalidParameterException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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

// time-picker dialog
public class Timepicker_frag extends DialogFragment
{
    public static final String STORE_TIME = "STORE_TIME";
    public static final String TIME = "TIME";
    public static final String AM_PM = "AM_PM";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle saved_instance_state)
    {
        // parse from current widget text
        int hour, minute;

        Calendar cal = Calendar.getInstance();

        boolean hour_24 = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("hour_24", true);

        String time_format = getResources().getString(hour_24 ? R.string.time_format_24 : R.string.time_format_12);
        String time_format_edit = getResources().getString(hour_24 ? R.string.time_format_24 : R.string.time_format_12_edit);

        String time = getArguments().getString(TIME);
        String am_pm = getArguments().getString(AM_PM);

        if(time == null)
            throw new InvalidParameterException("No time argument given");
        if(am_pm == null)
            throw new InvalidParameterException("No am/pm argument given");

        if(!hour_24)
            time += " " + am_pm;

        SimpleDateFormat df = new SimpleDateFormat(time_format, Locale.US);
        Date date_obj = df.parse(time, new ParsePosition(0));
        if(date_obj == null)
        {
            // couldn't parse
            Toast.makeText(getActivity(), getResources().getString(R.string.invalid_time, time, time_format_edit),
                    Toast.LENGTH_LONG).show();

            // set to stored date
            cal.setTimeInMillis(getArguments().getLong(STORE_TIME, 0) * 1000);
        }
        else
        {
            cal.setTime(date_obj);
        }

        hour = cal.get(Calendar.HOUR_OF_DAY);
        minute = cal.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), (Settings) getActivity(), hour, minute, hour_24);
    }
}
