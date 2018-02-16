package org.mattvchandler.progressbars.util;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.mattvchandler.progressbars.R;

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

// extend AppCompatActivity to call recreate when the "dark_theme" preference changes
// and to set the correct theme when create is called
public abstract class Dynamic_theme_activity extends AppCompatActivity
{
    private boolean dark_theme = false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        // get and set the theme from preferences
        dark_theme = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("dark_theme", false);
        setTheme(dark_theme ? R.style.Theme_progress_bars_dark : R.style.Theme_progress_bars);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        boolean new_dark_theme = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("dark_theme", false);

        // has the theme changed? recreate this activity
        if(new_dark_theme != dark_theme)
            recreate();
    }
}
