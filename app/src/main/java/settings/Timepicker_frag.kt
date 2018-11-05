package org.mattvchandler.progressbars.settings

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.DialogFragment
import android.widget.Toast
import org.mattvchandler.progressbars.R
import java.security.InvalidParameterException
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*

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

// time-picker dialog
class Timepicker_frag: DialogFragment()
{

    override fun onCreateDialog(saved_instance_state: Bundle?): Dialog
    {
        // parse from current widget text
        val hour: Int
        val minute: Int

        val cal = Calendar.getInstance()

        val hour_24 = PreferenceManager.getDefaultSharedPreferences(context).getBoolean("hour_24", resources.getBoolean(R.bool.pref_hour_24_default))

        val time_format = resources.getString(if(hour_24) R.string.time_format_24 else R.string.time_format_12)
        val time_format_edit = resources.getString(if(hour_24) R.string.time_format_24 else R.string.time_format_12_edit)

        var time = arguments!!.getString(TIME)
        val am_pm = arguments!!.getString(AM_PM)

        if(time == null)
            throw InvalidParameterException("No time argument given")
        if(am_pm == null)
            throw InvalidParameterException("No am/pm argument given")

        if(!hour_24)
            time += " $am_pm"

        val df = SimpleDateFormat(time_format, Locale.US)
        val date_obj = df.parse(time, ParsePosition(0))
        if(date_obj == null)
        {
            // couldn't parse
            Toast.makeText(activity, resources.getString(R.string.invalid_time, time, time_format_edit),
                    Toast.LENGTH_LONG).show()

            // set to stored date
            cal.timeInMillis = arguments!!.getLong(STORE_TIME, 0) * 1000
        }
        else
        {
            cal.time = date_obj
        }

        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)

        return TimePickerDialog(activity, activity as Settings?, hour, minute, hour_24)
    }

    companion object
    {
        const val STORE_TIME = "STORE_TIME"
        const val TIME = "TIME"
        const val AM_PM = "AM_PM"
    }
}
