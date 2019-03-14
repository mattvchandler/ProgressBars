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

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.databinding.ActivityPreferencesBinding

// application settings screen
class Preferences: Dynamic_theme_activity()
{
    companion object
    {
        private const val LOCATION_PERMISSION_RESPONSE = 1
    }
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
            if(key == "theme")
            {
                // request location permission for local sunset / sunrise times
                if(sharedPreferences.getString("theme", "") == resources.getString(R.string.theme_values_auto))
                {
                    if (ContextCompat.checkSelfPermission(activity as Context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(activity!!, Manifest.permission.ACCESS_FINE_LOCATION))
                        {
                            class Location_frag: DialogFragment()
                            {
                                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
                                        AlertDialog.Builder(activity)
                                                .setTitle(R.string.loc_perm_title)
                                                .setMessage(R.string.loc_perm_msg)
                                                .setPositiveButton(android.R.string.ok) { _, _ -> ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_RESPONSE)}
                                                .setNegativeButton(android.R.string.cancel, null)
                                                .create()
                            }
                            Location_frag().show(activity!!.supportFragmentManager, "location_permission_dialog")
                        }
                        else
                        {
                            ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_RESPONSE)
                        }
                    }
                }
                // recreate this activity to apply the new theme
                activity?.recreate()
            }
        }

        override fun onPreferenceTreeClick(preference: Preference?): Boolean
        {
            when(preference?.key)
            {
                "system_notifications" ->
                {
                    val intent = Intent()
                    when
                    {
                        Build.VERSION.SDK_INT in 21..25 ->
                        {
                            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                            intent.putExtra("app_package", context!!.packageName)
                            intent.putExtra("app_uid", context!!.applicationInfo.uid)
                        }
                        Build.VERSION.SDK_INT > 26 ->
                        {
                            intent.action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, context!!.packageName)
                        }
                        else ->
                        {
                            intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.data = Uri.parse("package:" + context!!.packageName)
                        }
                    }

                    startActivity(intent)
                    return false
                }

                else -> return super.onPreferenceTreeClick(preference)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityPreferencesBinding>(this, R.layout.activity_preferences)
        setSupportActionBar(binding.toolbar as Toolbar)
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
