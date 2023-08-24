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

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.settings.Settings.Companion.get_time_format
import java.security.InvalidParameterException
import java.text.ParsePosition
import java.util.*

// time-picker dialog
class Timepicker_frag: DialogFragment()
{

    override fun onCreateDialog(saved_instance_state: Bundle?): Dialog
    {
        // parse from current widget text
        val hour: Int
        val minute: Int

        val cal = Calendar.getInstance()

        val time = requireArguments().getString(TIME) ?: throw InvalidParameterException("No time argument given")

        val df = get_time_format()
        val date_obj = df.parse(time, ParsePosition(0))
        if(date_obj == null)
        {
            // couldn't parse
            Toast.makeText(activity, resources.getString(R.string.invalid_time, time, df.toLocalizedPattern()), Toast.LENGTH_LONG).show()

            // set to stored date
            cal.timeInMillis = requireArguments().getLong(STORE_TIME, 0) * 1000
        }
        else
        {
            cal.time = date_obj
        }

        hour = cal.get(Calendar.HOUR_OF_DAY)
        minute = cal.get(Calendar.MINUTE)

        return TimePickerDialog(activity, activity as Settings?, hour, minute, android.text.format.DateFormat.is24HourFormat(activity))
    }

    companion object
    {
        const val STORE_TIME = "STORE_TIME"
        const val TIME = "TIME"
        const val AM_PM = "AM_PM"
    }
}
