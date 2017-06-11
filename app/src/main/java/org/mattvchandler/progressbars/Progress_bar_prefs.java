package org.mattvchandler.progressbars;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.MenuItem;

import org.mattvchandler.progressbars.databinding.ActivityProgressBarsPrefsBinding;

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
            if(key.equals("dark_theme"))
            {
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

        getFragmentManager().beginTransaction().replace(R.id.preferences, new Progress_bar_prefs_frag()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case android.R.id.home:
                finish();
                return true;
        }
        return false;
    }
}
