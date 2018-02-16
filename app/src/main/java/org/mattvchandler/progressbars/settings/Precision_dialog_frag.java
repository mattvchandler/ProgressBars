package org.mattvchandler.progressbars.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;

import org.mattvchandler.progressbars.R;

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

// a number_picker fragment for precision setting
public class Precision_dialog_frag extends DialogFragment
{
    public static final String PRECISION_ARG = "precision";

    private NumberPicker np;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);
        //noinspection ConstantConditions
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // unpack and set the starting value
        int precision;
        if(savedInstanceState == null)
        {
            //noinspection ConstantConditions
            precision = getArguments().getInt(PRECISION_ARG);
        }
        else
        {
            //noinspection ConstantConditions
            precision = savedInstanceState.getInt(PRECISION_ARG);
        }

        // build a number picker with range 0-10
        np = new NumberPicker(builder.getContext());
        np.setMinValue(0);
        np.setMaxValue(10);
        np.setValue(precision);

        // create a dialog containing the number picker, w/ OK and CANCEL buttons
        builder.setTitle(R.string.precision)
                .setView(np)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // ON OK, set the value one last time, and call the listener
                        ((Settings)getActivity()).on_precision_set(np.getValue());
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        // return the finished dialog
        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle out)
    {
        out.putInt(PRECISION_ARG, np.getValue());
    }
}
