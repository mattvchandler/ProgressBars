package org.mattvchandler.progressbars;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import org.mattvchandler.progressbars.Progress_bar_adapter;

public class Progress_bar_row_touch_helper_callback extends ItemTouchHelper.Callback
{
    private Progress_bar_adapter adapter;

    Progress_bar_row_touch_helper_callback(Progress_bar_adapter adapter_in)
    {
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

    @Override
    public boolean isLongPressDragEnabled()
    {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled()
    {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState)
    {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE)
            ((Progress_bar_adapter.Progress_bar_row_view_holder)viewHolder).on_selected();
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        super.clearView(recyclerView, viewHolder);
        ((Progress_bar_adapter.Progress_bar_row_view_holder)viewHolder).on_cleared();
    }
}
