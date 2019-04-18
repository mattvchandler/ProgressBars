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

import android.content.Intent
import android.database.ContentObserver
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import org.mattvchandler.progressbars.databinding.ActivityProgressBarsBinding
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.list.Adapter
import org.mattvchandler.progressbars.list.Touch_helper_callback
import org.mattvchandler.progressbars.settings.Settings
import org.mattvchandler.progressbars.util.About_dialog
import org.mattvchandler.progressbars.util.Dynamic_theme_activity
import org.mattvchandler.progressbars.util.Preferences

// TODO: progress_bar_widget? persistent notification?
// TODO: consider moving to LiveData / ViewModel. May reduce complexity

// main activity. display each timer in a list
class Progress_bars: Dynamic_theme_activity()
{
    companion object
    {
        const val EXTRA_ID = "org.mattvchandler.progressbars.EXTRA_ID"
        const val CHANGE_LIST_EVENT = "ProgressBars.CHANGE_LIST_EVENT"
        const val RESULT_EDIT_DATA = 0

        private const val SAVE_UNDO_REDO = "SAVE_UNDO_REDO"
        private const val SAVE_ADD_FROM_SHORTCUT = "SAVE_ADD_FROM_SHORTCUT"

        private var running_instance: Progress_bars? = null
        val is_running: Boolean
            get() = running_instance != null

        fun apply_repeat(id: Int)
        {
            running_instance?.adapter!!.apply_repeat(id)
        }
    }

    private lateinit var binding: ActivityProgressBarsBinding
    private lateinit var adapter: Adapter

    private lateinit var date_format: String

    private var add_from_shortcut = true

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
        setSupportActionBar(binding.toolbar as Toolbar)
        binding.mainList.addItemDecoration(DividerItemDecoration(binding.mainList.context, DividerItemDecoration.VERTICAL))

        consume_insets(this, binding.mainList, binding.appbarLayout)

        // save date format to detect when it changes
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        date_format = prefs.getString("date_format", resources.getString(R.string.pref_date_format_default))!!

        // set up row Adapter
        adapter = Adapter(this)

        if(savedInstanceState != null)
        {
            adapter.undo_redo_stacks = savedInstanceState.getSerializable(SAVE_UNDO_REDO)!!
            add_from_shortcut = savedInstanceState.getBoolean(SAVE_ADD_FROM_SHORTCUT, false)
        }

        binding.mainList.layoutManager = LinearLayoutManager(this)
        binding.mainList.adapter = adapter

        val touch_helper = ItemTouchHelper(Touch_helper_callback(adapter))
        touch_helper.attachToRecyclerView(binding.mainList)

        val scroll_to_id = intent.getIntExtra(EXTRA_ID, -1)
        if(scroll_to_id >= 0)
        {
            try
            {
                binding.mainList.scrollToPosition(adapter.find_by_id(scroll_to_id))
            }
            catch(ignored: NoSuchElementException) {}
        }

        // start running each second
        update().run()

        contentResolver.registerContentObserver(android.provider.Settings.System.getUriFor(android.provider.Settings.System.TIME_12_24), false, on_24_hour_change)

        if(add_from_shortcut && intent.action == Intent.ACTION_MAIN && !intent.hasCategory(Intent.CATEGORY_LAUNCHER)) // started from shortcut
            startActivityForResult(Intent(this, Settings::class.java), RESULT_EDIT_DATA)

        add_from_shortcut = false
    }

    override fun onSaveInstanceState(out: Bundle)
    {
        super.onSaveInstanceState(out)

        out.putSerializable(SAVE_UNDO_REDO, adapter.undo_redo_stacks)
    }

    override fun onDestroy()
    {
        contentResolver.unregisterContentObserver(on_24_hour_change)

        binding.mainList.adapter = null
        super.onDestroy()
    }

    override fun onResume()
    {
        super.onResume()

        // check to see if date format has changed. rebuild activity with new format if it has
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val new_date_format = prefs.getString("date_format", resources.getString(R.string.pref_date_format_default))

        if(new_date_format != date_format)
            recreate()
    }

    override fun onPause()
    {
        adapter.save_to_db()
        super.onPause()
    }

    override fun onStart()
    {
        super.onStart()
        running_instance = this
    }

    override fun onStop()
    {
        running_instance = null
        super.onStop()
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

        menu.findItem(R.id.undo).isEnabled = adapter.can_undo()
        menu.findItem(R.id.redo).isEnabled = adapter.can_redo()

        return true
    }

    // handle toolbar menu presses
    override fun onOptionsItemSelected(item: MenuItem) = when(item.itemId)
    {
        R.id.add_butt ->
        {
            startActivityForResult(Intent(this, Settings::class.java), RESULT_EDIT_DATA)
            true
        }

        R.id.undo ->
        {
            adapter.undo()
            true
        }

        R.id.redo ->
        {
            adapter.redo()
            true
        }

        R.id.settings ->
        {
            // open app settings menu
            startActivity(Intent(this, Preferences::class.java))
            true
        }

        R.id.about ->
        {
            // show about dialog
            About_dialog().show(supportFragmentManager, "about")
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(request_code: Int, result_code: Int, intent: Intent?)
    {
        // get data back from Countdown_text
        if(result_code == RESULT_OK && request_code == RESULT_EDIT_DATA)
        {
            val data = intent!!.getSerializableExtra(Settings.EXTRA_EDIT_DATA)!! as Data
            adapter.set_edited(data)
        }
        else
            super.onActivityResult(request_code, result_code, intent)
    }

    fun scroll_to(pos: Int) = binding.mainList.scrollToPosition(pos)

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
