package org.mattvchandler.progressbars;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.mattvchandler.progressbars.databinding.ActivitySettingsBinding;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Settings extends AppCompatActivity implements Precision_dialog_frag.NoticeDialogListener,
                                                           DatePickerDialog.OnDateSetListener,
                                                           TimePickerDialog.OnTimeSetListener
{
    public static final String EXRTA_EDIT_ROW_ID = "org.mattvchandler.progressbars.EDIT_ROW_ID";
    public static final String RESULT_NEW_DATA = "org.mattvchandler.progressbars.RESULT_ROW_ID";
    public static final String RESULT_OLD_DATA = "org.mattvchandler.progressbars.RESULT_OLD_DATA";

    public static final String STATE_DATA = "data";
    public static final String STATE_SAVE_DATA = "data";

    private ActivitySettingsBinding binding;
    private Progress_bar_data data;
    private Progress_bar_data save_data;

    private EditText date_time_dialog_target;

    // TODO: remove hardcoded strings
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        setSupportActionBar(binding.progressBarToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // fill timezone spinners
        ArrayAdapter<String> tz_adapter = new ArrayAdapter<String>(this, R.layout.right_aligned_spinner, TimeZone.getAvailableIDs());
        tz_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.startTz.setAdapter(tz_adapter);
        binding.endTz.setAdapter(tz_adapter);

        View.OnFocusChangeListener date_listener = new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    String new_date = ((EditText)v).getText().toString();
                    SimpleDateFormat df = new SimpleDateFormat(Settings.this.getResources().getString(R.string.date_format), Locale.US);

                    Date date = df.parse(new_date, new ParsePosition(0));
                    if(date == null)
                    {
                        Toast.makeText(Settings.this, "Invalid date: " + new_date + ". Correct format is: " +
                                                      Settings.this.getResources().getString(R.string.date_format),
                                Toast.LENGTH_LONG).show();

                        if(v.getId() == R.id.start_date_sel)
                        {
                            df.setTimeZone(TimeZone.getTimeZone(data.start_tz));
                            ((EditText) v).setText(df.format(new Date(data.start_time * 1000)));
                        }
                        else if(v.getId() == R.id.end_date_sel)
                        {
                            df.setTimeZone(TimeZone.getTimeZone(data.end_tz));
                            ((EditText) v).setText(df.format(new Date(data.end_time * 1000)));
                        }
                    }
                    else
                    {
                        new_date = df.format(date);
                        ((EditText) v).setText(new_date);
                    }
                }
            }
        };

        View.OnFocusChangeListener time_listener = new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if(!hasFocus)
                {
                    String new_time = ((EditText)v).getText().toString();
                    SimpleDateFormat df = new SimpleDateFormat(Settings.this.getResources().getString(R.string.time_format), Locale.US);

                    Date time = df.parse(new_time, new ParsePosition(0));
                    if(time == null)
                    {
                        Toast.makeText(Settings.this, "Invalid time: " + new_time + ". Correct format is: " +
                                                      Settings.this.getResources().getString(R.string.time_format),
                                Toast.LENGTH_LONG).show();

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
                        new_time = df.format(time);
                        ((EditText) v).setText(new_time);
                    }
                }
            }
        };

        binding.startTimeSel.setOnFocusChangeListener(time_listener);
        binding.endTimeSel.setOnFocusChangeListener(time_listener);

        binding.startDateSel.setOnFocusChangeListener(date_listener);
        binding.endDateSel.setOnFocusChangeListener(date_listener);

        // only run this on 1st creation
        if(savedInstanceState == null)
        {
            long rowid = getIntent().getLongExtra(EXRTA_EDIT_ROW_ID, -1);

            // no rowid passed? make a new one
            if(rowid < 0)
            {
                SQLiteDatabase db = new Progress_bar_DB(this).getWritableDatabase();

                data = new Progress_bar_data(this);

                Cursor cursor = db.rawQuery("SELECT MAX(" + Progress_bar_contract.Progress_bar_table.ORDER_COL + ") + 1 AS new_order FROM " + Progress_bar_contract.Progress_bar_table.TABLE_NAME, null);
                cursor.moveToFirst();
                data.order = cursor.getLong(0);
                cursor.close();
                db.close();
            }
            else
            {
                // get data from row
                data = new Progress_bar_data(this, rowid);
            }
            save_data = new Progress_bar_data(data);
        }
        else
        {
            data = (Progress_bar_data)savedInstanceState.getSerializable(STATE_DATA);
            save_data = (Progress_bar_data)savedInstanceState.getSerializable(STATE_SAVE_DATA);
        }

        binding.setData(data);

        int found = 0;
        for(int i = 0; i < tz_adapter.getCount(); ++i)
        {
            if(tz_adapter.getItem(i).equals(data.start_tz))
            {
                binding.startTz.setSelection(i);
                ++found;
            }
            if(tz_adapter.getItem(i).equals(data.end_tz))
            {
                binding.endTz.setSelection(i);
                ++found;
            }
            if(found == 2)
                break;
        }


        SimpleDateFormat df_date = new SimpleDateFormat(getResources().getString(R.string.date_format), Locale.US);
        SimpleDateFormat df_time = new SimpleDateFormat(getResources().getString(R.string.time_format), Locale.US);

        Date start_date = new Date(data.start_time * 1000);
        df_date.setTimeZone(TimeZone.getTimeZone(data.start_tz));
        df_time.setTimeZone(TimeZone.getTimeZone(data.start_tz));
        binding.startDateSel.setText(df_date.format(start_date));
        binding.startTimeSel.setText(df_time.format(start_date));

        Date end_date = new Date(data.end_time * 1000);
        df_date.setTimeZone(TimeZone.getTimeZone(data.end_tz));
        df_time.setTimeZone(TimeZone.getTimeZone(data.end_tz));
        binding.endDateSel.setText(df_date.format(end_date));
        binding.endTimeSel.setText(df_time.format(end_date));
    }

    @Override
    protected void onSaveInstanceState(Bundle out)
    {
        store_widgets_to_data();
        super.onSaveInstanceState(out);
        out.putSerializable(STATE_DATA, data);
        out.putSerializable(STATE_SAVE_DATA, save_data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.settings_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.save_butt:

                if(!store_widgets_to_data())
                    return true;

                Intent intent = new Intent();
                if(data.rowid < 0)
                {
                    data.insert(this);
                }
                else
                {
                    data.update(this);
                    intent.putExtra(RESULT_OLD_DATA, save_data);
                }

                intent.putExtra(RESULT_NEW_DATA, data);

                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return false;
    }

    public boolean store_widgets_to_data()
    {
        boolean errors = false;
        // get data from widgets precision has been stored through its callback already
        data.start_tz = binding.startTz.getSelectedItem().toString();
        data.end_tz = binding.endTz.getSelectedItem().toString();

        // validate dates and times
        SimpleDateFormat datetime_df = new SimpleDateFormat(getResources().getString(R.string.date_format) + " " +
                                                            getResources().getString(R.string.time_format), Locale.US);
        SimpleDateFormat date_df = new SimpleDateFormat(getResources().getString(R.string.date_format), Locale.US);
        SimpleDateFormat time_df = new SimpleDateFormat(getResources().getString(R.string.time_format), Locale.US);

        datetime_df.setTimeZone(TimeZone.getTimeZone(data.start_tz));
        date_df.setTimeZone(TimeZone.getTimeZone(data.start_tz));
        time_df.setTimeZone(TimeZone.getTimeZone(data.start_tz));

        Date start_date = date_df.parse(binding.startDateSel.getText().toString(), new ParsePosition((0)));
        Date start_time = time_df.parse(binding.startTimeSel.getText().toString(), new ParsePosition((0)));

        if(start_date == null)
        {
            Toast.makeText(this, "Invalid date: " + binding.startDateSel.getText().toString()  +
                                 ". Correct format is: " + getResources().getString(R.string.date_format),
                    Toast.LENGTH_LONG).show();

            errors = true;
        }
        if(start_time == null)
        {
            Toast.makeText(this, "Invalid time: " + binding.startTimeSel.getText().toString()  +
                                 ". Correct format is: " + getResources().getString(R.string.time_format),
                    Toast.LENGTH_LONG).show();

            errors = true;
        }

        if(start_date != null && start_time!= null)
        {
            data.start_time = datetime_df.parse(binding.startDateSel.getText().toString() + " " +
                                                binding.startTimeSel.getText().toString(),
                    new ParsePosition((0))).getTime() / 1000;
        }

        datetime_df.setTimeZone(TimeZone.getTimeZone(data.end_tz));
        date_df.setTimeZone(TimeZone.getTimeZone(data.end_tz));
        time_df.setTimeZone(TimeZone.getTimeZone(data.end_tz));

        Date end_date = date_df.parse(binding.endDateSel.getText().toString(), new ParsePosition((0)));
        Date end_time = time_df.parse(binding.endTimeSel.getText().toString(), new ParsePosition((0)));

        if(end_date == null)
        {
            Toast.makeText(this, "Invalid date: " + binding.endDateSel.getText().toString()  +
                                 ". Correct format is: " + getResources().getString(R.string.date_format),
                    Toast.LENGTH_LONG).show();

            errors = true;
        }
        if(end_time == null)
        {
            Toast.makeText(this, "Invalid time: " + binding.endTimeSel.getText().toString()  +
                                 ". Correct format is: " + getResources().getString(R.string.time_format),
                    Toast.LENGTH_LONG).show();

            errors = true;
        }

        if(end_date != null && end_time != null)
        {
            data.end_time = datetime_df.parse(binding.endDateSel.getText().toString() + " " +
                                              binding.endTimeSel.getText().toString(),
                    new ParsePosition((0))).getTime() / 1000;
        }

        if(data.end_time < data.start_time)
        {
            Toast.makeText(this, "Error: End date/time is before start date/time", Toast.LENGTH_LONG).show();
            errors = true;
        }

        data.title = binding.title.getText().toString();
        data.pre_text = binding.preText.getText().toString();
        data.countdown_text = binding.countdownText.getText().toString();
        data.complete_text = binding.completeText.getText().toString();
        data.post_text = binding.postText.getText().toString();

        data.show_progress = binding.showProgress.isChecked();
        data.show_start = binding.showStart.isChecked();
        data.show_end = binding.showEnd.isChecked();
        data.show_years = binding.showYears.isChecked();
        data.show_months = binding.showMonths.isChecked();
        data.show_weeks = binding.showWeeks.isChecked();
        data.show_days = binding.showDays.isChecked();
        data.show_hours = binding.showHours.isChecked();
        data.show_minutes = binding.showMinutes.isChecked();
        data.show_seconds = binding.showSeconds.isChecked();
        data.terminate = binding.terminate.isChecked();
        data.notify = binding.notify.isChecked();

        return !errors;
    }

    public static class Datepicker_frag extends DialogFragment
    {
        public static final String STORE_DATE = "STORE_DATE";
        public static final String DATE = "DATE";

        @Override
        public Dialog onCreateDialog(Bundle saved_instance_state)
        {
            String date = getArguments().getString(DATE);
            int year, month, day;
            try
            {
                String[] date_components = date.split("-");
                year = Integer.parseInt(date_components[0]);
                month = Integer.parseInt(date_components[1]) - 1;
                day = Integer.parseInt(date_components[2]);
            }
            catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
            {
                Toast.makeText(getActivity(), "Invalid date: " + date + ". Correct format is: " +
                              getActivity().getResources().getString(R.string.date_format),
                        Toast.LENGTH_LONG).show();

                // set to stored date
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(getArguments().getLong(STORE_DATE, 0) * 1000);
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
            }

            return new DatePickerDialog(getActivity(), (Settings) getActivity(), year, month, day);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day)
    {
        String date = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, day);
        date_time_dialog_target.setText(date);
    }

    public static class Timepicker_frag extends DialogFragment
    {
        public static final String STORE_TIME = "STORE_TIME";
        public static final String TIME = "TIME";

        @Override
        public Dialog onCreateDialog(Bundle saved_instance_state)
        {
            String time = getArguments().getString(TIME);
            int hour, minute, second;
            try
            {
                String[] date_components = time.split(":");
                hour = Integer.parseInt(date_components[0]);
                minute = Integer.parseInt(date_components[1]);
                second = Integer.parseInt(date_components[2]);
            }
            catch(NumberFormatException | ArrayIndexOutOfBoundsException e)
            {
                Toast.makeText(getActivity(), "Invalid time: " + time + ". Correct format is: " +
                                              getActivity().getResources().getString(R.string.time_format),
                        Toast.LENGTH_LONG).show();

                // set to stored time
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(getArguments().getLong(STORE_TIME, 0) * 1000);
                hour = cal.get(Calendar.HOUR_OF_DAY);
                minute = cal.get(Calendar.MINUTE);
                second = cal.get(Calendar.SECOND);
            }

            return new TimePickerDialog(getActivity(), (Settings) getActivity(), hour, minute, true);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute)
    {
        String time = String.format(Locale.US, "%02d:%02d:00", hour, minute);
        date_time_dialog_target.setText(time);
    }

    public void on_start_cal_butt(View view)
    {
        date_time_dialog_target = binding.startDateSel;
        Datepicker_frag frag = new Datepicker_frag();
        Bundle args = new Bundle();
        args.putString(Datepicker_frag.DATE, binding.startDateSel.getText().toString());
        args.putLong(Datepicker_frag.STORE_DATE, data.start_time);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), "start_date_picker");
    }

    public void on_start_clock_butt(View view)
    {
        date_time_dialog_target = binding.startTimeSel;
        Timepicker_frag frag = new Timepicker_frag();
        Bundle args = new Bundle();
        args.putString(Timepicker_frag.TIME, binding.startTimeSel.getText().toString());
        args.putLong(Timepicker_frag.STORE_TIME, data.start_time);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), "start_time_picker");
    }

    public void on_end_cal_butt(View view)
    {
        date_time_dialog_target = binding.endDateSel;
        Datepicker_frag frag = new Datepicker_frag();
        Bundle args = new Bundle();
        args.putString(Datepicker_frag.DATE, binding.endDateSel.getText().toString());
        args.putLong(Datepicker_frag.STORE_DATE, data.end_time);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), "end_date_picker");
    }

    public void on_end_clock_butt(View view)
    {
        date_time_dialog_target = binding.endTimeSel;
        Timepicker_frag frag = new Timepicker_frag();
        Bundle args = new Bundle();
        args.putString(Timepicker_frag.TIME, binding.endTimeSel.getText().toString());
        args.putLong(Timepicker_frag.STORE_TIME, data.end_time);
        frag.setArguments(args);
        frag.show(getSupportFragmentManager(), "end_time_picker");
    }
    public void on_precision_butt(View view)
    {
        Precision_dialog_frag d = new Precision_dialog_frag();
        Bundle args = new Bundle();
        args.putInt(Precision_dialog_frag.PRECISION_ARG, data.precision);
        d.setArguments(args);
        d.show(getSupportFragmentManager(), "precision");
    }

    @Override
    public void on_precision_dialog_positive(Precision_dialog_frag dialog)
    {
        data.precision = dialog.getValue();
        binding.precision.setText(String.valueOf(data.precision));
    }
}
