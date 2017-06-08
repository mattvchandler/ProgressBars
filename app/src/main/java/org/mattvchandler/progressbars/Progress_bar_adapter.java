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

public class Progress_bar_adapter extends RecyclerView.Adapter<Progress_bar_adapter.Progress_bar_row_view_holder>
{
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

        public void bind_cursor(Cursor cursor)
        {
            data = new Progress_bar_view_data(cursor);
            row_binding.setData(data);
        }
        @Override
        public void onClick(View v)
        {
            Intent intent = new Intent(v.getContext(), Settings.class);
            intent.putExtra(Settings.EXTRA_EDIT_ROW_ID, data.rowid);
            context.startActivityForResult(intent, Progress_bars.UPDATE_REQUEST);
        }

        public void on_selected()
        {
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorLongPressedHighlight, tv, true);
            row_binding.progressRow.setBackgroundColor(tv.data);
        }

        public void on_cleared()
        {
            TypedValue tv = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorBackground, tv, true);
            row_binding.progressRow.setBackgroundColor(tv.data);
        }
    }

    private Cursor cursor;
    private final Progress_bars context;

    public Progress_bar_adapter(Cursor cur, Progress_bars con)
    {
        cursor = cur;
        context = con;
    }

    public void resetCursor(Cursor cur)
    {
        if(cursor != null)
            cursor.close();

        if(cur != null)
        {
            cursor = cur;
        }
        else
        {
            SQLiteDatabase db = new Progress_bar_DB(context).getWritableDatabase();
            cursor = db.rawQuery(Progress_bar_contract.Progress_bar_table.SELECT_ALL_ROWS, null);
        }
    }

    @Override
    public Progress_bar_row_view_holder onCreateViewHolder(ViewGroup parent_in, int viewType)
    {
        View v = LayoutInflater.from(parent_in.getContext()).inflate(R.layout.progress_bar_row, parent_in, false);
        Progress_bar_row_view_holder holder = new Progress_bar_row_view_holder(v);
        v.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(Progress_bar_row_view_holder holder, int position)
    {
        cursor.moveToPosition(position);
        holder.bind_cursor(cursor);
    }

    @Override
    public int getItemCount()
    {
        return cursor.getCount();
    }

    public int find_by_rowid(long rowid)
    {
        for(int i = 0; i < getItemCount(); ++i)
        {
            cursor.moveToPosition(i);
            if(cursor.getLong(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table._ID)) == rowid)
            {
                return i;
            }
        }

        throw new NoSuchElementException("rowid: " + String.valueOf(rowid) + " not found in cursor");
    }

    public void on_item_move(int from_pos, int to_pos)
    {
        cursor.moveToPosition(from_pos);
        String from_rowid = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table._ID));
        String from_order = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.ORDER_COL));

        cursor.moveToPosition(to_pos);
        String to_rowid = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table._ID));
        String to_order = cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.ORDER_COL));

        SQLiteDatabase db = new Progress_bar_DB(context).getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, to_order);
        db.update(Progress_bar_contract.Progress_bar_table.TABLE_NAME, values,
                Progress_bar_contract.Progress_bar_table._ID + " = ?", new String[] {from_rowid});

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, from_order);
        db.update(Progress_bar_contract.Progress_bar_table.TABLE_NAME, values,
                Progress_bar_contract.Progress_bar_table._ID + " = ?", new String[] {to_rowid});

        db.close();

        resetCursor(null);

        notifyItemMoved(to_pos, from_pos);
    }

    public void on_item_dismiss(final int pos)
    {
        cursor.moveToPosition(pos);
        final Progress_bar_data save_data = new Progress_bar_data(cursor);

        save_data.delete(context);

        resetCursor(null);

        notifyItemRemoved(pos);

        Snackbar.make(context.findViewById(R.id.mainList), save_data.title + context.getResources().getString(R.string.deleted), Snackbar.LENGTH_LONG)
                .setAction(context.getResources().getString(R.string.undo), new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        save_data.insert(context);
                        resetCursor(null);
                        Progress_bar_adapter.this.notifyItemInserted(pos);
                    }
                }).show();
    }
}
