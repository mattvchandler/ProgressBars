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

package org.mattvchandler.progressbars.util

import android.content.Context
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import org.mattvchandler.progressbars.R

// extend AppCompatActivity to call recreate when the "dark_theme" preference changes
// and to set the correct theme when create is called
abstract class Dynamic_theme_activity: AppCompatActivity()
{
    private lateinit var theme: String
    override fun onCreate(savedInstanceState: Bundle?)
    {
        val (night_mode, new_theme) = get_current_night_mode(this)

        theme = new_theme
        AppCompatDelegate.setDefaultNightMode(night_mode)

        super.onCreate(savedInstanceState)
    }

    override fun onResume()
    {
        super.onResume()
        val new_theme = PreferenceManager.getDefaultSharedPreferences(this).getString("theme", resources.getString(R.string.theme_values_default))!!

        // has the theme changed? recreate this activity
        if(new_theme != theme)
            Handler().postDelayed({recreate()}, 0)
    }

    companion object
    {
        fun get_current_night_mode(context: Context): Pair<Int, String>
        {
            val theme = PreferenceManager.getDefaultSharedPreferences(context).getString("theme", context.resources.getString(R.string.theme_values_default))!!

            var night_mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

            when(theme)
            {
                context.resources.getString(R.string.theme_values_system)    -> night_mode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                context.resources.getString(R.string.theme_values_day)       -> night_mode = AppCompatDelegate.MODE_NIGHT_NO
                context.resources.getString(R.string.theme_values_night)     -> night_mode = AppCompatDelegate.MODE_NIGHT_YES
                context.resources.getString(R.string.theme_values_auto)      -> night_mode = AppCompatDelegate.MODE_NIGHT_AUTO // I know this is deprecated, but I don't care. There isn't a better alternative before Android Pie
                context.resources.getString(R.string.theme_values_auto_batt) -> night_mode = AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
            }

            return Pair(night_mode, theme)
        }
    }
}
