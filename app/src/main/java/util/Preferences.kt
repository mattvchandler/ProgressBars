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

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
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
            PreferenceManager.setDefaultValues(requireActivity(), R.xml.preferences, false)
            findPreference<ListPreference>(resources.getString(R.string.pref_date_format_key))?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            findPreference<ListPreference>(resources.getString(R.string.pref_first_day_of_wk_key))?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            findPreference<ListPreference>(resources.getString(R.string.pref_widget_refresh_key))?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            findPreference<ListPreference>(resources.getString(R.string.pref_widget_text_color_key))?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
            findPreference<ListPreference>(resources.getString(R.string.pref_theme_key))?.summaryProvider = ListPreference.SimpleSummaryProvider.getInstance()
        }

        // register / unregister listener
        override fun onResume()
        {
            super.onResume()
            preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)

        }

        override fun onPause()
        {
            super.onPause()
            preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?)
        {
            if(key == resources.getString(R.string.pref_theme_key))
            {
                // request location permission for local sunset / sunrise times
                if(sharedPreferences?.getString(resources.getString(R.string.pref_theme_key), "") == resources.getString(R.string.pref_theme_value_auto))
                {
                    if (ContextCompat.checkSelfPermission(activity as Context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                    {
                        if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
                        {
                            class Location_frag: DialogFragment()
                            {
                                override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
                                        AlertDialog.Builder(requireContext())
                                                .setTitle(R.string.loc_perm_title)
                                                .setMessage(R.string.loc_perm_msg)
                                                .setPositiveButton(android.R.string.ok) { _, _ -> ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_RESPONSE)}
                                                .setNegativeButton(android.R.string.cancel, null)
                                                .create()
                            }
                            Location_frag().show(requireActivity().supportFragmentManager, "location_permission_dialog")
                        }
                        else
                        {
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_RESPONSE)
                        }
                    }
                }
                // recreate this activity to apply the new theme
                activity?.recreate()
            }
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean
        {
            when(preference.key)
            {
                resources.getString(R.string.pref_system_notifications_key) ->
                {
                    val intent = Intent()
                    when
                    {
                        Build.VERSION.SDK_INT in 21..25 ->
                        {
                            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
                            intent.putExtra("app_package", requireContext().packageName)
                            intent.putExtra("app_uid", requireContext().applicationInfo.uid)
                        }
                        Build.VERSION.SDK_INT >= 26 ->
                        {
                            intent.action = android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS
                            intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                        }
                        else ->
                        {
                            intent.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            intent.addCategory(Intent.CATEGORY_DEFAULT)
                            intent.data = Uri.parse("package:${requireContext().packageName}")
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

        consume_insets(this, binding.preferences, binding.appbarLayout)

        // put settings content into frame layout
        supportFragmentManager.beginTransaction().replace(R.id.preferences, Progress_bar_prefs_frag()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId)
        {
            // make home button go back
            android.R.id.home -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
}
