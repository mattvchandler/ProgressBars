package org.mattvchandler.progressbars;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;

import org.mattvchandler.progressbars.databinding.AboutDialogBinding;

public class About_dialog_frag extends DialogFragment
{
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        AboutDialogBinding binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.about_dialog, null, false);

        builder.setView(binding.getRoot());

        binding.logo.setImageResource(R.mipmap.progress_bar_launcher_icon);
        binding.version.setText("v. " + BuildConfig.VERSION_NAME);

        binding.license.setMovementMethod(LinkMovementMethod.getInstance());
        binding.website.setMovementMethod(LinkMovementMethod.getInstance());

        return builder.create();
    }
}
