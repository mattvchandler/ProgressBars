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

        // can't set mipmap resources from XML, so set it here
        binding.logo.setImageResource(R.mipmap.progress_bar_launcher_icon);

        // also can't get version number set it here
        binding.version.setText(getActivity().getResources().getString(R.string.app_version, BuildConfig.VERSION_NAME));

        // allow clicking links in license and website text
        binding.license.setMovementMethod(LinkMovementMethod.getInstance());
        binding.website.setMovementMethod(LinkMovementMethod.getInstance());

        return builder.create();
    }
}
