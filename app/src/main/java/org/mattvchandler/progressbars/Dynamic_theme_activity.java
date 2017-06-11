package org.mattvchandler.progressbars;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

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
