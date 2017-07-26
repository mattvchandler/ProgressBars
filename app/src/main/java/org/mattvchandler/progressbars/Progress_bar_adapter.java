package org.mattvchandler.progressbars;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mattvchandler.progressbars.databinding.ProgressBarRowBinding;

import java.util.NoSuchElementException;

import static java.lang.Math.min;

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

// keeps track of timer GUI rows
public class Progress_bar_adapter extends RecyclerView.Adapter<Progress_bar_adapter.Progress_bar_row_view_holder>
{
    // an individual row object
    public class Progress_bar_row_view_holder extends RecyclerView.ViewHolder
                                                     implements View.OnClickListener
    {
        final ProgressBarRowBinding row_binding;
        Progress_bar_view_data data;

        public Progress_bar_row_view_holder(View v)
        {
            super(v);
            row_binding = DataBindingUtil.bind(v);
        }

        // get DB and display data from the cursor for this row
        public void bind_cursor(Cursor cursor)
        {
            data = new Progress_bar_view_data(context, cursor);
            row_binding.setData(data); // let the GUI elements see the data
        }

        public void update()
        {
            data.update();
        }

        // click the row to edit its data
        @Override
        public void onClick(View v)
        {
            // create and launch an intent to launch the editor, and pass the rowid
            Intent intent = new Intent(v.getContext(), Settings.class);
            intent.putExtra(Settings.EXTRA_EDIT_ROW_ID, data.rowid);
            context.startActivityForResult(intent, Progress_bars.UPDATE_REQUEST);
        }

        // called when a row is dragged for deletion or reordering
        public void on_selected()
        {
            // get the background color
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true);
            // make it darker and-semi transparent
            row_binding.progressRow.setBackgroundColor(min(tv.data - 0x40202020, 0));
        }

        // called when a row is released from reordering
        public void on_cleared()
        {
            // reset the original background color
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true);
            row_binding.progressRow.setBackgroundColor(tv.data);
        }
    }

    private final SQLiteDatabase db;
    private Cursor cursor;
    private final Progress_bars context;

    public Progress_bar_adapter(Progress_bars context)
    {
        // store the data we'll need for the lifetime of this object
        this.context = context;
        db = new Progress_bar_DB(context).getWritableDatabase();
        cursor = db.rawQuery(Progress_bar_table.SELECT_ALL_ROWS, null);
    }

    // call when DB info has changed, to let the adapter update its cursor
    public void reset_cursor()
    {
        if(cursor != null)
            cursor.close();

        // get new DB data
        cursor = db.rawQuery(Progress_bar_table.SELECT_ALL_ROWS, null);
    }

    @Override
    public Progress_bar_row_view_holder onCreateViewHolder(ViewGroup parent_in, int viewType)
    {
        // create a new row
        View v = LayoutInflater.from(parent_in.getContext()).inflate(R.layout.progress_bar_row, parent_in, false);
        Progress_bar_row_view_holder holder = new Progress_bar_row_view_holder(v);
        v.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(Progress_bar_row_view_holder holder, int position)
    {
        // move to the requested position and bind the data
        cursor.moveToPosition(position);
        holder.bind_cursor(cursor);
    }

    @Override
    public int getItemCount()
    {
        return cursor.getCount();
    }

    // look up position by rowid.
    public int find_by_rowid(long rowid)
    {
        // linear search of cursor to find the row with matching rowid
        for(int i = 0; i < getItemCount(); ++i)
        {
            cursor.moveToPosition(i);
            if(cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_table._ID)) == rowid)
            {
                return i;
            }
        }

        throw new NoSuchElementException("rowid: " + String.valueOf(rowid) + " not found in cursor");
    }

    // called when two rows need to switch places
    public void on_item_move(int from_pos, int to_pos)
    {
        // get current order # and rowids
        cursor.moveToPosition(from_pos);
        String from_rowid = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table._ID));
        String from_order = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.ORDER_COL));

        cursor.moveToPosition(to_pos);
        String to_rowid = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table._ID));
        String to_order = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_table.ORDER_COL));

        ContentValues values = new ContentValues();

        // swap orders

        // put 'from' at order #-1
        values.put(Progress_bar_table.ORDER_COL, -1);
        db.update(Progress_bar_table.TABLE_NAME, values,
                Progress_bar_table._ID + " = ?", new String[] {from_rowid});

        // put 'to' at 'from's old old position
        values.clear();
        values.put(Progress_bar_table.ORDER_COL, from_order);
        db.update(Progress_bar_table.TABLE_NAME, values,
                Progress_bar_table._ID + " = ?", new String[] {to_rowid});

        // put 'from' at 'to's old old position
        values.clear();
        values.put(Progress_bar_table.ORDER_COL, to_order);
        db.update(Progress_bar_table.TABLE_NAME, values,
                Progress_bar_table._ID + " = ?", new String[] {from_rowid});

        // get new data
        reset_cursor();

        // swap items in GUI
        notifyItemMoved(to_pos, from_pos);
    }

    // called when a row is deleted
    public void on_item_dismiss(final int pos)
    {
        cursor.moveToPosition(pos);

        // get a copy of the data before we delete it
        final Progress_bar_data save_data = new Progress_bar_data(cursor);

        // delete from DB
        save_data.delete(context);

        // get new data
        reset_cursor();

        notifyItemRemoved(pos);

        // show deletion message with undo option
        Snackbar.make(context.findViewById(R.id.mainList), context.getResources().getString(R.string.deleted, save_data.title), Snackbar.LENGTH_LONG)
                .setAction(context.getResources().getString(R.string.undo), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        // re-insert the deleted row
                        save_data.insert(context);
                        reset_cursor();
                        Progress_bar_adapter.this.notifyItemInserted(pos);
                    }
                }).show();
    }

}
