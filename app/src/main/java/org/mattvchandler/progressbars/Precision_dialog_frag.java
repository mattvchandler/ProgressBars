package org.mattvchandler.progressbars;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.widget.NumberPicker;

public class Precision_dialog_frag extends DialogFragment
{
    public static final String PRECISION_ARG = "precision";

    private int value;

    public interface NoticeDialogListener
    {
        public void on_precision_dialog_positive(Precision_dialog_frag dialog);
    }

    NoticeDialogListener listener;

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch(ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public int getValue()
    {
        return value;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        value = getArguments().getInt(PRECISION_ARG);

        final NumberPicker np = new NumberPicker(builder.getContext());
        np.setMinValue(0);
        np.setMaxValue(10);
        np.setValue(value);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                value = newVal;
            }
        });

        builder.setMessage(R.string.precision)
                .setView(np)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        value = np.getValue();
                        listener.on_precision_dialog_positive(Precision_dialog_frag.this);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        return builder.create();
    }
}
