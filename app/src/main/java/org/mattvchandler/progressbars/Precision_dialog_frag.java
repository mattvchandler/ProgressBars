package org.mattvchandler.progressbars;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;

// a number_picker fragment for precision setting
public class Precision_dialog_frag extends DialogFragment
{
    public static final String PRECISION_ARG = "precision";

    private int value;

    public interface NoticeDialogListener
    {
        void on_precision_dialog_positive(Precision_dialog_frag dialog);
    }

    private NoticeDialogListener listener;

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

    // read the chosen value. this is apparently the cleanest way to get it out
    public int getValue()
    {
        return value;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // unpack and set the starting value
        value = getArguments().getInt(PRECISION_ARG);

        // build a number picker with range 0-10
        final NumberPicker np = new NumberPicker(builder.getContext());
        np.setMinValue(0);
        np.setMaxValue(10);
        np.setValue(value);

        // listen for changes, and update value when they happen
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener()
        {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal)
            {
                value = newVal;
            }
        });

        // create a dialog containing the number picker, w/ OK and CANCEL buttons
        builder.setMessage(R.string.precision)
                .setView(np)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        // ON OK, set the value one last time, and call the listener
                        value = np.getValue();
                        listener.on_precision_dialog_positive(Precision_dialog_frag.this);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null);
        // return the finished dialog
        return builder.create();
    }
}
