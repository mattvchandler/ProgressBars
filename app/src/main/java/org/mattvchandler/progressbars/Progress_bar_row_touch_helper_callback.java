package org.mattvchandler.progressbars;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import org.mattvchandler.progressbars.Progress_bar_adapter;

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

// handle drag gestures for reorder and dismiss in RecyclerView
class Progress_bar_row_touch_helper_callback extends ItemTouchHelper.Callback
{
    private final Progress_bar_adapter adapter;

    Progress_bar_row_touch_helper_callback(Progress_bar_adapter adapter_in)
    {
        adapter = adapter_in;
    }

    // longpress and drag to reorder list
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder source, RecyclerView.ViewHolder target)
    {
        adapter.on_item_move(source.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    // swipe to delete row
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
        // reorder up and down, swipe left and right
        return makeMovementFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.START | ItemTouchHelper.END);
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState)
    {
        super.onSelectedChanged(viewHolder, actionState);
        // notify when a row is selected
        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE)
            ((Progress_bar_adapter.Progress_bar_row_view_holder)viewHolder).on_selected();
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
    {
        super.clearView(recyclerView, viewHolder);
        // notify when a row is deselected
        ((Progress_bar_adapter.Progress_bar_row_view_holder)viewHolder).on_cleared();
    }
}
