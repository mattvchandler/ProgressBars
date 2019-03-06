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
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.NumberPicker

import org.mattvchandler.progressbars.R

// a number_picker fragment for precision setting
class Precision_dialog_frag: DialogFragment()
{
    private lateinit var np: NumberPicker

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog
    {
        super.onCreateDialog(savedInstanceState)

        val builder = AlertDialog.Builder(activity!!)

        // unpack and set the starting value
        val precision: Int = savedInstanceState?.getInt(PRECISION_ARG) ?: arguments!!.getInt(PRECISION_ARG)

        // build a number picker with range 0-10
        np = NumberPicker(builder.context)
        np.minValue = 0
        np.maxValue = 10
        np.value = precision

        val margin_size = builder.context.resources.getDimensionPixelSize(R.dimen.margin_size)
        val layout_params = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        layout_params.setMargins(margin_size, margin_size, margin_size, margin_size)
        layout_params.gravity = Gravity.CENTER
        np.layoutParams = layout_params

        val layout = FrameLayout(builder.context)
        layout.addView(np)

        // create a dialog containing the number picker, w/ OK and CANCEL buttons
        builder.setTitle(R.string.precision)
                .setView(layout)
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // ON OK, set the value one last time, and call the listener
                    (activity as Settings).on_precision_set(np.value)
                }
                .setNegativeButton(android.R.string.cancel, null)
        // return the finished dialog
        return builder.create()
    }

    override fun onSaveInstanceState(out: Bundle)
    {
        out.putInt(PRECISION_ARG, np.value)
    }

    companion object
    {
        const val PRECISION_ARG = "precision"
    }
}
