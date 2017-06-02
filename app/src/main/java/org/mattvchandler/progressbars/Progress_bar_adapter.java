package org.mattvchandler.progressbars;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mattvchandler.progressbars.databinding.ProgressBarRowBinding;

public class Progress_bar_adapter extends RecyclerView.Adapter<Progress_bar_adapter.Progress_bar_row_view_holder>
{

    public static class Progress_bar_row_view_holder extends RecyclerView.ViewHolder
    {
        ProgressBarRowBinding row_binding;

        public Progress_bar_row_view_holder(View v)
        {
            super(v);
            row_binding = DataBindingUtil.bind(v);
        }

        public void bind_cursor(Cursor cursor)
        {
            // TODO: all of the other fields
            // TODO: timeText, percentage, and progressBar will need to be set by a timer
            Progress_bar_data data = new Progress_bar_data(
                    cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.TITLE_COL)),
                    "2017-01-23",
                    "10:11:12",
                    "2017-01-23",
                    "10:11:12",
                    "42.00%",
                    42,
                    cursor.getString(cursor.getColumnIndexOrThrow(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL)));
            row_binding.setData(data);
        }
    }

    private Cursor cursor;

    public Progress_bar_data progress_bar_data;

    public Progress_bar_adapter(Cursor cur)
    {
        cursor = cur;
    }

    @Override
    public Progress_bar_row_view_holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar_row, parent, false);
        return new Progress_bar_row_view_holder(v);
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
}
