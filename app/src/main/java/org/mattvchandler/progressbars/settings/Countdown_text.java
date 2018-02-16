package org.mattvchandler.progressbars.settings;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MenuItem;

import org.mattvchandler.progressbars.R;
import org.mattvchandler.progressbars.databinding.ActivityCountdownTextBinding;
import org.mattvchandler.progressbars.db.Data;
import org.mattvchandler.progressbars.util.Dynamic_theme_activity;

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

// displays EditText widgets for setting countdown messages
public class Countdown_text extends Dynamic_theme_activity
{
    public static final String EXTRA_DATA = "org.mattvchandler.progressbars.EXTRA_DATA";
    public static final int RESULT_COUNTDOWN_TEXT = 1;
    private Data data;
    private ActivityCountdownTextBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_countdown_text);
        setSupportActionBar(binding.toolbar);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(savedInstanceState == null)
            data = (Data)getIntent().getSerializableExtra(EXTRA_DATA);
        else
            data = (Data)savedInstanceState.getSerializable(EXTRA_DATA);

        if(data == null)
            throw new InvalidParameterException("No data argument passed");

        binding.setData(data);
    }

    private void save()
    {
        data.pre_text       = binding.preText.getText().toString();
        data.start_text     = binding.startText.getText().toString();
        data.countdown_text = binding.countdownText.getText().toString();
        data.complete_text  = binding.completeText.getText().toString();
        data.post_text      = binding.postText.getText().toString();
    }

    @Override
    protected void onSaveInstanceState(Bundle out)
    {
        super.onSaveInstanceState(out);
        // save all data to be restored
        save();
        out.putSerializable(EXTRA_DATA, data);
    }

    private void go_back()
    {
        save();
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA, data);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed()
    {
        go_back();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // make home button go back
        switch(item.getItemId())
        {
        case android.R.id.home:
            go_back();
            return true;
        }
        return false;
    }
}
