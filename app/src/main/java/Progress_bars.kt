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

package org.mattvchandler.progressbars

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.Menu
import android.view.MenuItem

import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.db.Undo
import org.mattvchandler.progressbars.list.Adapter
import org.mattvchandler.progressbars.list.Touch_helper_callback
import org.mattvchandler.progressbars.settings.Settings
import org.mattvchandler.progressbars.util.About_dialog
import org.mattvchandler.progressbars.util.Dynamic_theme_activity
import org.mattvchandler.progressbars.util.Notification_handler
import org.mattvchandler.progressbars.util.Preferences
import org.mattvchandler.progressbars.databinding.ActivityProgressBarsBinding

import java.util.NoSuchElementException

// TODO: proper night mode support (https://medium.com/@chrisbanes/appcompat-v23-2-daynight-d10f90c83e94)
// TODO: plural strings
// TODO: add bkg for elevation & shadow to prg bar view
// TODO: choose notification priority

// main activity. display each timer in a list
class Progress_bars: Dynamic_theme_activity()
{
    companion object
    {
        const val EXTRA_SCROLL_TO_ROWID = "org.mattvchandler.progressbars.SCROLL_TO_ROWID"
    }

    private lateinit var binding: ActivityProgressBarsBinding
    private lateinit var adapter: Adapter

    private lateinit var date_format: String
    private var hour_24: Boolean = false

    private val on_db_change = object: BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            val change_type = intent.getStringExtra(Data.DB_CHANGED_TYPE)
            if(change_type == Data.INSERT || change_type == Data.UPDATE)
            {
                val rowid = intent.getLongExtra(Data.DB_CHANGED_ROWID, -1)
                if(rowid > 0)
                    binding.mainList.scrollToPosition(adapter.find_by_rowid(rowid))
            }

            invalidateOptionsMenu()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_progress_bars)
        setSupportActionBar(binding.toolbar)
        binding.mainList.addItemDecoration(DividerItemDecoration(binding.mainList.context, DividerItemDecoration.VERTICAL))

        // save date format to detect when it changes
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        date_format = prefs.getString("date_format", resources.getString(R.string.pref_date_format_default))!!
        hour_24 = prefs.getBoolean("hour_24", resources.getBoolean(R.bool.pref_hour_24_default))

        // set up row Adapter
        adapter = Adapter(this)

        binding.mainList.layoutManager = LinearLayoutManager(this)
        binding.mainList.adapter = adapter

        val touch_helper = ItemTouchHelper(Touch_helper_callback(adapter))
        touch_helper.attachToRecyclerView(binding.mainList)

        // update repeat times and alarms
        Data.apply_all_repeats(this)
        Notification_handler.reset_all_alarms(this)

        val scroll_to_rowid = intent.getLongExtra(EXTRA_SCROLL_TO_ROWID, -1)
        if(scroll_to_rowid >= 0)
        {
            try
            {
                binding.mainList.scrollToPosition(adapter.find_by_rowid(scroll_to_rowid))
            }
            catch(ignored: NoSuchElementException) {}
        }

        // start running each second
        update().run()

        // register to receive notifications of DB changes
        LocalBroadcastManager.getInstance(this).registerReceiver(on_db_change, IntentFilter(Data.DB_CHANGED_EVENT))
    }

    override fun onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(on_db_change)
        super.onDestroy()
    }

    public override fun onResume()
    {
        super.onResume()

        // check to see if date format has changed. rebuild activity with new format if it has
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val new_date_format = prefs.getString("date_format", resources.getString(R.string.pref_date_format_default))
        val new_hour_24 = prefs.getBoolean("hour_24", resources.getBoolean(R.bool.pref_hour_24_default))

        if(new_date_format != date_format || new_hour_24 != hour_24)
            recreate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        // set toolbar menu
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.progress_bar_action_bar, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean
    {
        // dis/enable undo-redo buttons as needed
        super.onPrepareOptionsMenu(menu)

        val undo_butt = menu.findItem(R.id.undo)
        val redo_butt = menu.findItem(R.id.redo)

        // TODO: better way to mark buttons disabled?
        if(Undo.can_undo(this))
        {
            undo_butt.isEnabled = true
            undo_butt.icon.alpha = 255
        }
        else
        {
            undo_butt.isEnabled = false
            undo_butt.icon.alpha = 255 / 3
        }
        if(Undo.can_redo(this))
        {
            redo_butt.isEnabled = true
            redo_butt.icon.alpha = 255
        }
        else
        {
            redo_butt.isEnabled = false
            redo_butt.icon.alpha = 255 / 3
        }

        return true
    }

    // handle toolbar menu presses
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.add_butt ->
            {
                // open editor with no rowid set
                startActivity(Intent(this, Settings::class.java))
                return true
            }

            R.id.undo ->
            {
                Undo.apply(this, Undo.UNDO)
                return true
            }

            R.id.redo ->
            {
                Undo.apply(this, Undo.REDO)
                return true
            }

            R.id.settings ->
            {
                // open app settings menu
                startActivity(Intent(this, Preferences::class.java))
                return true
            }

            R.id.about ->
            {
                // show about dialog
                About_dialog().show(supportFragmentManager, "about")
                return true
            }
        }
        return false
    }

    private inner class update: Runnable
    {
        private val handler = Handler()
        private val delay = 1000 // 1000ms

        override fun run()
        {
            for(i in 0 until adapter.itemCount)
            {
                val view_holder = binding.mainList.findViewHolderForAdapterPosition(i) as Adapter.Progress_bar_row_view_holder?
                view_holder?.update()
            }

            handler.postDelayed(this, delay.toLong())
        }
    }
}
