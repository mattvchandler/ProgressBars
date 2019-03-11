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

package org.mattvchandler.progressbars.list

import android.content.Intent
import android.databinding.ViewDataBinding
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.mattvchandler.progressbars.Progress_bars
import org.mattvchandler.progressbars.databinding.ProgressBarRowBinding
import org.mattvchandler.progressbars.databinding.SingleProgressBarRowBinding
import org.mattvchandler.progressbars.db.DB
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.db.Progress_bars_table
import org.mattvchandler.progressbars.settings.Settings
import java.security.InvalidParameterException
import java.util.Collections.swap

// keeps track of timer GUI rows
class Adapter(private val activity: Progress_bars): RecyclerView.Adapter<Adapter.Holder>()
{
    private val data_list: MutableList<View_data>

    // an individual row object
    inner class Holder(private val row_binding: ViewDataBinding): RecyclerView.ViewHolder(row_binding.root), View.OnClickListener
    {
        private var moved_from_pos = 0
        internal lateinit var data: View_data

        fun bind(data: View_data)
        {
            this.data = data

            when(row_binding)
            {
                is ProgressBarRowBinding -> row_binding.data = data
                is SingleProgressBarRowBinding -> row_binding.data = data
            }
        }

        fun update()
        {
            data.update_display(activity.resources)
        }

        // click the row to edit its data
        override fun onClick(v: View)
        {
            // create and launch an intent to launch the editor, and pass the rowid
            val intent = Intent(activity, Settings::class.java)
            intent.putExtra(Settings.EXTRA_EDIT_DATA, data) // TODO: Data, not rowid
            activity.startActivityForResult(intent, Progress_bars.RESULT_EDIT_DATA)
        }

        // called when dragging during each reordering
        fun on_move(to: Holder)
        {
            swap(data_list, adapterPosition, to.adapterPosition)
            notifyItemMoved(adapterPosition, to.adapterPosition)
        }
        // called when a row is selected for deletion or reordering
        fun on_selected()
        {
        }

        // called when a row is released from reordering
        fun on_cleared()
        {
            // TODO: set undo here?
        }
    }

    init
    {
        setHasStableIds(true)

        val db = DB(activity).readableDatabase
        val cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null)

        cursor.moveToFirst()
        data_list = (0 until cursor.count).map{ val data = View_data(activity, Data(cursor)); cursor.moveToNext(); data }.toMutableList()

        cursor.close()
        db.close()
    }

    override fun onCreateViewHolder(parent_in: ViewGroup, viewType: Int): Holder
    {
        // create a new row
        val inflater = LayoutInflater.from(activity)
        val row_binding = when(viewType)
        {
            SEPARATE_TIME_VIEW -> ProgressBarRowBinding.inflate(inflater, parent_in, false)
            SINGLE_TIME_VIEW -> SingleProgressBarRowBinding.inflate(inflater, parent_in, false)
            else -> throw(InvalidParameterException("Unknown viewType: $viewType"))
        }
        val holder = Holder(row_binding)
        row_binding.root.setOnClickListener(holder)
        return holder
    }

    override fun onBindViewHolder(holder: Holder, position: Int)
    {
        // move to the requested position and bind the data
        holder.bind(data_list[position])
    }

    override fun getItemViewType(position: Int): Int
    {
        return if(data_list[position].separate_time)
            SEPARATE_TIME_VIEW
        else
            SINGLE_TIME_VIEW
    }

    override fun getItemId(position: Int) = data_list[position].id
    override fun getItemCount() = data_list.size

    fun find_by_rowid(rowid: Long) = data_list.indexOfFirst{ it.rowid == rowid}
    fun find_by_id(id: Long) = data_list.indexOfFirst{ it.id == id}

    // called when a row is deleted
    fun on_item_dismiss(pos: Int)
    {
        data_list.removeAt(pos)
        notifyItemRemoved(pos)
    }

    fun set_edited(data: Data)
    {
        val pos = find_by_id(data.id)
        if(pos >= 0)
        {
            data_list[pos] = View_data(activity, data)
            notifyItemChanged(pos)
        }
        else
        {
            data_list.add(View_data(activity, data))
            notifyItemInserted(data_list.size - 1)
        }
    }

    fun save_to_db()
    {
        val db = DB(activity).writableDatabase
        db.beginTransaction()
        db.delete(Progress_bars_table.TABLE_NAME, null, null)

        for(i in 0 until data_list.size)
            data_list[i].insert(db, i.toLong())

        db.setTransactionSuccessful()
        db.endTransaction()
        db.close()
    }

    companion object
    {
        private const val SEPARATE_TIME_VIEW = 0
        private const val SINGLE_TIME_VIEW = 1
    }
}
