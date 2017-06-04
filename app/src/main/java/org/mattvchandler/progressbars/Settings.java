package org.mattvchandler.progressbars;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.mattvchandler.progressbars.databinding.ActivitySettingsBinding;

public class Settings extends AppCompatActivity
{
    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        setSupportActionBar(binding.progressBarToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
