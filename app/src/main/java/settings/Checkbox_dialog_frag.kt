/*
Copyright (C) 2019 Matthew Chandler

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

package org.mattvchandler.progressbars.settings

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.security.InvalidParameterException

// Dialog box w/ checkboxes
class Checkbox_dialog_frag: DialogFragment()
{
    private var selection: BooleanArray? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        super.onCreateDialog(savedInstanceState)

        selection = if(savedInstanceState == null)
            arguments!!.getBooleanArray(SELECTION_ARG)
        else
            savedInstanceState.getBooleanArray(SELECTION_ARG)

        if(selection == null)
            throw InvalidParameterException("No selection specified")

        val builder = AlertDialog.Builder(activity!!)
        builder.setTitle(arguments!!.getInt(TITLE_ARG))
                .setMultiChoiceItems(arguments!!.getInt(ENTRIES_ARG), selection ) { _, which, isChecked -> selection!![which] = isChecked }
                .setPositiveButton(android.R.string.ok) { _, _ -> (activity as Settings).on_checkbox_dialog_ok(tag!!, selection!!) }
                .setNegativeButton(android.R.string.cancel, null)

        return builder.create()
    }

    override fun onSaveInstanceState(out: Bundle)
    {
        out.putBooleanArray(SELECTION_ARG, selection)
    }

    companion object
    {
        const val TITLE_ARG = "TITLE_ARG"
        const val ENTRIES_ARG = "ENTRIES_ARG"
        const val SELECTION_ARG = "SELECTION_ARG"
    }
}
