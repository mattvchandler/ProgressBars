package org.mattvchandler.progressbars;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;

import org.mattvchandler.progressbars.db.Data;
import org.mattvchandler.progressbars.db.Undo;
import org.mattvchandler.progressbars.list.Adapter;
import org.mattvchandler.progressbars.list.Touch_helper_callback;
import org.mattvchandler.progressbars.settings.Settings;
import org.mattvchandler.progressbars.util.About_dialog_frag;
import org.mattvchandler.progressbars.util.Dynamic_theme_activity;
import org.mattvchandler.progressbars.util.Preferences;
import org.mattvchandler.progressbars.databinding.ActivityProgressBarsBinding;

import java.util.NoSuchElementException;

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

// main activity. display each timer in a list
public class Progress_bars extends Dynamic_theme_activity
{
    private ActivityProgressBarsBinding binding;
    private Adapter adapter;

    public static final String EXTRA_SCROLL_TO_ROWID = "org.mattvchandler.progressbars.SCROLL_TO_ROWID";

    private String date_format;
    private boolean hour_24;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_progress_bars);
        setSupportActionBar(binding.toolbar);
        binding.mainList.addItemDecoration(new DividerItemDecoration(binding.mainList.getContext(), DividerItemDecoration.VERTICAL));

        // save date format to detect when it changes
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        date_format = prefs.getString("date_format", getResources().getString(R.string.pref_date_format_default));
        hour_24 = prefs.getBoolean("hour_24", getResources().getBoolean(R.bool.pref_hour_24_default));

        // set up row Adapter
        adapter = new Adapter(this);

        binding.mainList.setLayoutManager(new LinearLayoutManager(this));
        binding.mainList.setAdapter(adapter);

        ItemTouchHelper touch_helper = new ItemTouchHelper(new Touch_helper_callback(adapter));
        touch_helper.attachToRecyclerView(binding.mainList);

        long scroll_to_rowid = getIntent().getLongExtra(EXTRA_SCROLL_TO_ROWID, -1);
        if(scroll_to_rowid >= 0)
        {
            try
            {
                binding.mainList.scrollToPosition(adapter.find_by_rowid(scroll_to_rowid));
            }
            catch(NoSuchElementException ignored) {}
        }
        // start running each second
        new update().run();

        // register to receive notifications of DB changes
        LocalBroadcastManager.getInstance(this).registerReceiver(on_db_change, new IntentFilter(Data.DB_CHANGED_EVENT));
    }

    @Override
    protected void onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(on_db_change);
        super.onDestroy();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // check to see if date format has changed. rebuild activity with new format if it has
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String new_date_format = prefs.getString("date_format", getResources().getString(R.string.pref_date_format_default));
        Boolean new_hour_24 = prefs.getBoolean("hour_24", getResources().getBoolean(R.bool.pref_hour_24_default));

        if(!new_date_format.equals(date_format) || new_hour_24 != hour_24)
        {
            recreate();
        }
    }

    private final BroadcastReceiver on_db_change = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            String change_type = intent.getStringExtra(Data.DB_CHANGED_TYPE);
            if(change_type.equals(Data.INSERT) || change_type.equals(Data.UPDATE))
            {
                long rowid = intent.getLongExtra(Data.DB_CHANGED_ROWID, -1);
                if(rowid > 0)
                    binding.mainList.scrollToPosition(adapter.find_by_rowid(rowid));
            }

            invalidateOptionsMenu();
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // set toolbar menu
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.progress_bar_action_bar, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // dis/enable undo-redo buttons as needed
        super.onPrepareOptionsMenu(menu);

        MenuItem undo_butt = menu.findItem(R.id.undo);
        MenuItem redo_butt = menu.findItem(R.id.redo);

        if(Undo.can_undo(this))
        {
            undo_butt.setEnabled(true);
            undo_butt.getIcon().setAlpha(255);
        }
        else
        {
            undo_butt.setEnabled(false);
            undo_butt.getIcon().setAlpha(255 / 3);
        }
        if(Undo.can_redo(this))
        {
            redo_butt.setEnabled(true);
            redo_butt.getIcon().setAlpha(255);
        }
        else
        {
            redo_butt.setEnabled(false);
            redo_butt.getIcon().setAlpha(255 / 3);
        }

        return true;
    }

    // handle toolbar menu presses
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
        case R.id.add_butt:
            // open editor with no rowid set
            startActivity(new Intent(this, Settings.class));
            return true;

        case R.id.undo:
            Undo.apply(this, Undo.UNDO);
            return true;

        case R.id.redo:
            Undo.apply(this, Undo.REDO);
            return true;

        case R.id.settings:
            // open app settings menu
            startActivity(new Intent(this, Preferences.class));
            return true;

        case R.id.about:
            // show about dialog
            new About_dialog_frag().show(getSupportFragmentManager(), "about");
            return  true;
        }
        return false;
    }

    private class update implements Runnable
    {
        private final Handler handler = new Handler();
        private final int delay = 1000; // 1000ms

        @Override
        public void run()
        {
            for (int i = 0; i < adapter.getItemCount(); ++i)
            {
                Adapter.Progress_bar_row_view_holder view_holder = (Adapter.Progress_bar_row_view_holder) binding.mainList.findViewHolderForAdapterPosition(i);
                if(view_holder != null)
                    view_holder.update();
            }

            handler.postDelayed(this, delay);
        }
    }
}
