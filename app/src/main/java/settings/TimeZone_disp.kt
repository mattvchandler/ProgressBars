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

package org.mattvchandler.progressbars.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.databinding.ActivityTimezoneBinding
import org.mattvchandler.progressbars.databinding.TimezoneItemBinding
import org.mattvchandler.progressbars.util.Dynamic_theme_activity
import java.io.Serializable
import java.util.*

class TimeZone_disp(val id: String, context: Context?, date: Date?): Serializable
{
    val name = id.replace('_', ' ')
    val subtitle:String?
    val search_kwds: List<String>

    init
    {
        val tz = TimeZone.getTimeZone(id)

        val is_daylight = if(date != null) tz.inDaylightTime(date) else false

        val disp_name = tz.getDisplayName(is_daylight, TimeZone.LONG)
        val short_name = tz.getDisplayName(is_daylight, TimeZone.SHORT)

        subtitle = context?.resources?.getString(R.string.tz_list_subtitle, disp_name, short_name)

        search_kwds = listOf(name, id, disp_name, short_name).map{ it.toLowerCase(Locale.getDefault()) }.distinct()
    }

    override fun toString() = name
    override fun equals(other: Any?): Boolean
    {
        if(this === other) return true
        if(javaClass != other?.javaClass) return false

        other as TimeZone_disp

        if(id != other.id) return false

        return true
    }

    override fun hashCode(): Int
    {
        return id.hashCode()
    }
}

private class TimeZone_adapter(private val activity: TimeZone_activity, private val date: Date): RecyclerView.Adapter<TimeZone_adapter.Holder>()
{
    inner class Holder(private val binding: TimezoneItemBinding, view: View): RecyclerView.ViewHolder(view), View.OnClickListener
    {
        lateinit var tz: TimeZone_disp
        fun set()
        {
            val position = adapterPosition
            if(position == RecyclerView.NO_POSITION)
                return

            tz = timezones[position]
            binding.tz = tz
        }
        override fun onClick(v: View?)
        {
            val intent = Intent()

            intent.putExtra(TimeZone_activity.EXTRA_SELECTED_TZ, tz)
            activity.setResult(AppCompatActivity.RESULT_OK, intent)
            activity.finish()
        }
    }

    private var timezones = SortedList(TimeZone_disp::class.java, object: SortedList.Callback<TimeZone_disp>()
    {
        override fun onMoved(from_pos: Int, to_pos: Int) { notifyItemMoved(from_pos, to_pos) }
        override fun onChanged(pos: Int, count: Int) { notifyItemRangeChanged(pos, count) }
        override fun onInserted(pos: Int, count: Int) { notifyItemRangeInserted(pos, count) }
        override fun onRemoved(pos: Int, count: Int) { notifyItemRangeRemoved(pos, count) }

        override fun compare(a: TimeZone_disp, b: TimeZone_disp) = compareBy<TimeZone_disp>{ it.id }.compare(a, b)
        override fun areItemsTheSame(a: TimeZone_disp, b: TimeZone_disp) = a == b
        override fun areContentsTheSame(a: TimeZone_disp, b: TimeZone_disp) = a == b
    })

    private val inflater = LayoutInflater.from(activity)

    init
    {
        filter("")
    }

    fun filter(search: String)
    {
        val all_tzs = TimeZone.getAvailableIDs().map{ TimeZone_disp(it, activity, date) }
        if(search == "")
            replace_all(all_tzs)
        else
            replace_all(all_tzs.filter{ tz -> tz.search_kwds.any{it.contains(search.toLowerCase(Locale.getDefault()))} })
    }

    fun replace_all(tzs: List<TimeZone_disp>)
    {
        timezones.beginBatchedUpdates()

        for(i in timezones.size() - 1 downTo 0)
        {
            val tz = timezones.get(i)
            if(tz !in tzs)
                timezones.remove(tz)
        }

        timezones.addAll(tzs)

        timezones.endBatchedUpdates()
    }

    override fun getItemCount(): Int
    {
        return timezones.size()
    }

    override fun onCreateViewHolder(parent_in: ViewGroup, viewType: Int): Holder
    {
        val binding = TimezoneItemBinding.inflate(inflater, parent_in, false)
        val holder = Holder(binding, binding.root)
        binding.root.setOnClickListener(holder)
        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int)
    {
        holder.set()
    }
}

class TimeZone_activity: Dynamic_theme_activity()
{
    private lateinit var adapter: TimeZone_adapter
    private var saved_search: String? = null
    private lateinit var binding: ActivityTimezoneBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_timezone)
        setSupportActionBar(binding.toolbar as Toolbar)

        if(supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val date = intent.getSerializableExtra(EXTRA_DATE) as Date

        adapter = TimeZone_adapter(this, date)
        binding.timezoneList.adapter = adapter
        binding.timezoneList.layoutManager = LinearLayoutManager(this)

        if(savedInstanceState != null)
            saved_search = savedInstanceState.getString(SAVE_SEARCH)
    }

    override fun onSaveInstanceState(outState: Bundle)
    {
        super.onSaveInstanceState(outState)
        outState.putString(SAVE_SEARCH, saved_search)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.timezone_action_bar, menu)
        val search_item = menu?.findItem(R.id.timezone_search)!!
        val search = search_item.actionView as SearchView

        search.maxWidth = Int.MAX_VALUE

        search.setOnQueryTextListener(object: SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String?): Boolean
            {
                search.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean
            {
                saved_search = newText
                adapter.filter(newText?: "")
                binding.timezoneList.scrollToPosition(0)
                return true
            }
        })

        if(saved_search != null)
        {
            search.setQuery(saved_search, true)
            search.clearFocus()
        }
        else
            search.requestFocus()

        return super.onCreateOptionsMenu(menu)
    }

    companion object
    {
        const val EXTRA_DATE = "org.mattvchandler.progressbars.EXTRA_DATE"
        const val EXTRA_SELECTED_TZ = "org.mattvchandler.progressbars.EXTRA_SELECTED_ID"

        private const val SAVE_SEARCH = "search"
    }
}
