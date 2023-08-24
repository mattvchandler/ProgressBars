/*
Copyright (C) 2020 Matthew Chandler

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
package org.mattvchandler.progressbars.util

import android.app.Dialog
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import org.mattvchandler.progressbars.BuildConfig
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.databinding.AboutDialogBinding

class About_dialog: DialogFragment()
{
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        val builder = AlertDialog.Builder(requireActivity())
        val binding = DataBindingUtil.inflate<AboutDialogBinding>(LayoutInflater.from(activity), R.layout.about_dialog, null, false)

        builder.setView(binding.root)
        binding.version.text = requireActivity().resources.getString(R.string.app_version, BuildConfig.VERSION_NAME)

        // allow clicking links in license and website text
        binding.license.movementMethod = LinkMovementMethod.getInstance()
        binding.website.movementMethod = LinkMovementMethod.getInstance()

        return builder.create()
    }
}
