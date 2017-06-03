package org.mattvchandler.progressbars;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import org.mattvchandler.progressbars.Progress_bar_adapter;

/**
 * Created by matt on 6/3/17.
 */

public class Progress_bar_row_touch_helper_callback extends ItemTouchHelper.SimpleCallback
{

    private Progress_bar_adapter adapter;

    Progress_bar_row_touch_helper_callback(Progress_bar_adapter adapter_in)
    {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        adapter = adapter_in;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target)
    {
        adapter.on_item_move(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
    {
        adapter.on_item_dismiss(viewHolder.getAdapterPosition());
    }
}
