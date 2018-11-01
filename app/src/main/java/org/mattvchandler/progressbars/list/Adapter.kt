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

package org.mattvchandler.progressbars.list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.databinding.DataBindingUtil
import android.provider.BaseColumns
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import org.mattvchandler.progressbars.db.DB
import org.mattvchandler.progressbars.db.Data
import org.mattvchandler.progressbars.db.Progress_bars_table
import org.mattvchandler.progressbars.Progress_bars
import org.mattvchandler.progressbars.R
import org.mattvchandler.progressbars.settings.Settings
import org.mattvchandler.progressbars.databinding.ProgressBarRowBinding

import java.util.NoSuchElementException

import java.lang.Math.abs
import java.lang.Math.min

// keeps track of timer GUI rows
class Adapter(private val context: Progress_bars): RecyclerView.Adapter<Adapter.Progress_bar_row_view_holder>()
{
    private val db: SQLiteDatabase = DB(context).writableDatabase
    private var cursor: Cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null)

    // an individual row object
    inner class Progress_bar_row_view_holder(v: View): RecyclerView.ViewHolder(v), View.OnClickListener
    {
        private val row_binding: ProgressBarRowBinding = DataBindingUtil.bind(v)!!
        private var moved_from_pos = 0
        internal lateinit var data: View_data

        // get DB and display data from the cursor for this row
        fun bind_cursor(cursor: Cursor)
        {
            data = View_data(context, cursor)
            row_binding.data = data // let the GUI elements see the data
        }

        fun update()
        {
            data.update_display(context.resources)
        }

        // click the row to edit its data
        override fun onClick(v: View)
        {
            // create and launch an intent to launch the editor, and pass the rowid
            val intent = Intent(v.context, Settings::class.java)
            intent.putExtra(Settings.EXTRA_EDIT_ROW_ID, data.rowid)
            context.startActivity(intent)
        }

        // called when a row is dragged for deletion or reordering
        fun on_selected()
        {
            // get the background color
            val tv = TypedValue()
            context.theme.resolveAttribute(android.R.attr.colorBackground, tv, true)
            // make it darker and-semi transparent
            row_binding.progressRow.setBackgroundColor(min(tv.data - 0x40202020, 0))

            moved_from_pos = adapterPosition
        }

        // called when a row is released from reordering
        fun on_cleared()
        {
            // reset the original background color
            val tv = TypedValue()
            context.theme.resolveAttribute(android.R.attr.colorBackground, tv, true)
            row_binding.progressRow.setBackgroundColor(tv.data)

            val moved_to_pos = adapterPosition
            if(moved_to_pos != RecyclerView.NO_POSITION && moved_to_pos != moved_from_pos)
                data.reorder(context, moved_from_pos, moved_to_pos)
        }
    }

    init
    {
        val db_change_receiver = object: BroadcastReceiver()
        {
            // store the data we'll need for the lifetime of this object
            override fun onReceive(context: Context, intent: Intent)
            {
                val rowid = intent.getLongExtra(Data.DB_CHANGED_ROWID, -1L)
                val change_type = intent.getStringExtra(Data.DB_CHANGED_TYPE)
                if(rowid == -1L && change_type != "move")
                    return

                when(change_type)
                {
                    Data.INSERT ->
                    {
                        reset_cursor()
                        this@Adapter.notifyItemInserted(find_by_rowid(rowid))
                    }
                    Data.UPDATE ->
                    {
                        this@Adapter.notifyItemChanged(find_by_rowid(rowid))
                        reset_cursor()
                    }
                    Data.DELETE ->
                    {
                        this@Adapter.notifyItemRemoved(find_by_rowid(rowid))
                        reset_cursor()
                    }
                    Data.MOVE ->
                    {
                        reset_cursor()
                        val from_pos = intent.getIntExtra(Data.DB_CHANGED_FROM_POS, -1)
                        val to_pos = intent.getIntExtra(Data.DB_CHANGED_TO_POS, -1)
                        if(from_pos == -1 || to_pos == -1)
                            return

                        this@Adapter.notifyItemRangeChanged(min(from_pos, to_pos), abs(from_pos - to_pos) + 1)
                    }
                }
            }
        }

        LocalBroadcastManager.getInstance(context).registerReceiver(db_change_receiver, IntentFilter(Data.DB_CHANGED_EVENT))
    }

    // called when DB info has changed, to let us update the cursor
    private fun reset_cursor()
    {
        cursor.close()

        // get new DB data
        cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null)
    }

    override fun onCreateViewHolder(parent_in: ViewGroup, viewType: Int): Progress_bar_row_view_holder
    {
        // create a new row
        val v = LayoutInflater.from(parent_in.context).inflate(R.layout.progress_bar_row, parent_in, false)
        val holder = Progress_bar_row_view_holder(v)
        v.setOnClickListener(holder)
        return holder
    }

    override fun onBindViewHolder(holder: Progress_bar_row_view_holder, position: Int)
    {
        // move to the requested position and bind the data
        cursor.moveToPosition(position)
        holder.bind_cursor(cursor)
    }

    override fun getItemCount(): Int
    {
        return cursor.count
    }

    // look up position by rowid.
    fun find_by_rowid(rowid: Long): Int
    {
        // linear search of cursor to find the row with matching rowid
        for(i in 0 until itemCount)
        {
            cursor.moveToPosition(i)
            if(cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID)) == rowid)
            {
                return i
            }
        }

        throw NoSuchElementException("rowid: " + rowid.toString() + " not found in cursor")
    }

    // called when a row is deleted
    fun on_item_dismiss(pos: Int)
    {
        cursor.moveToPosition(pos)

        // delete from DB
        Data(cursor).delete(context)
    }
}
