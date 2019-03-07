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

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
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

        search_kwds = listOf(name, id, disp_name, short_name).map{ it.toLowerCase() }.distinct()
    }

    override fun toString() = name
}

private fun get_timezone_list(date: Date, context: Context): List<TimeZone_disp>
{
    val ids = TimeZone.getAvailableIDs()
    return List(ids.size)
    {
        TimeZone_disp(ids[it], context, date)
    }
}

private class TimeZone_adapter(private val activity: TimeZone_activity, private val date: Date): RecyclerView.Adapter<TimeZone_adapter.Holder>(), Filterable
{
    private var timezones = get_timezone_list(date, activity)
    private val inflater = LayoutInflater.from(activity)

    override fun getItemCount(): Int
    {
        return timezones.size
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
            activity.setResult(Activity.RESULT_OK, intent)
            activity.finish()
        }
    }

    inner class TimeZone_filter: Filter()
    {
        override fun performFiltering(constraint: CharSequence?): FilterResults
        {
            val results = FilterResults()

            val timezones = if(constraint != null)
                get_timezone_list(date, activity).filter{ tz -> tz.search_kwds.any{it.contains(constraint.toString().toLowerCase())} }
            else
                get_timezone_list(date, activity)

            Log.d("MyFilter", "$constraint: ${timezones.size}")

            results.values = timezones
            results.count = timezones.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?)
        {
            if(results != null)
            {
                @Suppress("UNCHECKED_CAST")
                timezones = results.values as List<TimeZone_disp>
            }
            notifyDataSetChanged()
        }
    }

    private val filter = TimeZone_filter()
    override fun getFilter(): Filter
    {
        return filter
    }
}

class TimeZone_activity: Dynamic_theme_activity()
{
    private lateinit var adapter: TimeZone_adapter
    private var saved_search: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityTimezoneBinding>(this, R.layout.activity_timezone)
        setSupportActionBar(binding.toolbar)

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
                adapter.filter.filter(newText)
                return true
            }
        })

        if(saved_search != null)
        {
            search.setQuery(saved_search, true)
            search.clearFocus()
        }

        return super.onCreateOptionsMenu(menu)
    }

    companion object
    {
        const val EXTRA_DATE = "org.mattvchandler.progressbars.EXTRA_DATE"
        const val EXTRA_SELECTED_TZ = "org.mattvchandler.progressbars.EXTRA_SELECTED_ID"

        private const val SAVE_SEARCH = "search"
    }
}
