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
import android.widget.TimePicker;
import android.widget.Toast;

import org.mattvchandler.progressbars.databinding.ActivitySettingsBinding;

import java.util.TimeZone;

public class Settings extends AppCompatActivity implements Precision_dialog_frag.NoticeDialogListener
{
    public static final String EXRTA_EDIT_ROW_ID = "org.mattvchandler.progressbars.EDIT_ROW_ID";
    public static final String RESULT_ROW_ID = "org.mattvchandler.progressbars.RESULT_ROW_ID";
    public static final String RESULT_NEW_ROW = "org.mattvchandler.progressbars.RESULT_NEW_ROW";

    public static final String STATE_DATA = "data";

    private ActivitySettingsBinding binding;

    private Progress_bar_data data;

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

        for(int i = 0; i < tz_adapter.getCount(); ++i)
        {
            if(tz_adapter.getItem(i).equals(TimeZone.getDefault().getID()))
            {
                binding.startTz.setSelection(i);
                binding.endTz.setSelection(i);
                break;
            }
        }

        // only run this on 1st creation
        if(savedInstanceState == null)
        {
            String rowid_in = getIntent().getStringExtra(EXRTA_EDIT_ROW_ID);

            // no rowid passed? make a new one
            if(rowid_in == null)
            {
                SQLiteDatabase db = new Progress_bar_DB(this).getWritableDatabase();

                data = new Progress_bar_data();

                Cursor cursor = db.rawQuery("SELECT MAX(" + Progress_bar_contract.Progress_bar_table.ORDER_COL + ") + 1 AS new_order FROM " + Progress_bar_contract.Progress_bar_table.TABLE_NAME, null);
                cursor.moveToFirst();
                data.order = cursor.getLong(0);
                cursor.close();
                db.close();
            }
            else
            {
                // get data from row
            }

            Toast.makeText(this, "Editing row: " + data.rowid, Toast.LENGTH_SHORT).show();
        }
        else
        {
            data = (Progress_bar_data)savedInstanceState.getSerializable(STATE_DATA);
        }

        binding.setData(data);
    }

    @Override
    protected void onSaveInstanceState(Bundle out)
    {
        super.onSaveInstanceState(out);
        out.putSerializable(STATE_DATA, data);
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
                Intent intent = new Intent();

                // get data from widgets (some have been set through callbacks already)
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

                if(data.rowid < 0)
                {
                    data.insert(this);
                    intent.putExtra(RESULT_NEW_ROW, true);
                }
                else
                {
                    
                }

                intent.putExtra(RESULT_ROW_ID, data.rowid);
                setResult(RESULT_OK, intent);
                finish();
                return true;
        }
        return false;
    }

    public static class Timepicker_frag extends DialogFragment implements TimePickerDialog.OnTimeSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle saved_instance_state)
        {
            return new TimePickerDialog(getActivity(), this, 0, 0, false);
        }

        @Override
        public void onTimeSet(TimePicker view, int hour, int minute)
        {
            Toast.makeText(getActivity(), "Time chosen was: " + String.valueOf(hour) + ":" + String.valueOf(minute), Toast.LENGTH_SHORT).show();
        }
    }

    public static class Datepicker_frag extends DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle saved_instance_state)
        {
            return new DatePickerDialog(getActivity(), this, 2101, 0, 1);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int day)
        {
            Toast.makeText(getActivity(), "Date chosen was: " + String.valueOf(year) + "-" + String.valueOf(month) + "-" + String.valueOf(day), Toast.LENGTH_SHORT).show();
        }
    }

    public void on_start_clock_butt(View view)
    {
        new Timepicker_frag().show(getSupportFragmentManager(), "start_time_picker");
    }

    public void on_start_cal_butt(View view)
    {
        new Datepicker_frag().show(getSupportFragmentManager(), "start_date_picker");
    }

    public void on_end_clock_butt(View view)
    {
        new Timepicker_frag().show(getSupportFragmentManager(), "end_time_picker");
    }

    public void on_end_cal_butt(View view)
    {
        new Datepicker_frag().show(getSupportFragmentManager(), "end_date_picker");
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
