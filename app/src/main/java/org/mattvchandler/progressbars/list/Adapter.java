package org.mattvchandler.progressbars.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mattvchandler.progressbars.db.DB;
import org.mattvchandler.progressbars.db.Data;
import org.mattvchandler.progressbars.db.Progress_bars_table;
import org.mattvchandler.progressbars.Progress_bars;
import org.mattvchandler.progressbars.R;
import org.mattvchandler.progressbars.settings.Settings;
import org.mattvchandler.progressbars.databinding.ProgressBarRowBinding;

import java.util.NoSuchElementException;

import static java.lang.Math.abs;
import static java.lang.Math.min;

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

// keeps track of timer GUI rows
public class Adapter extends RecyclerView.Adapter<Adapter.Progress_bar_row_view_holder>
{
    // an individual row object
    public class Progress_bar_row_view_holder extends RecyclerView.ViewHolder
                                                     implements View.OnClickListener
    {
        final ProgressBarRowBinding row_binding;
        View_data data;
        int moved_from_pos = 0;

        public Progress_bar_row_view_holder(View v)
        {
            super(v);
            row_binding = DataBindingUtil.bind(v);
        }

        // get DB and display data from the cursor for this row
        public void bind_cursor(Cursor cursor)
        {
            data = new View_data(context, cursor);
            row_binding.setData(data); // let the GUI elements see the data
        }

        public void update()
        {
            data.update(context.getResources());
        }

        // click the row to edit its data
        @Override
        public void onClick(View v)
        {
            // create and launch an intent to launch the editor, and pass the rowid
            Intent intent = new Intent(v.getContext(), Settings.class);
            intent.putExtra(Settings.EXTRA_EDIT_ROW_ID, data.rowid);
            context.startActivity(intent);
        }

        // called when a row is dragged for deletion or reordering
        public void on_selected()
        {
            // get the background color
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true);
            // make it darker and-semi transparent
            row_binding.progressRow.setBackgroundColor(min(tv.data - 0x40202020, 0));

            moved_from_pos = getAdapterPosition();
        }

        // called when a row is released from reordering
        public void on_cleared()
        {
            // reset the original background color
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true);
            row_binding.progressRow.setBackgroundColor(tv.data);

            int moved_to_pos = getAdapterPosition();
            if(moved_to_pos != RecyclerView.NO_POSITION && moved_to_pos != moved_from_pos)
                data.reorder(context, moved_from_pos, moved_to_pos);
        }
    }

    private final SQLiteDatabase db;
    private Cursor cursor;
    private final Progress_bars context;

    public Adapter(Progress_bars context)
    {
        // store the data we'll need for the lifetime of this object
        this.context = context;
        db = new DB(context).getWritableDatabase();
        cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null);

        BroadcastReceiver db_change_receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                long rowid = intent.getLongExtra(Data.DB_CHANGED_ROWID, -1);
                String change_type = intent.getStringExtra(Data.DB_CHANGED_TYPE);
                if(rowid == -1 && !change_type.equals("move"))
                    return;

                switch(change_type)
                {
                case Data.INSERT:
                    reset_cursor();
                    Adapter.this.notifyItemInserted(find_by_rowid(rowid));
                    break;
                case Data.UPDATE:
                    Adapter.this.notifyItemChanged(find_by_rowid(rowid));
                    reset_cursor();
                    break;
                case Data.DELETE:
                    Adapter.this.notifyItemRemoved(find_by_rowid(rowid));
                    reset_cursor();
                    break;
                case Data.MOVE:
                    reset_cursor();
                    int from_pos = intent.getIntExtra(Data.DB_CHANGED_FROM_POS, -1);
                    int to_pos = intent.getIntExtra(Data.DB_CHANGED_TO_POS, -1);
                    if(from_pos == -1 || to_pos == -1)
                        return;

                    Adapter.this.notifyItemRangeChanged(min(from_pos, to_pos), abs(from_pos - to_pos) + 1);
                    break;
                default:
                }
            }
        };

        LocalBroadcastManager.getInstance(context).registerReceiver(db_change_receiver, new IntentFilter(Data.DB_CHANGED_EVENT));
    }

    // called when DB info has changed, to let us update the cursor
    private void reset_cursor()
    {
        if(cursor != null)
            cursor.close();

        // get new DB data
        cursor = db.rawQuery(Progress_bars_table.SELECT_ALL_ROWS, null);
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
            if(cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bars_table._ID)) == rowid)
            {
                return i;
            }
        }

        throw new NoSuchElementException("rowid: " + String.valueOf(rowid) + " not found in cursor");
    }

    // called when a row is deleted
    public void on_item_dismiss(final int pos)
    {
        cursor.moveToPosition(pos);

        // delete from DB
        new Data(cursor).delete(context);
    }
}
