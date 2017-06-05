package org.mattvchandler.progressbars;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.databinding.DataBindingUtil;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import org.mattvchandler.progressbars.databinding.ActivitySettingsBinding;

import java.util.TimeZone;

public class Settings extends AppCompatActivity
{
    private ActivitySettingsBinding binding;

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

}
