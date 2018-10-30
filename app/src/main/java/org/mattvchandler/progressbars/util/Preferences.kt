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

import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.preference.PreferenceManager
import android.os.Bundle
import android.view.MenuItem
import android.support.v7.preference.PreferenceFragmentCompat

import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.databinding.ActivityPreferencesBinding

// application settings screen
class Preferences: Dynamic_theme_activity()
{
    class Progress_bar_prefs_frag: PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener
    {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
        {
            setPreferencesFromResource(R.xml.preferences, rootKey)
            PreferenceManager.setDefaultValues(activity, R.xml.preferences, false)
        }

        // register / unregister listener
        override fun onResume()
        {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        }

        override fun onPause()
        {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)

        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String)
        {
            if(key == "master_notification")
            {
                // re-enable / disable all notification alarms when master notification setting is toggled
                activity?.let { reset_all_alarms(it) }
            }
            else if(key == "dark_theme")
            {
                // recreate this activity to apply the new theme
                activity?.recreate()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityPreferencesBinding>(this, R.layout.activity_preferences)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // put settings content into frame layout
        supportFragmentManager.beginTransaction().replace(R.id.preferences, Progress_bar_prefs_frag()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        // make home button go back
        when(item.itemId)
        {
            android.R.id.home -> { finish(); return true }
        }
        return false
    }
}
