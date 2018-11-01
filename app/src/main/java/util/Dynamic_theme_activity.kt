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

package org.mattvchandler.progressbars.util

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate

import org.mattvchandler.progressbars.R

// extend AppCompatActivity to call recreate when the "dark_theme" preference changes
// and to set the correct theme when create is called
abstract class Dynamic_theme_activity: AppCompatActivity()
{
    private lateinit var theme: String
    override fun onCreate(savedInstanceState: Bundle?)
    {
        theme = PreferenceManager.getDefaultSharedPreferences(this).getString("theme", resources.getString(R.string.theme_values_default))!!

        var night_mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

        when(theme)
        {
            resources.getString(R.string.theme_values_system) -> night_mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            resources.getString(R.string.theme_values_day)    -> night_mode = AppCompatDelegate.MODE_NIGHT_NO
            resources.getString(R.string.theme_values_night)  -> night_mode = AppCompatDelegate.MODE_NIGHT_YES
            resources.getString(R.string.theme_values_auto)   -> night_mode = AppCompatDelegate.MODE_NIGHT_AUTO
        }
        AppCompatDelegate.setDefaultNightMode(night_mode)

        super.onCreate(savedInstanceState)
    }

    override fun onResume()
    {
        super.onResume()
        val new_theme = PreferenceManager.getDefaultSharedPreferences(this).getString("theme", resources.getString(R.string.theme_values_default))!!

        // has the theme changed? recreate this activity
        if(new_theme != theme)
            recreate()
    }
}
