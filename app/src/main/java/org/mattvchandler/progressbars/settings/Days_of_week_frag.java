package org.mattvchandler.progressbars.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import org.mattvchandler.progressbars.db.Table;
import org.mattvchandler.progressbars.R;

/*
Copyright (C) 2017 Matthew Chandler

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

// Dialog box for choosing days of the week
public class Days_of_week_frag extends DialogFragment
{
    public static final String DAYS_OF_WEEK_ARG = "DAYS_OF_WEEK";
    private int days_of_week;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        if(savedInstanceState == null)
            days_of_week = getArguments().getInt(DAYS_OF_WEEK_ARG);
        else
            days_of_week = savedInstanceState.getInt(DAYS_OF_WEEK_ARG);

        boolean selected[] = new boolean[Table.Days_of_week.values().length];
        for(Table.Days_of_week day : Table.Days_of_week.values())
        {
            selected[day.index] = (days_of_week & day.mask) != 0;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.days_of_week_title)
                .setMultiChoiceItems(R.array.day_of_week, selected,
                        new DialogInterface.OnMultiChoiceClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked)
                            {
                                if(isChecked)
                                    days_of_week |= (1 << which);
                                else
                                    days_of_week &= ~(1 << which);
                            }
                        })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                if(days_of_week == 0)
                                {
                                    Toast.makeText(getContext(), R.string.no_days_of_week_err, Toast.LENGTH_LONG).show();
                                }
                                else
                                {
                                    // binding.repeatDaysOfWeek.setText(get_days_of_week_abbr(getContext(), days_of_week));
                                    ((Settings)getActivity()).on_days_of_week_set(days_of_week);
                                }
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle out)
    {
        out.putInt(DAYS_OF_WEEK_ARG, days_of_week);
    }
}
