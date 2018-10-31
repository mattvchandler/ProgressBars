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

package org.mattvchandler.progressbars.settings

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.view.MenuItem

import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.databinding.ActivityCountdownTextBinding
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.util.Dynamic_theme_activity

import java.security.InvalidParameterException

// displays EditText widgets for setting countdown messages
class Countdown_text: Dynamic_theme_activity()
{
    private var data: Data? = null
    private lateinit var binding: ActivityCountdownTextBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_countdown_text)
        setSupportActionBar(binding.toolbar)
        if(supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        data = if(savedInstanceState == null)
            intent.getSerializableExtra(EXTRA_DATA) as Data
        else
            savedInstanceState.getSerializable(EXTRA_DATA) as Data

        if(data == null)
            throw InvalidParameterException("No data argument passed")

        binding.data = data
    }

    private fun save()
    {
        data!!.pre_text = binding.preText.text!!.toString()
        data!!.start_text = binding.startText.text!!.toString()
        data!!.countdown_text = binding.countdownText.text!!.toString()
        data!!.complete_text = binding.completeText.text!!.toString()
        data!!.post_text = binding.postText.text!!.toString()
    }

    override fun onSaveInstanceState(out: Bundle)
    {
        super.onSaveInstanceState(out)
        // save all data to be restored
        save()
        out.putSerializable(EXTRA_DATA, data)
    }

    private fun go_back()
    {
        save()
        val intent = Intent()
        intent.putExtra(EXTRA_DATA, data)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed()
    {
        go_back()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // make home button go back
        when(item.itemId)
        {
            android.R.id.home ->
            {
                go_back()
                return true
            }
        }
        return false
    }

    companion object
    {
        const val EXTRA_DATA = "org.mattvchandler.progressbars.EXTRA_DATA"
        const val RESULT_COUNTDOWN_TEXT = 1
    }
}
