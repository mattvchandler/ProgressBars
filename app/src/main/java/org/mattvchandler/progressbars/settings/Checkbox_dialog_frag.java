package org.mattvchandler.progressbars.settings;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import java.security.InvalidParameterException;

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

// Dialog box w/ checkboxes
public class Checkbox_dialog_frag extends DialogFragment
{
    public static final String TITLE_ARG = "TITLE_ARG";
    public static final String ENTRIES_ARG = "ENTRIES_ARG";
    public static final String SELECTION_ARG = "SELECTION_ARG";
    private boolean selection[];

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        super.onCreateDialog(savedInstanceState);

        if(savedInstanceState == null)
            selection = getArguments().getBooleanArray(SELECTION_ARG);
        else
            selection = savedInstanceState.getBooleanArray(SELECTION_ARG);

        if(selection == null)
            throw new InvalidParameterException("No selection specified");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getArguments().getInt(TITLE_ARG))
                .setMultiChoiceItems(getArguments().getInt(ENTRIES_ARG), selection,
                        new DialogInterface.OnMultiChoiceClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked)
                            {
                                selection[which] = isChecked;
                            }
                        })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                ((Settings)getActivity()).on_checkbox_dialog_ok(getTag(), selection);
                            }
                        })
                .setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    @Override
    public void onSaveInstanceState(Bundle out)
    {
        out.putBooleanArray(SELECTION_ARG, selection);
    }
}
