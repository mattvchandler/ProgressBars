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
