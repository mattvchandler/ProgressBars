package org.mattvchandler.progressbars;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.MenuItem;

import org.mattvchandler.progressbars.databinding.ActivityProgressBarsPrefsBinding;

// application settings screen
public class Progress_bar_prefs extends Dynamic_theme_activity
{
    public static class Progress_bar_prefs_frag extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            PreferenceManager.setDefaultValues(getActivity(), R.xml.preferences, false);
        }

        // register / unregister listener
        @Override
        public void onResume()
        {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        }
        @Override
        public void onPause()
        {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);

        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
        {
            if(key.equals("master_notification"))
            {
                // re-enable / disable all notification alarms when master notification setting is toggled
                Notification_handler.reset_all_alarms(getActivity());
            }
            else if(key.equals("dark_theme"))
            {
                // recreate this activity to apply the new theme
                getActivity().recreate();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        ActivityProgressBarsPrefsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_progress_bars_prefs);
        setSupportActionBar(binding.progressBarToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // put settings content into framelayout
        getFragmentManager().beginTransaction().replace(R.id.preferences, new Progress_bar_prefs_frag()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // make home button go back
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
