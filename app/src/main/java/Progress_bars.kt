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

package org.mattvchandler.progressbars

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
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
import org.mattvchandler.progressbars.databinding.ActivityProgressBarsBinding
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.list.Adapter
import org.mattvchandler.progressbars.list.Touch_helper_callback
import org.mattvchandler.progressbars.settings.Settings
import org.mattvchandler.progressbars.util.About_dialog
import org.mattvchandler.progressbars.util.Dynamic_theme_activity
import org.mattvchandler.progressbars.util.Notification_handler
import org.mattvchandler.progressbars.util.Preferences
import java.util.*

// main activity. display each timer in a list
class Progress_bars: Dynamic_theme_activity()
{
    companion object
    {
        const val EXTRA_ROWID = "org.mattvchandler.progressbars.EXTRA_ROWID"
        const val CHANGE_LIST_EVENT = "ProgressBars.CHANGE_LIST_EVENT"
        const val RESULT_EDIT_DATA = 0
    }

    private lateinit var binding: ActivityProgressBarsBinding
    private lateinit var adapter: Adapter

    private lateinit var date_format: String


    private val on_list_change = object: BroadcastReceiver()
    {
        override fun onReceive(context: Context, intent: Intent)
        {
            when(intent.action)
            {
                CHANGE_LIST_EVENT ->
                {
                    val rowid = intent.getLongExtra(EXTRA_ROWID, -1)
                    if(rowid >= 0)
                        adapter.apply_repeat(adapter.find_by_rowid(rowid))
                }
            }
        }
    }

    private val on_24_hour_change = object: ContentObserver(Handler())
    {
        override fun onChange(selfChange: Boolean)
        {
            super.onChange(selfChange)
            recreate()
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

        // set up row Adapter
        adapter = Adapter(this)

        binding.mainList.layoutManager = LinearLayoutManager(this)
        binding.mainList.adapter = adapter

        val touch_helper = ItemTouchHelper(Touch_helper_callback(adapter))
        touch_helper.attachToRecyclerView(binding.mainList)

        val scroll_to_rowid = intent.getLongExtra(EXTRA_ROWID, -1)
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

        val broadcast_filter = IntentFilter()
        broadcast_filter.addAction(CHANGE_LIST_EVENT)
        LocalBroadcastManager.getInstance(this).registerReceiver(on_list_change, IntentFilter(CHANGE_LIST_EVENT))
        contentResolver.registerContentObserver(android.provider.Settings.System.getUriFor(android.provider.Settings.System.TIME_12_24), false, on_24_hour_change)
    }

    override fun onStop()
    {
        adapter.save_to_db()
        super.onStop()
    }

    override fun onDestroy()
    {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(on_list_change)
        contentResolver.unregisterContentObserver(on_24_hour_change)

        binding.mainList.adapter = null
        super.onDestroy()
    }

    public override fun onResume()
    {
        super.onResume()

        // check to see if date format has changed. rebuild activity with new format if it has
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val new_date_format = prefs.getString("date_format", resources.getString(R.string.pref_date_format_default))

        if(new_date_format != date_format)
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

        // TODO: en/disable undo/redo buttons (and implement the whole undo/redo system)
//        menu.findItem(R.id.undo).isEnabled = Undo.can_undo(this)
//        menu.findItem(R.id.redo).isEnabled = Undo.can_redo(this)

        return true
    }

    // handle toolbar menu presses
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.add_butt ->
            {
                startActivityForResult(Intent(this, Settings::class.java), RESULT_EDIT_DATA)
                return true
            }

            R.id.undo ->
            {
//                Undo.apply(this, Undo.UNDO)
                return true
            }

            R.id.redo ->
            {
//                Undo.apply(this, Undo.REDO)
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

    override fun onActivityResult(request_code: Int, result_code: Int, intent: Intent?)
    {
        // get data back from Countdown_text
        if(result_code == Activity.RESULT_OK && request_code == RESULT_EDIT_DATA)
        {
            val data = intent!!.getSerializableExtra(Settings.EXTRA_EDIT_DATA)!! as Data
            val pos = adapter.set_edited(data)
            binding.mainList.scrollToPosition(pos)
        }
    }

    private inner class update: Runnable
    {
        private val handler = Handler()
        private val delay = 1000 // 1000ms

        override fun run()
        {
            for(i in 0 until adapter.itemCount)
            {
                val view_holder = binding.mainList.findViewHolderForAdapterPosition(i) as Adapter.Holder?
                view_holder?.update()
            }

            handler.postDelayed(this, delay.toLong())
        }
    }
}
