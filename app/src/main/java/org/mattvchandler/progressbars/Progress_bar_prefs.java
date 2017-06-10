package org.mattvchandler.progressbars;

import android.databinding.DataBindingUtil;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import org.mattvchandler.progressbars.databinding.ActivityProgressBarsPrefsBinding;

public class Progress_bar_prefs extends AppCompatActivity
{
    public static class Progress_bar_prefs_frag extends PreferenceFragment
    {
        @Override
        public void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
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
