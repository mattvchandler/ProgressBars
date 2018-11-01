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

package org.mattvchandler.progressbars.settings

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.preference.PreferenceManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast

import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.db.Progress_bars_table
import org.mattvchandler.progressbars.util.Dynamic_theme_activity
import org.mattvchandler.progressbars.util.Preferences
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.databinding.ActivitySettingsBinding

import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Settings for each timer
class Settings: Dynamic_theme_activity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var data: Data
    private lateinit var save_data: Data

    private var date_time_dialog_target: Int = 0

    private lateinit var date_format: String
    private lateinit var time_format: String
    private lateinit var time_format_edit: String
    private var hour_24: Boolean = false

    private var array_am_i: Int = 0
    private var array_pm_i: Int = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // we'll reference these a lot, so look them up now
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        date_format = prefs.getString("date_format", resources.getString(R.string.pref_date_format_default))!!
        hour_24 = prefs.getBoolean("hour_24", resources.getBoolean(R.bool.pref_hour_24_default))

        // time_format is the displayed time, time_format_edit is the time in an edittext box
        // for 24-hour time the format, is the same
        // for 12-hour time, the am/pm is dropped from the edit format, and is set with a spinner instead
        time_format = resources.getString(if(hour_24) R.string.time_format_24 else R.string.time_format_12)
        time_format_edit = resources.getString(if(hour_24) R.string.time_format_24 else R.string.time_format_12_edit)

        // show or hide the AM/PM dropdowns as needed
        binding.startAmPm.visibility = if(hour_24) View.GONE else View.VISIBLE
        binding.endAmPm.visibility = if(hour_24) View.GONE else View.VISIBLE

        // set up timezone spinners
        val tz_adapter = ArrayAdapter(this, R.layout.right_aligned_spinner, TimeZone.getAvailableIDs())
        tz_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.startTz.adapter = tz_adapter
        binding.endTz.adapter = tz_adapter

        // only run this on 1st creation
        if(savedInstanceState == null)
        {
            val rowid = intent.getLongExtra(EXTRA_EDIT_ROW_ID, -1)

            // no rowid passed? make a new one
            data = if(rowid < 0)
            {
                setTitle(R.string.add_title)
                Data(this)
            }
            else
            {
                // get data from row
                setTitle(R.string.edit_title)
                Data(this, rowid)
            }
            save_data = Data(data)
        }
        else
        {
            // reload old and current data from save state
            data = savedInstanceState.getSerializable(STATE_DATA) as Data
            save_data = savedInstanceState.getSerializable(STATE_SAVE_DATA) as Data
            date_time_dialog_target = savedInstanceState.getInt(STATE_TARGET)

            if(data.rowid < 0)
                setTitle(R.string.add_title)
            else
                setTitle(R.string.edit_title)
        }

        // set listeners on time and date fields
        binding.startTimeSel.onFocusChangeListener = Time_listener(time_format_edit, data)
        binding.endTimeSel.onFocusChangeListener = Time_listener(time_format_edit, data)

        binding.startDateSel.onFocusChangeListener = Date_listener(date_format, data)
        binding.endDateSel.onFocusChangeListener = Date_listener(date_format, data)

        binding.repeatCount.onFocusChangeListener = Repeat_count_listener(data)

        binding.repeatUnits.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long)
            {
                val week_selected = i == Progress_bars_table.Unit.WEEK.index
                binding.repeatOn.visibility = if(week_selected) View.VISIBLE else View.GONE
                binding.repeatDaysOfWeek.visibility = if(week_selected) View.VISIBLE else View.GONE

                data.repeat_unit = i
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        // populate timezones and set selected values
        binding.data = data

        var found = 0
        for(i in 0 until tz_adapter.count)
        {
            if(tz_adapter.getItem(i) != null)
            {
                if(tz_adapter.getItem(i) == data.start_tz)
                {
                    binding.startTz.setSelection(i)
                    ++found
                }

                if(tz_adapter.getItem(i) == data.end_tz)
                {
                    binding.endTz.setSelection(i)
                    ++found
                }
                if(found == 2)
                    break
            }
        }

        // populate date/time widget values
        val df_date = SimpleDateFormat(date_format, Locale.US)
        val df_time = SimpleDateFormat(time_format_edit, Locale.US)

        val start_date = Date(data.start_time * 1000)
        df_date.timeZone = TimeZone.getTimeZone(data.start_tz)
        df_time.timeZone = TimeZone.getTimeZone(data.start_tz)
        binding.startDateSel.setText(df_date.format(start_date))
        binding.startTimeSel.setText(df_time.format(start_date))

        // find which index in the spinner is am and which is PM
        for(i in 0 until binding.startAmPm.adapter.count)
        {
            val item = binding.startAmPm.adapter.getItem(i) as CharSequence

            if(item == resources.getString(R.string.AM))
                array_am_i = i
            else if(item == resources.getString(R.string.PM))
                array_pm_i = i
        }

        if(!hour_24)
        {
            // get am/pm status
            val start_cal = Calendar.getInstance(TimeZone.getTimeZone(data.start_tz))
            start_cal.time = start_date
            val am_pm = start_cal.get(Calendar.AM_PM)

            if(am_pm == Calendar.AM)
                binding.startAmPm.setSelection(array_am_i)
            else
                binding.startAmPm.setSelection(array_pm_i)
        }

        val end_date = Date(data.end_time * 1000)
        df_date.timeZone = TimeZone.getTimeZone(data.end_tz)
        df_time.timeZone = TimeZone.getTimeZone(data.end_tz)
        binding.endDateSel.setText(df_date.format(end_date))
        binding.endTimeSel.setText(df_time.format(end_date))

        if(!hour_24)
        {
            // get am/pm status
            val end_cal = Calendar.getInstance(TimeZone.getTimeZone(data.end_tz))
            end_cal.time = end_date
            val am_pm = end_cal.get(Calendar.AM_PM)

            if(am_pm == Calendar.AM)
                binding.endAmPm.setSelection(array_am_i)
            else
                binding.endAmPm.setSelection(array_pm_i)
        }

        binding.repeatFreq.visibility = if(data.repeats) View.VISIBLE else View.GONE
        binding.repeatCount.setText(data.repeat_count.toString())
        binding.repeatUnits.setSelection(data.repeat_unit)
        binding.repeatDaysOfWeek.text = get_days_of_week_abbr(this, data.repeat_days_of_week)

        val week_selected = data.repeat_unit == Progress_bars_table.Unit.WEEK.index
        binding.repeatOn.visibility = if(week_selected) View.VISIBLE else View.GONE
        binding.repeatDaysOfWeek.visibility = if(week_selected) View.VISIBLE else View.GONE
    }

    public override fun onResume()
    {
        super.onResume()

        // check for date format change
        val old_date_format = date_format
        val old_time_format = time_format
        val old_hour_24 = hour_24

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        date_format = prefs.getString("date_format", resources.getString(R.string.pref_date_format_default))!!
        hour_24 = prefs.getBoolean("hour_24", resources.getBoolean(R.bool.pref_hour_24_default))

        time_format = resources.getString(if(hour_24) R.string.time_format_24 else R.string.time_format_12)
        time_format_edit = resources.getString(if(hour_24) R.string.time_format_24 else R.string.time_format_12_edit)

        if(old_date_format != date_format)
        {
            // date format has changed. get formatter for old and new formats
            val new_df = SimpleDateFormat(date_format, Locale.US)
            val old_df = SimpleDateFormat(old_date_format, Locale.US)

            var date: String
            var date_obj: Date?

            // parse date as old format, replace w/ new
            date = binding.startDateSel.text.toString()
            date_obj = old_df.parse(date, ParsePosition(0))
            if(date_obj != null)
            {
                date = new_df.format(date_obj)
                binding.startDateSel.setText(date)
            }

            date = binding.endDateSel.text.toString()
            date_obj = old_df.parse(date, ParsePosition(0))
            if(date_obj != null)
            {
                date = new_df.format(date_obj)
                binding.endDateSel.setText(date)
            }
        }

        if(old_hour_24 != hour_24)
        {
            // time format has changed. get formatter for old and new formats
            // NOTE: get disp format for old, edit for new
            val new_df = SimpleDateFormat(time_format_edit, Locale.US)
            val old_df = SimpleDateFormat(old_time_format, Locale.US)

            var time: String
            var time_obj: Date?

            // parse date as old format, replace w/ new
            time = binding.startTimeSel.text.toString()
            // add am/pm (as text) if needed
            if(!old_hour_24)
                time += " " + binding.startAmPm.selectedItem.toString()

            // attempt to parse
            time_obj = old_df.parse(time, ParsePosition(0))
            if(time_obj != null)
            {
                time = new_df.format(time_obj)
                binding.startTimeSel.setText(time)

                // get am/pm and set spinner if needed
                if(!hour_24)
                {
                    val cal = Calendar.getInstance()
                    cal.time = time_obj
                    val am_pm = cal.get(Calendar.AM_PM)
                    binding.startAmPm.setSelection(if(am_pm == Calendar.AM) array_am_i else array_pm_i)
                }
            }

            time = binding.endTimeSel.text.toString()
            // add am/pm if needed
            if(!old_hour_24)
                time += " " + binding.endAmPm.selectedItem.toString()

            // attempt to parse
            time_obj = old_df.parse(time, ParsePosition(0))
            if(time_obj != null)
            {
                time = new_df.format(time_obj)

                // get am/pm and set spinner if needed
                binding.endTimeSel.setText(time)
                if(!hour_24)
                {
                    val cal = Calendar.getInstance()
                    cal.time = time_obj
                    val am_pm = cal.get(Calendar.AM_PM)
                    binding.endAmPm.setSelection(if(am_pm == Calendar.AM) array_am_i else array_pm_i)
                }
            }

            // reset am/pm spinner visibility
            binding.startAmPm.visibility = if(hour_24) View.GONE else View.VISIBLE
            binding.endAmPm.visibility = if(hour_24) View.GONE else View.VISIBLE
        }
    }

    override fun onSaveInstanceState(out: Bundle)
    {
        super.onSaveInstanceState(out)

        // save all data to be restored
        store_widgets_to_data()
        out.putSerializable(STATE_DATA, data)
        out.putSerializable(STATE_SAVE_DATA, save_data)
        out.putInt(STATE_TARGET, date_time_dialog_target)

        // clear listeners
        binding.startTimeSel.onFocusChangeListener = null
        binding.endTimeSel.onFocusChangeListener = null
        binding.startDateSel.onFocusChangeListener = null
        binding.endDateSel.onFocusChangeListener = null
        binding.repeatCount.onFocusChangeListener = null
        binding.repeatUnits.onItemSelectedListener = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.settings_action_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.save_butt ->
            {
                // dump all widget data into data struct
                if(!store_widgets_to_data())
                    return true

                // check to make sure start time is before end
                if(data.end_time < data.start_time)
                {
                    Toast.makeText(this, R.string.end_before_start_err, Toast.LENGTH_LONG).show()
                    return true
                }

                // insert new or update existing row
                if(data.rowid < 0)
                    data.insert(this)
                else
                    data.update(this)

                finish()
                return true
            }

            R.id.settings ->
            {
                startActivity(Intent(this, Preferences::class.java))
                return true
            }
        }
        return false
    }

    // dump all widget data into data obj
    private fun store_widgets_to_data(): Boolean
    {
        var errors = false
        // precision data has been stored through its callback already
        data.start_tz = binding.startTz.selectedItem.toString()
        data.end_tz = binding.endTz.selectedItem.toString()

        val datetime_df = SimpleDateFormat("$date_format $time_format", Locale.US)
        val date_df = SimpleDateFormat(date_format, Locale.US)
        val time_df = SimpleDateFormat(time_format_edit, Locale.US)

        datetime_df.timeZone = TimeZone.getTimeZone(data.start_tz)
        date_df.timeZone = TimeZone.getTimeZone(data.start_tz)
        time_df.timeZone = TimeZone.getTimeZone(data.start_tz)

        val start_date = date_df.parse(binding.startDateSel.text.toString(), ParsePosition(0))
        val start_time = time_df.parse(binding.startTimeSel.text.toString(), ParsePosition(0))

        // validate date and time
        if(start_date == null)
        {
            Toast.makeText(this@Settings, resources.getString(R.string.invalid_date,
                    binding.startDateSel.text, date_format),
                    Toast.LENGTH_LONG).show()

            errors = true
        }
        if(start_time == null)
        {
            Toast.makeText(this@Settings, resources.getString(R.string.invalid_time,
                    binding.startTimeSel.text, time_format_edit),
                    Toast.LENGTH_LONG).show()

            errors = true
        }

        // parse full date-time string
        if(start_date != null && start_time != null)
        {
            if(hour_24)
            {
                data.start_time = datetime_df.parse(binding.startDateSel.text.toString() + " " +
                        binding.startTimeSel.text.toString(),
                        ParsePosition(0)).time / 1000
            }
            else
            {
                data.start_time = datetime_df.parse(binding.startDateSel.text.toString() + " " +
                        binding.startTimeSel.text.toString() + " " +
                        binding.startAmPm.selectedItem.toString(),
                        ParsePosition(0)).time / 1000
            }
        }

        datetime_df.timeZone = TimeZone.getTimeZone(data.end_tz)
        date_df.timeZone = TimeZone.getTimeZone(data.end_tz)
        time_df.timeZone = TimeZone.getTimeZone(data.end_tz)

        val end_date = date_df.parse(binding.endDateSel.text.toString(), ParsePosition(0))
        val end_time = time_df.parse(binding.endTimeSel.text.toString(), ParsePosition(0))

        // validate date and time
        if(end_date == null)
        {
            Toast.makeText(this@Settings, resources.getString(R.string.invalid_date,
                    binding.endDateSel.text, date_format),
                    Toast.LENGTH_LONG).show()

            errors = true
        }
        if(end_time == null)
        {
            Toast.makeText(this@Settings, resources.getString(R.string.invalid_time,
                    binding.endTimeSel.text, time_format_edit),
                    Toast.LENGTH_LONG).show()

            errors = true
        }

        // parse full date-time string
        if(end_date != null && end_time != null)
        {
            if(hour_24)
            {
                data.end_time = datetime_df.parse(binding.endDateSel.text.toString() + " " +
                        binding.endTimeSel.text.toString(),
                        ParsePosition(0)).time / 1000
            }
            else
            {
                data.end_time = datetime_df.parse(binding.endDateSel.text.toString() + " " +
                        binding.endTimeSel.text.toString() + " " +
                        binding.endAmPm.selectedItem.toString(),
                        ParsePosition(0)).time / 1000
            }
        }

        // other repeat data stored in callbacks
        var repeat_count = 0
        try
        {
            repeat_count = Integer.parseInt(binding.repeatCount.text.toString())
        }
        catch(ignored: NumberFormatException)
        {
        }

        if(repeat_count <= 0)
        {
            Toast.makeText(this@Settings, R.string.invalid_repeat_count, Toast.LENGTH_LONG).show()
            errors = true
        }
        else
        {
            data.repeat_count = repeat_count
        }

        data.title = binding.title.text.toString()

        return !errors
    }

    // Button pressed callbacks
    fun on_start_cal_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        // create a calendar dialog, pass current date string
        date_time_dialog_target = R.id.start_date_sel
        val frag = Datepicker_frag()
        val args = Bundle()
        args.putString(Datepicker_frag.DATE, binding.startDateSel.text.toString())
        args.putLong(Datepicker_frag.STORE_DATE, data.start_time)
        frag.arguments = args
        frag.show(supportFragmentManager, "start_date_picker")
    }

    fun on_start_clock_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        // create a clock dialog, pass current time string
        date_time_dialog_target = R.id.start_time_sel
        val frag = Timepicker_frag()
        val args = Bundle()
        args.putString(Timepicker_frag.TIME, binding.startTimeSel.text.toString())
        args.putLong(Timepicker_frag.STORE_TIME, data.start_time)
        args.putString(Timepicker_frag.AM_PM, binding.startAmPm.selectedItem.toString())
        frag.arguments = args
        frag.show(supportFragmentManager, "start_time_picker")
    }

    fun on_end_cal_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        // create a calendar dialog, pass current date string
        date_time_dialog_target = R.id.end_date_sel
        val frag = Datepicker_frag()
        val args = Bundle()
        args.putString(Datepicker_frag.DATE, binding.endDateSel.text.toString())
        args.putLong(Datepicker_frag.STORE_DATE, data.end_time)
        frag.arguments = args
        frag.show(supportFragmentManager, "end_date_picker")
    }

    fun on_end_clock_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        // create a clock dialog, pass current time string
        date_time_dialog_target = R.id.end_time_sel
        val frag = Timepicker_frag()
        val args = Bundle()
        args.putString(Timepicker_frag.TIME, binding.endTimeSel.text.toString())
        args.putLong(Timepicker_frag.STORE_TIME, data.end_time)
        args.putString(Timepicker_frag.AM_PM, binding.endAmPm.selectedItem.toString())
        frag.arguments = args
        frag.show(supportFragmentManager, "end_time_picker")
    }

    fun on_repeat_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        data.repeats = binding.repeatSw.isChecked
        binding.repeatFreq.visibility = if(data.repeats) View.VISIBLE else View.GONE
    }

    fun on_precision_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        val d = Precision_dialog_frag()
        val args = Bundle()
        args.putInt(Precision_dialog_frag.PRECISION_ARG, data.precision)
        d.arguments = args
        d.show(supportFragmentManager, "precision")
    }

    fun on_days_of_week_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        val selected = BooleanArray(Progress_bars_table.Days_of_week.values().size)
        for(day in Progress_bars_table.Days_of_week.values())
        {
            selected[day.index] = data.repeat_days_of_week and day.mask != 0
        }

        val frag = Checkbox_dialog_frag()

        val args = Bundle()
        args.putInt(Checkbox_dialog_frag.TITLE_ARG, R.string.days_of_week_title)
        args.putInt(Checkbox_dialog_frag.ENTRIES_ARG, R.array.day_of_week)
        args.putBooleanArray(Checkbox_dialog_frag.SELECTION_ARG, selected)

        frag.arguments = args
        frag.show(supportFragmentManager, DAYS_OF_WEEK_CHECKBOX_DIALOG)
    }

    fun on_show_elements_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        val selected = BooleanArray(3)
        selected[SHOW_PROGRESS_CHECKBOX] = data.show_progress
        selected[SHOW_START_CHECKBOX] = data.show_start
        selected[SHOW_END_CHECKBOX] = data.show_end

        val frag = Checkbox_dialog_frag()

        val args = Bundle()
        args.putInt(Checkbox_dialog_frag.TITLE_ARG, R.string.show_elements_header)
        args.putInt(Checkbox_dialog_frag.ENTRIES_ARG, R.array.show_elements)
        args.putBooleanArray(Checkbox_dialog_frag.SELECTION_ARG, selected)

        frag.arguments = args
        frag.show(supportFragmentManager, SHOW_ELEMENTS_CHECKBOX_DIALOG)
    }

    fun on_show_units_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        val selected = BooleanArray(7)
        selected[SHOW_SECONDS_CHECKBOX] = data.show_seconds
        selected[SHOW_MINUTES_CHECKBOX] = data.show_minutes
        selected[SHOW_HOURS_CHECKBOX] = data.show_hours
        selected[SHOW_DAYS_CHECKBOX] = data.show_days
        selected[SHOW_WEEKS_CHECKBOX] = data.show_weeks
        selected[SHOW_MONTHS_CHECKBOX] = data.show_months
        selected[SHOW_YEARS_CHECKBOX] = data.show_years

        val frag = Checkbox_dialog_frag()

        val args = Bundle()
        args.putInt(Checkbox_dialog_frag.TITLE_ARG, R.string.show_units_header)
        args.putInt(Checkbox_dialog_frag.ENTRIES_ARG, R.array.time_units)
        args.putBooleanArray(Checkbox_dialog_frag.SELECTION_ARG, selected)

        frag.arguments = args
        frag.show(supportFragmentManager, SHOW_UNITS_CHECKBOX_DIALOG)
    }

    fun on_timer_opts_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        val selected = BooleanArray(3)
        selected[TERMINATE_CHECKBOX] = data.terminate
        selected[NOTIFY_START_CHECKBOX] = data.notify_start
        selected[NOTIFY_END_CHECKBOX] = data.notify_end

        val frag = Checkbox_dialog_frag()

        val args = Bundle()
        args.putInt(Checkbox_dialog_frag.TITLE_ARG, R.string.timer_opts_header)
        args.putInt(Checkbox_dialog_frag.ENTRIES_ARG, R.array.timer_opts)
        args.putBooleanArray(Checkbox_dialog_frag.SELECTION_ARG, selected)

        frag.arguments = args
        frag.show(supportFragmentManager, TIMER_OPTS_CHECKBOX_DIALOG)
    }

    fun on_countdown_text_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        // Launch screen to enter countdown text
        val intent = Intent(this, Countdown_text::class.java)
        intent.putExtra(Countdown_text.EXTRA_DATA, data)
        startActivityForResult(intent, Countdown_text.RESULT_COUNTDOWN_TEXT)
    }

    // Dialog return callbacks

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int)
    {
        // build new string from returned data
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)

        val df = SimpleDateFormat(date_format, Locale.US)
        (findViewById<View>(date_time_dialog_target) as android.support.design.widget.TextInputEditText).setText(df.format(cal.time))
    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int)
    {
        // build new string from returned data
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)

        val df = SimpleDateFormat(time_format_edit, Locale.US)
        (findViewById<View>(date_time_dialog_target) as android.support.design.widget.TextInputEditText).setText(df.format(cal.time))

        // set AM/PM spinner if needed
        if(!hour_24)
        {
            val spin_target = if(date_time_dialog_target == R.id.start_date_sel) binding.startAmPm else binding.endAmPm
            val am_pm = cal.get(Calendar.AM_PM)
            spin_target.setSelection(if(am_pm == Calendar.AM) array_am_i else array_pm_i)
        }
    }

    // called when OK pressed on precision dialog
    fun on_precision_set(precision: Int)
    {
        // get and store the data
        data.precision = precision
        binding.precision.text = data.precision.toString()
    }

    // called when OK pressed on checkbox dialogs
    fun on_checkbox_dialog_ok(id: String, selected: BooleanArray)
    {
        if(id == DAYS_OF_WEEK_CHECKBOX_DIALOG)
        {
            var days_of_week = 0
            for(day in selected.indices)
            {
                if(selected[day])
                    days_of_week = days_of_week or (1 shl day)
            }

            if(days_of_week == 0)
            {
                Toast.makeText(this@Settings, R.string.no_days_of_week_err, Toast.LENGTH_LONG).show()
            }
            else
            {
                data.repeat_days_of_week = days_of_week
                binding.repeatDaysOfWeek.text = get_days_of_week_abbr(this@Settings, data.repeat_days_of_week)
            }
        }
        if(id == SHOW_ELEMENTS_CHECKBOX_DIALOG)
        {
            data.show_progress = selected[SHOW_PROGRESS_CHECKBOX]
            data.show_start = selected[SHOW_START_CHECKBOX]
            data.show_end = selected[SHOW_END_CHECKBOX]
        }
        if(id == SHOW_UNITS_CHECKBOX_DIALOG)
        {
            data.show_seconds = selected[SHOW_SECONDS_CHECKBOX]
            data.show_minutes = selected[SHOW_MINUTES_CHECKBOX]
            data.show_hours = selected[SHOW_HOURS_CHECKBOX]
            data.show_days = selected[SHOW_DAYS_CHECKBOX]
            data.show_weeks = selected[SHOW_WEEKS_CHECKBOX]
            data.show_months = selected[SHOW_MONTHS_CHECKBOX]
            data.show_years = selected[SHOW_YEARS_CHECKBOX]
        }
        if(id == TIMER_OPTS_CHECKBOX_DIALOG)
        {
            data.terminate = selected[TERMINATE_CHECKBOX]
            data.notify_start = selected[NOTIFY_START_CHECKBOX]
            data.notify_end = selected[NOTIFY_END_CHECKBOX]
        }
    }

    // get data back from Countdown_text
    override fun onActivityResult(request_code: Int, result_code: Int, intent: Intent?)
    {
        if(request_code == Countdown_text.RESULT_COUNTDOWN_TEXT && result_code == Activity.RESULT_OK)
        {
            // get changed data
            data = intent?.getSerializableExtra(Countdown_text.EXTRA_DATA) as Data
        }
    }

    companion object
    {
        const val EXTRA_EDIT_ROW_ID = "org.mattvchandler.progressbars.EDIT_ROW_ID"

        private const val STATE_DATA = "data"
        private const val STATE_SAVE_DATA = "save_data"
        private const val STATE_TARGET = "target"

        private const val DAYS_OF_WEEK_CHECKBOX_DIALOG = "DAYS_OF_WEEK"
        private const val SHOW_ELEMENTS_CHECKBOX_DIALOG = "SHOW_ELEMENTS"
        private const val SHOW_UNITS_CHECKBOX_DIALOG = "SHOW_UNITS"
        private const val TIMER_OPTS_CHECKBOX_DIALOG = "SHOW_TIMER_OPTS"

        private const val SHOW_PROGRESS_CHECKBOX = 0
        private const val SHOW_START_CHECKBOX = 1
        private const val SHOW_END_CHECKBOX = 2

        private const val SHOW_SECONDS_CHECKBOX = 0
        private const val SHOW_MINUTES_CHECKBOX = 1
        private const val SHOW_HOURS_CHECKBOX = 2
        private const val SHOW_DAYS_CHECKBOX = 3
        private const val SHOW_WEEKS_CHECKBOX = 4
        private const val SHOW_MONTHS_CHECKBOX = 5
        private const val SHOW_YEARS_CHECKBOX = 6

        private const val TERMINATE_CHECKBOX = 0
        private const val NOTIFY_START_CHECKBOX = 1
        private const val NOTIFY_END_CHECKBOX = 2

        private fun get_days_of_week_abbr(context: Context, days_of_week: Int): String
        {
            // set days of week for weekly repeat (ex: MWF)
            val days_of_week_str = StringBuilder()
            for(day in Progress_bars_table.Days_of_week.values())
            {
                if(days_of_week and day.mask != 0)
                    days_of_week_str.append(context.resources.getStringArray(R.array.day_of_week_abbr)[day.index])
            }

            return days_of_week_str.toString()
        }
    }
}
