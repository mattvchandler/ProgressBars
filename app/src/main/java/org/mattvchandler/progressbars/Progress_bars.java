package org.mattvchandler.progressbars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.mattvchandler.progressbars.databinding.ActivityProgressBarsBinding;

import java.util.NoSuchElementException;

/*
Copyright (C) 2017 Matthew Chandler

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
    private Progress_bar_adapter adapter;

    public static final int UPDATE_REQUEST = 1;
    public static final String EXTRA_SCROLL_TO_ROWID = "org.mattvchandler.progressbars.SCROLL_TO_ROWID";

    private String date_format;
    private boolean hour_24;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_progress_bars);
        setSupportActionBar(binding.progressBarToolbar);
        binding.mainList.addItemDecoration(new DividerItemDecoration(binding.mainList.getContext(), DividerItemDecoration.VERTICAL));

        // save date format to detect when it changes
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        date_format = prefs.getString("date_format", getResources().getString(R.string.pref_date_format_default));
        hour_24 = prefs.getBoolean("hour_24", true);

        // on first run, create a new prog bar if DB is empty
        if(savedInstanceState == null)
        {
            SQLiteDatabase db = new Progress_bar_DB(this).getReadableDatabase();
            Cursor cursor = db.rawQuery(Progress_bar_table.SELECT_ALL_ROWS, null);
            if(cursor.getCount() == 0)
            {
                new Progress_bar_data(this).insert(this);
            }
            else
            {
                // clean up existing orders. make them sequential
                Progress_bar_table.cleanup_order(this);
            }
            cursor.close();
            db.close();

            Notification_handler.reset_all_alarms(this);
        }

        // set up row adapter
        adapter = new Progress_bar_adapter(this);

        binding.mainList.setLayoutManager(new LinearLayoutManager(this));
        binding.mainList.setAdapter(adapter);

        ItemTouchHelper touch_helper = new ItemTouchHelper(new Progress_bar_row_touch_helper_callback(adapter));
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
    }

    @Override
    public void onResume()
    {
        super.onResume();

        // check to see if date format has changed. rebuild activity with new format if it has
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String new_date_format = prefs.getString("date_format", getResources().getString(R.string.pref_date_format_default));
        boolean new_hour_24 = prefs.getBoolean("hour_24", true);

        if(!new_date_format.equals(date_format) || new_hour_24 != hour_24)
        {
            recreate();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // set toolbar menu
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.progress_bar_action_bar, menu);
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
            startActivityForResult(new Intent(this, Settings.class), UPDATE_REQUEST);
            return true;

        case R.id.settings:
            // open app settings menu
            startActivity(new Intent(this, Progress_bar_prefs.class));
            return true;

        case R.id.about:
            // show about dialog
            new About_dialog_frag().show(getSupportFragmentManager(), "about");
            return  true;
        }
        return false;
    }

    // catch return from adding or editing a row
    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data)
    {
        if(request_code == UPDATE_REQUEST && result_code == RESULT_OK)
        {
            // get new data and keep a backup of old
            final Progress_bar_data new_data = (Progress_bar_data)data.getSerializableExtra(Settings.RESULT_NEW_DATA);
            final Progress_bar_data old_data = (Progress_bar_data)data.getSerializableExtra(Settings.RESULT_OLD_DATA);

            adapter.reset_cursor();

            // was a row added?
            if(old_data == null)
            {
                adapter.notifyItemInserted(adapter.getItemCount());

                // show message and offer undo action
                Snackbar.make(binding.mainList, getResources().getString(R.string.added_new,  new_data.title), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.undo), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                // delete the new row
                                int pos = adapter.find_by_rowid(new_data.rowid);
                                new_data.delete(Progress_bars.this);

                                // inform the adapter of the changes
                                adapter.reset_cursor();
                                adapter.notifyItemRemoved(pos);
                            }
                        }).show();
            }
            else // an existing row was changed
            {
                adapter.notifyItemChanged(adapter.find_by_rowid(new_data.rowid));

                // show message and offer undo action
                Snackbar.make(binding.mainList, getResources().getString(R.string.saved, new_data.title), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.undo), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                // update DB with old info
                                old_data.update(Progress_bars.this);

                                adapter.reset_cursor();
                                adapter.notifyItemChanged(adapter.find_by_rowid(old_data.rowid));
                            }
                        }).show();
            }
        }
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
                Progress_bar_adapter.Progress_bar_row_view_holder view_holder = (Progress_bar_adapter.Progress_bar_row_view_holder) binding.mainList.findViewHolderForAdapterPosition(i);
                if(view_holder != null)
                    view_holder.update();
            }

            handler.postDelayed(this, delay);
        }
    }
}
