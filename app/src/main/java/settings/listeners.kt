/*
Copyright (C) 2020 Matthew Chandler

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

package org.mattvchandler.progressbars.settings

import android.view.View
import android.widget.EditText
import android.widget.Toast
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.settings.Settings.Companion.get_date_format
import org.mattvchandler.progressbars.settings.Settings.Companion.get_time_format
import java.text.ParsePosition
import java.util.*

// listen for changes to date text
internal class Date_listener(private val data: Data): View.OnFocusChangeListener
{
    override fun onFocusChange(v: View, hasFocus: Boolean)
    {
        // check format when date entry loses focus
        if(!hasFocus)
        {
            // attempt to parse current text
            var new_date = (v as EditText).text.toString()
            val df = get_date_format(v.context)

            val date = df.parse(new_date, ParsePosition(0))
            if(date == null)
            {
                // couldn't parse, show message
                Toast.makeText(v.context, v.getContext().resources.getString(R.string.invalid_date,
                        new_date, df.toLocalizedPattern()), Toast.LENGTH_LONG).show()

                // replace with old value, so field contains valid data
                if(v.id == R.id.start_date_sel)
                {
                    df.timeZone = TimeZone.getTimeZone(data.start_tz)
                    v.setText(df.format(Date(data.start_time * 1000)))
                }
                else if(v.id == R.id.end_date_sel)
                {
                    df.timeZone = TimeZone.getTimeZone(data.end_tz)
                    v.setText(df.format(Date(data.end_time * 1000)))
                }
            }
            else
            {
                // new value is valid, set it.
                new_date = df.format(date)
                v.setText(new_date)
            }
        }
    }
}

// listen for changes to time text
internal class Time_listener(private val data: Data): View.OnFocusChangeListener
{
    override fun onFocusChange(v: View, hasFocus: Boolean)
    {
        // check format when time entry loses focus
        if(!hasFocus)
        {
            // attempt to parse current text
            var new_time = (v as EditText).text.toString()
            val df = get_time_format()

            val time = df.parse(new_time, ParsePosition(0))
            if(time == null)
            {
                // couldn't parse, show message
                Toast.makeText(v.getContext(), v.getContext().resources.getString(R.string.invalid_time,
                        new_time, df.toLocalizedPattern()),
                        Toast.LENGTH_LONG).show()

                // replace with old value, so field contains valid data
                if(v.id == R.id.start_time_sel)
                {
                    df.timeZone = TimeZone.getTimeZone(data.start_tz)
                    v.setText(df.format(Date(data.start_time * 1000)))
                }
                else if(v.id == R.id.end_time_sel)
                {
                    df.timeZone = TimeZone.getTimeZone(data.end_tz)
                    v.setText(df.format(Date(data.end_time * 1000)))
                }
            }
            else
            {
                // new value is valid, set it.
                new_time = df.format(time)
                v.setText(new_time)
            }
        }
    }
}

// listen for changes to repeat count & units
internal class Repeat_count_listener(private val data: Data): View.OnFocusChangeListener
{
    override fun onFocusChange(v: View, hasFocus: Boolean)
    {
        // check value when count loses focus
        if(!hasFocus)
        {
            var count = 0
            try
            {
                count = Integer.parseInt((v as EditText).text.toString())
            }
            catch(ignored: NumberFormatException) {}

            if(count <= 0)
            {
                (v as EditText).setText(data.repeat_count.toString())
                Toast.makeText(v.getContext(), R.string.invalid_repeat_count, Toast.LENGTH_LONG).show()
            }
        }
    }
}
