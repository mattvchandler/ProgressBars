/*
Copyright (C) 2019 Matthew Chandler

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
import android.database.ContentObserver
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.preference.PreferenceManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.TextInputEditText
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
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
import java.util.*

// Settings for each timer
class Settings: Dynamic_theme_activity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener
{
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var data: Data
    private lateinit var save_data: Data

    private var date_time_dialog_target: Int = 0

    private var locale = Locale.getDefault()
    private var date_df = SimpleDateFormat.getDateInstance() as SimpleDateFormat
    private var time_df = SimpleDateFormat.getTimeInstance() as SimpleDateFormat

    private val on_24_hour_change = object: ContentObserver(Handler())
    {
        override fun onChange(selfChange: Boolean)
        {
            super.onChange(selfChange)
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        setSupportActionBar(binding.toolbar as Toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // only run this on 1st creation
        if(savedInstanceState == null)
        {
            data = intent.getSerializableExtra(EXTRA_EDIT_DATA) as Data? ?: Data(this)

            // no rowid passed? make a new one
            if(intent.hasExtra(EXTRA_EDIT_DATA))
            {
                setTitle(R.string.edit_title)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
            }
            else
                setTitle(R.string.add_title)

            save_data = Data(data)

            date_df = get_date_format(this)
            time_df = get_time_format()
        }
        else
        {
            // reload old and current data from save state
            data = savedInstanceState.getSerializable(STATE_DATA) as Data
            save_data = savedInstanceState.getSerializable(STATE_SAVE_DATA) as Data
            date_time_dialog_target = savedInstanceState.getInt(STATE_TARGET)

            date_df = savedInstanceState.getSerializable(STATE_DATE_DF) as SimpleDateFormat
            time_df = savedInstanceState.getSerializable(STATE_TIME_DF) as SimpleDateFormat
            locale = savedInstanceState.getSerializable(STATE_LOCALE) as Locale

            // populate date/time widget values
            if(intent.hasExtra(EXTRA_EDIT_DATA))
                setTitle(R.string.edit_title)
            else
                setTitle(R.string.add_title)
        }

        // set selected values
        binding.data = data

        binding.endDateTxt.hint  = if(data.separate_time) resources.getString(R.string.end_date_txt)  else resources.getString(R.string.single_date_txt)
        binding.endTimeTxt.hint  = if(data.separate_time) resources.getString(R.string.end_time_txt)  else resources.getString(R.string.single_time_txt)
        binding.endTzTxt.text    = if(data.separate_time) resources.getString(R.string.end_tz_prompt) else resources.getString(R.string.single_tz_prompt)
        binding.terminateSw.text = if(data.separate_time) resources.getString(R.string.terminate)     else resources.getString(R.string.single_terminate)
        binding.endNotifySw.text = if(data.separate_time) resources.getString(R.string.end_notify)    else resources.getString(R.string.single_notify)

        binding.startTz.text = data.start_tz.replace('_', ' ')
        binding.endTz.text   = data.end_tz.replace('_', ' ')

        val start_date = Date(data.start_time * 1000)
        date_df.timeZone = TimeZone.getTimeZone(data.start_tz)
        time_df.timeZone = TimeZone.getTimeZone(data.start_tz)
        binding.startDateSel.setText(date_df.format(start_date))
        binding.startTimeSel.setText(time_df.format(start_date))

        val end_date = Date(data.end_time * 1000)
        date_df.timeZone = TimeZone.getTimeZone(data.end_tz)
        time_df.timeZone = TimeZone.getTimeZone(data.end_tz)
        binding.endDateSel.setText(date_df.format(end_date))
        binding.endTimeSel.setText(time_df.format(end_date))

        binding.repeatFreq.visibility = if(data.repeats) View.VISIBLE else View.GONE
        binding.repeatCount.setText(data.repeat_count.toString())
        binding.repeatUnits.setSelection(data.repeat_unit)
        binding.repeatDaysOfWeek.text = get_days_of_week_abbr(this, data.repeat_days_of_week)

        val week_selected = data.repeat_unit == Progress_bars_table.Unit.WEEK.index
        binding.repeatOn.visibility = if(week_selected) View.VISIBLE else View.GONE
        binding.repeatDaysOfWeek.visibility = if(week_selected) View.VISIBLE else View.GONE

        if(Build.VERSION.SDK_INT < 26)
            binding.notificationSettings.visibility = View.GONE
    }

    public override fun onResume()
    {
        super.onResume()

        // check for date format change
        val old_date_df = date_df
        val old_time_df = time_df
        val old_locale = locale

        date_df = get_date_format(this)
        time_df = get_time_format()

        locale = Locale.getDefault()

        if(old_date_df.toLocalizedPattern() != date_df.toLocalizedPattern() || locale != old_locale)
        {
            // date format has changed. get formatter for old and new formats

            // parse date as old format, replace w/ new
            var date = binding.startDateSel.text.toString()

            old_date_df.timeZone = TimeZone.getTimeZone(data.start_tz)
            var date_obj = old_date_df.parse(date, ParsePosition(0))
            if(date_obj != null)
            {
                date_df.timeZone = TimeZone.getTimeZone(data.start_tz)
                date = date_df.format(date_obj)
                binding.startDateSel.setText(date)
            }

            date = binding.endDateSel.text.toString()

            old_date_df.timeZone = TimeZone.getTimeZone(data.end_tz)
            date_obj = old_date_df.parse(date, ParsePosition(0))
            if(date_obj != null)
            {
                date_df.timeZone = TimeZone.getTimeZone(data.end_tz)
                date = date_df.format(date_obj)
                binding.endDateSel.setText(date)
            }
        }
        if(old_time_df.toLocalizedPattern() != time_df.toLocalizedPattern() || locale != old_locale)
        {
            var time = binding.startTimeSel.text.toString()

            old_time_df.timeZone = TimeZone.getTimeZone(data.start_tz)
            var date_obj = old_time_df.parse(time, ParsePosition(0))
            if(date_obj != null)
            {
                time_df.timeZone = TimeZone.getTimeZone(data.start_tz)
                time = time_df.format(date_obj)
                binding.startTimeSel.setText(time)
            }

            time = binding.endTimeSel.text.toString()

            old_time_df.timeZone = TimeZone.getTimeZone(data.end_tz)
            date_obj = old_time_df.parse(time, ParsePosition(0))
            if(date_obj != null)
            {
                time_df.timeZone = TimeZone.getTimeZone(data.end_tz)
                time = time_df.format(date_obj)
                binding.endTimeSel.setText(time)
            }
        }

        // set listeners on time and date fields
        binding.startTimeSel.onFocusChangeListener = Time_listener(data)
        binding.endTimeSel.onFocusChangeListener = Time_listener(data)

        binding.startDateSel.onFocusChangeListener = Date_listener(data)
        binding.endDateSel.onFocusChangeListener = Date_listener(data)

        binding.repeatCount.onFocusChangeListener = Repeat_count_listener(data)

        binding.repeatUnits.onItemSelectedListener = object: AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long)
            {
                val week_selected = i == Progress_bars_table.Unit.WEEK.index
                binding.repeatOn.visibility = if(week_selected) View.VISIBLE else View.GONE
                binding.repeatDaysOfWeek.visibility = if(week_selected) View.VISIBLE else View.GONE

                data.repeat_unit = i
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }

        contentResolver.registerContentObserver(android.provider.Settings.System.getUriFor(android.provider.Settings.System.TIME_12_24), false, on_24_hour_change)
    }

    override fun onPause()
    {
        // clear listeners
        binding.startTimeSel.onFocusChangeListener = null
        binding.endTimeSel.onFocusChangeListener = null
        binding.startDateSel.onFocusChangeListener = null
        binding.endDateSel.onFocusChangeListener = null
        binding.repeatCount.onFocusChangeListener = null
        binding.repeatUnits.onItemSelectedListener = null

        contentResolver.unregisterContentObserver(on_24_hour_change)

        super.onPause()
    }

    override fun onSaveInstanceState(out: Bundle)
    {
        super.onSaveInstanceState(out)

        // save all data to be restored
        store_widgets_to_data()
        out.putSerializable(STATE_DATA, data)
        out.putSerializable(STATE_SAVE_DATA, save_data)
        out.putInt(STATE_TARGET, date_time_dialog_target)
        out.putSerializable(STATE_DATE_DF, date_df)
        out.putSerializable(STATE_TIME_DF, time_df)
        out.putSerializable(STATE_LOCALE, locale)
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
                if(data.separate_time && data.end_time < data.start_time)
                {
                    Toast.makeText(this, R.string.end_before_start_err, Toast.LENGTH_LONG).show()
                    return true
                }

                val intent = Intent()
                intent.putExtra(EXTRA_EDIT_DATA, data)
                setResult(Activity.RESULT_OK, intent)

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
        // most data has been stored through callbacks already

        var errors = false

        val start_time = parse_date_and_time(binding.startDateSel.text.toString(), binding.startTimeSel.text.toString(),  data.start_tz)
        if(start_time == null)
            errors = true
        else
            data.start_time = start_time

        val end_time = parse_date_and_time(binding.endDateSel.text.toString(), binding.endTimeSel.text.toString(),  data.end_tz)
        if(end_time == null)
            errors = true
        else
            data.end_time = end_time

        // other repeat data stored in callbacks
        var repeat_count = 0
        try
        {
            repeat_count = Integer.parseInt(binding.repeatCount.text.toString())
        }
        catch(ignored: NumberFormatException) {}

        if(repeat_count <= 0)
        {
            Toast.makeText(this, R.string.invalid_repeat_count, Toast.LENGTH_LONG).show()
            errors = true
        }
        else
        {
            data.repeat_count = repeat_count
        }

        data.title = binding.title.text.toString()

        return !errors
    }

    private fun parse_date_and_time(date_txt: String, time_txt: String, timezone: String): Long?
    {
        date_df.timeZone = TimeZone.getTimeZone(timezone)
        time_df.timeZone = TimeZone.getTimeZone(timezone)

        val date = date_df.parse(date_txt, ParsePosition(0))
        val time = time_df.parse(time_txt, ParsePosition(0))

        // validate date and time
        if(date == null)
        {
            Toast.makeText(this, resources.getString(R.string.invalid_date,
                    date_txt, date_df.toLocalizedPattern()),
                    Toast.LENGTH_LONG).show()

            return null
        }
        if(time == null)
        {
            Toast.makeText(this, resources.getString(R.string.invalid_time,
                    time_txt, time_df.toLocalizedPattern()), Toast.LENGTH_LONG).show()

            return null
        }

        val datetime_df = SimpleDateFormat.getInstance() as SimpleDateFormat
        datetime_df.applyLocalizedPattern("${date_df.toLocalizedPattern()} ${time_df.toLocalizedPattern()}")
        datetime_df.timeZone = TimeZone.getTimeZone(timezone)
        datetime_df.isLenient = true

        val datetime = datetime_df.parse("$date_txt $time_txt", ParsePosition(0))

        return if(datetime == null) null else datetime.time / 1000
    }

    // Button pressed callbacks
    fun on_separate_time_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        data.separate_time = binding.separateTimeSw.isChecked

        val visibility = if(data.separate_time) View.VISIBLE else View.GONE
        binding.startTimeDivider.visibility = visibility
        binding.startDateBox.visibility     = visibility
        binding.startTimeBox.visibility     = visibility
        binding.startTzBox.visibility       = visibility
        binding.startNotifySw.visibility    = visibility

        binding.endDateTxt.hint  = if(data.separate_time) resources.getString(R.string.end_date_txt)  else resources.getString(R.string.single_date_txt)
        binding.endTimeTxt.hint  = if(data.separate_time) resources.getString(R.string.end_time_txt)  else resources.getString(R.string.single_time_txt)
        binding.endTzTxt.text    = if(data.separate_time) resources.getString(R.string.end_tz_prompt) else resources.getString(R.string.single_tz_prompt)
        binding.terminateSw.text = if(data.separate_time) resources.getString(R.string.terminate)     else resources.getString(R.string.single_terminate)
        binding.endNotifySw.text = if(data.separate_time) resources.getString(R.string.end_notify)    else resources.getString(R.string.single_notify)

    }

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
        frag.arguments = args
        frag.show(supportFragmentManager, "end_time_picker")
    }

    fun on_start_tz_butt(@Suppress("UNUSED_PARAMETER") view:View)
    {
        val start_time = parse_date_and_time(binding.startDateSel.text.toString(), binding.startTimeSel.text.toString(),  data.start_tz) ?: return

        val intent = Intent(this, TimeZone_activity::class.java)
        intent.putExtra(TimeZone_activity.EXTRA_DATE, Date(start_time * 1000))
        startActivityForResult(intent, RESULT_TIMEZONE_START)
    }

    fun on_end_tz_butt(@Suppress("UNUSED_PARAMETER") view:View)
    {
        val end_time = parse_date_and_time(binding.endDateSel.text.toString(), binding.endTimeSel.text.toString(),  data.end_tz) ?: return

        val intent = Intent(this, TimeZone_activity::class.java)
        intent.putExtra(TimeZone_activity.EXTRA_DATE, Date(end_time * 1000))
        startActivityForResult(intent, RESULT_TIMEZONE_END)
    }

    fun on_repeat_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
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
        val selected = BooleanArray(if(data.separate_time) 3 else 1)
        if(data.separate_time)
        {
            selected[SHOW_PROGRESS_CHECKBOX] = data.show_progress
            selected[SHOW_START_CHECKBOX] = data.show_start
            selected[SHOW_END_CHECKBOX] = data.show_end
        }
        else
        {
            selected[SHOW_SINGLE_TIME_CHECKBOX] = data.show_end
        }

        val frag = Checkbox_dialog_frag()

        val args = Bundle()
        args.putInt(Checkbox_dialog_frag.TITLE_ARG, R.string.show_elements_header)
        args.putInt(Checkbox_dialog_frag.ENTRIES_ARG, if(data.separate_time) R.array.show_elements else R.array.single_show_elements)
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

    fun on_countdown_text_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        // Launch screen to enter countdown text
        val intent = Intent(this, Countdown_text::class.java)
        intent.putExtra(Countdown_text.EXTRA_DATA, data)
        startActivityForResult(intent, RESULT_COUNTDOWN_TEXT)
    }

    fun on_notification_settings_butt(@Suppress("UNUSED_PARAMETER") view: View)
    {
        val intent = Intent()
        when
        {
//            Build.VERSION.SDK_INT in 21..25 ->
//            {
//                intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
//                intent.putExtra("app_package", packageName)
//                intent.putExtra("app_uid", applicationInfo.uid)
//            }
            Build.VERSION.SDK_INT > 26 ->
            {
                if(!store_widgets_to_data())
                    return

                data.create_notification_channel(this)
                intent.action = android.provider.Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS
                intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, packageName)
                intent.putExtra(android.provider.Settings.EXTRA_CHANNEL_ID, data.channel_id)
            }
//            else ->
//            {
//                intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
//                intent.addCategory(Intent.CATEGORY_DEFAULT)
//                intent.data = Uri.parse("package:$packageName")
//            }
        }

        startActivity(intent)
    }

    // Dialog return callbacks

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int)
    {
        // build new string from returned data
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, day)

        findViewById<TextInputEditText>(date_time_dialog_target).setText(date_df.format(cal.time))
    }

    override fun onTimeSet(view: TimePicker, hour: Int, minute: Int)
    {
        // build new string from returned data
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, minute)
        cal.set(Calendar.SECOND, 0)

        findViewById<TextInputEditText>(date_time_dialog_target).setText(time_df.format(cal.time))
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
        else if(id == SHOW_ELEMENTS_CHECKBOX_DIALOG)
        {
            if(data.separate_time)
            {
                data.show_progress = selected[SHOW_PROGRESS_CHECKBOX]
                data.show_start = selected[SHOW_START_CHECKBOX]
                data.show_end = selected[SHOW_END_CHECKBOX]
            }
            else
            {
                data.show_end = selected[SHOW_SINGLE_TIME_CHECKBOX]
            }
        }
        else if(id == SHOW_UNITS_CHECKBOX_DIALOG)
        {
            data.show_seconds = selected[SHOW_SECONDS_CHECKBOX]
            data.show_minutes = selected[SHOW_MINUTES_CHECKBOX]
            data.show_hours = selected[SHOW_HOURS_CHECKBOX]
            data.show_days = selected[SHOW_DAYS_CHECKBOX]
            data.show_weeks = selected[SHOW_WEEKS_CHECKBOX]
            data.show_months = selected[SHOW_MONTHS_CHECKBOX]
            data.show_years = selected[SHOW_YEARS_CHECKBOX]
        }
    }

    override fun onActivityResult(request_code: Int, result_code: Int, intent: Intent?)
    {
        // get data back from Countdown_text
        if(result_code == Activity.RESULT_OK)
        {
            when(request_code)
            {
                RESULT_COUNTDOWN_TEXT -> data = intent?.getSerializableExtra(Countdown_text.EXTRA_DATA) as Data
                RESULT_TIMEZONE_START -> {
                    val tz = intent?.getSerializableExtra(TimeZone_activity.EXTRA_SELECTED_TZ) as TimeZone_disp
                    data.start_tz = tz.id
                    binding.startTz.text = tz.name
                }
                RESULT_TIMEZONE_END -> {
                    val tz = intent?.getSerializableExtra(TimeZone_activity.EXTRA_SELECTED_TZ) as TimeZone_disp
                    data.end_tz = tz.id
                    binding.endTz.text = tz.name
                }
            }
        }
    }

    companion object
    {
        const val EXTRA_EDIT_DATA = "org.mattvchandler.progressbars.EDIT_DATA"

        private const val STATE_DATA = "data"
        private const val STATE_SAVE_DATA = "save_data"
        private const val STATE_TARGET = "target"
        private const val STATE_DATE_FORMAT = "date_format"
        private const val STATE_DATE_DF = "date_df"
        private const val STATE_TIME_DF = "time_df"
        private const val STATE_LOCALE = "locale"

        private const val DAYS_OF_WEEK_CHECKBOX_DIALOG = "DAYS_OF_WEEK"
        private const val SHOW_ELEMENTS_CHECKBOX_DIALOG = "SHOW_ELEMENTS"
        private const val SHOW_UNITS_CHECKBOX_DIALOG = "SHOW_UNITS"
        private const val TIMER_OPTS_CHECKBOX_DIALOG = "SHOW_TIMER_OPTS"

        private const val SHOW_PROGRESS_CHECKBOX = 0
        private const val SHOW_START_CHECKBOX = 1
        private const val SHOW_END_CHECKBOX = 2
        private const val SHOW_SINGLE_TIME_CHECKBOX = 0

        private const val SHOW_SECONDS_CHECKBOX = 0
        private const val SHOW_MINUTES_CHECKBOX = 1
        private const val SHOW_HOURS_CHECKBOX = 2
        private const val SHOW_DAYS_CHECKBOX = 3
        private const val SHOW_WEEKS_CHECKBOX = 4
        private const val SHOW_MONTHS_CHECKBOX = 5
        private const val SHOW_YEARS_CHECKBOX = 6

        private const val RESULT_TIMEZONE_START = 0
        private const val RESULT_TIMEZONE_END = 1
        private const val RESULT_COUNTDOWN_TEXT = 2

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

        fun get_date_format(context: Context): SimpleDateFormat
        {
            val date_format = PreferenceManager.getDefaultSharedPreferences(context).getString("date_format", context.resources.getString(R.string.pref_date_format_default))!!
            val date_df = SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT) as SimpleDateFormat

            if(date_format != "locale")
            {
                date_df.applyPattern(date_format)
            }
            else
            {
                // force 4-digit year regardless of what the locale default is
                val new_pattern = date_df.toLocalizedPattern().replace("y+".toRegex(), "yyyy")
                date_df.applyLocalizedPattern(new_pattern)
            }

            date_df.isLenient = true

            return date_df
        }

        fun get_time_format(): SimpleDateFormat
        {
            val time_df = SimpleDateFormat.getTimeInstance(SimpleDateFormat.MEDIUM) as SimpleDateFormat
            time_df.isLenient = true

            return time_df
        }
    }
}
