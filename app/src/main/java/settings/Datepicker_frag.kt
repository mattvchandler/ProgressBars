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

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.preference.PreferenceManager
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.settings.Settings.Companion.get_date_format
import java.security.InvalidParameterException
import java.text.ParsePosition
import java.util.*

// date picker dialog
class Datepicker_frag: DialogFragment()
{
    override fun onCreateDialog(saved_instance_state: Bundle?): Dialog
    {
        // parse from current widget text
        val year: Int
        val month: Int
        val day: Int

        val cal = Calendar.getInstance()

        val preferenceManager = PreferenceManager.getDefaultSharedPreferences(requireActivity())
        val date_format = preferenceManager.getString(resources.getString(R.string.pref_date_format_key), resources.getString(R.string.pref_date_format_default))

        val date = requireArguments().getString(DATE) ?: throw InvalidParameterException("No date argument given")

        val df = get_date_format(requireActivity())

        val date_obj = df.parse(date, ParsePosition(0))
        if(date_obj == null)
        {
            // couldn't parse
            Toast.makeText(activity, resources.getString(R.string.invalid_date, date, if(date_format != "locale") date_format else df.toLocalizedPattern()), Toast.LENGTH_LONG).show()

            // set to stored date
            cal.timeInMillis = requireArguments().getLong(STORE_DATE, 0) * 1000
        }
        else
        {
            cal.time = date_obj
        }

        year = cal.get(Calendar.YEAR)
        month = cal.get(Calendar.MONTH)
        day = cal.get(Calendar.DAY_OF_MONTH)


        val date_picker = DatePickerDialog(requireActivity(), activity as Settings?, year, month, day)

        if(Build.VERSION.SDK_INT >= 21)
        {
            val first_day_of_wk = preferenceManager.getString(resources.getString(R.string.pref_first_day_of_wk_key), resources.getString(R.string.pref_first_day_of_wk_default))!!.toInt()
            if(first_day_of_wk != 0)
                date_picker.datePicker.firstDayOfWeek = first_day_of_wk
        }
        return date_picker
    }

    companion object
    {
        const val STORE_DATE = "STORE_DATE"
        const val DATE = "DATE"
    }
}
