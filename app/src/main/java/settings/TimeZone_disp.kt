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
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.AppCompatEditText
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import org.mattvchandler.progressbars.R
import java.util.*

class TimeZone_disp(val id: String, date: Date?)
{
    val name = id.replace('_', ' ')
    val disp_name: String
    val short_name: String
    val search_kwds: List<String>

    init
    {
        val tz = TimeZone.getTimeZone(id)

        val is_daylight = if(date != null) tz.inDaylightTime(date) else false

        disp_name = tz.getDisplayName(is_daylight, TimeZone.LONG)
        short_name = tz.getDisplayName(is_daylight, TimeZone.SHORT)

        search_kwds = listOf(name, id, disp_name, short_name).map{ it.toLowerCase() }.distinct()
    }

    override fun toString() = name
}

private fun get_timezone_list(date: Date): List<TimeZone_disp>
{
    val ids = TimeZone.getAvailableIDs()
    return List(ids.size)
    {
        TimeZone_disp(ids[it], date)
    }
}

private class TimeZone_adapter(private val context: Context, private val date: Date): BaseAdapter(), Filterable
{
    private var timezones = get_timezone_list(date)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View
    {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.timezone_item, parent, false)

        view.findViewById<TextView>(R.id.time_zone_title).text = timezones[position].name
        view.findViewById<TextView>(R.id.time_zone_subtitle).text = context.resources.getString(R.string.tz_list_subtitle, timezones[position].disp_name, timezones[position].short_name)

        return view
    }

    override fun getItem(position: Int): TimeZone_disp
    {
        return timezones[position]
    }

    override fun getItemId(position: Int): Long
    {
        return position.toLong()
    }

    override fun getCount(): Int
    {
        return timezones.size
    }

    inner class TimeZone_filter: Filter()
    {
        override fun performFiltering(constraint: CharSequence?): FilterResults
        {
            val results = FilterResults()

            val timezones = if(constraint != null)
                get_timezone_list(date).filter{ tz -> tz.search_kwds.any{it.contains(constraint.toString().toLowerCase())} }
            else
                get_timezone_list(date)

            results.values = timezones
            results.count = timezones.size
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?)
        {
            if(results == null)
                notifyDataSetInvalidated()
            else
            {
                @Suppress("UNCHECKED_CAST")
                timezones = results.values as List<TimeZone_disp>
                notifyDataSetChanged()
            }
        }
    }

    private val filter = TimeZone_filter()
    override fun getFilter(): Filter
    {
        return filter
    }
}

class TimeZone_frag: DialogFragment()
{
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        super.onCreateView(inflater, container, savedInstanceState)

        val view = inflater.inflate(R.layout.frag_timezone, container)

        val search = view.findViewById(R.id.time_zone_search) as AppCompatEditText
        val list = view.findViewById(R.id.time_zone_list) as ListView

        val adapter = TimeZone_adapter(context!!, arguments!!.getSerializable("date")!! as Date)
        list.adapter = adapter

        search.addTextChangedListener(object: TextWatcher
        {
            override fun afterTextChanged(s: Editable?)
            {
                adapter.filter.filter(s)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        list.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            (activity as Settings).on_tz_set(adapter.getItem(position), tag!!)
            dismiss()
        }

        return view
    }

    override fun onCancel(dialog: DialogInterface?)
    {
        super.onCancel(dialog)
        dismiss()
    }
}
