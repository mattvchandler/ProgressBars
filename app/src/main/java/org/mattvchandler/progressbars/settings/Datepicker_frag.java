package org.mattvchandler.progressbars.settings;

import android.app.DatePickerDialog;
import android.app.Dialog;
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

// date picker dialog
public class Datepicker_frag extends DialogFragment
{
    public static final String STORE_DATE = "STORE_DATE";
    public static final String DATE = "DATE";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle saved_instance_state)
    {
        // parse from current widget text
        int year, month, day;

        Calendar cal = Calendar.getInstance();

        String date_format = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getString("date_format", getResources().getString(R.string.pref_date_format_default));

        //noinspection ConstantConditions
        String date = getArguments().getString(DATE);
        if(date == null)
            throw new InvalidParameterException("No date argument given");

        SimpleDateFormat df = new SimpleDateFormat(date_format, Locale.US);
        Date date_obj = df.parse(date, new ParsePosition(0));
        if(date_obj == null)
        {
            // couldn't parse
            Toast.makeText(getActivity(), getResources().getString(R.string.invalid_date, date, date_format),
                    Toast.LENGTH_LONG).show();

            // set to stored date
            cal.setTimeInMillis(getArguments().getLong(STORE_DATE, 0) * 1000);
        }
        else
        {
            cal.setTime(date_obj);
        }

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH);
        day = cal.get(Calendar.DAY_OF_MONTH);

        //noinspection ConstantConditions
        return new DatePickerDialog(getActivity(), (Settings) getActivity(), year, month, day);
    }
}
