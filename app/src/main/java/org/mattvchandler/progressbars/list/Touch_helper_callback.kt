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

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

// handle drag gestures for reorder and dismiss in RecyclerView
class Touch_helper_callback(private val adapter: Adapter): ItemTouchHelper.Callback()
{

    // long press and drag to reorder list
    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
    {
        adapter.notifyItemMoved(source.adapterPosition, target.adapterPosition)
        return true
    }

    // swipe to delete row
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int)
    {
        adapter.on_item_dismiss(viewHolder.adapterPosition)
    }

    override fun isLongPressDragEnabled(): Boolean
    {
        return true
    }

    override fun isItemViewSwipeEnabled(): Boolean
    {
        return true
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int
    {
        // reorder up and down, swipe left and right
        return ItemTouchHelper.Callback.makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START or ItemTouchHelper.END)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int)
    {
        super.onSelectedChanged(viewHolder, actionState)
        // notify when a row is selected
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE)
            (viewHolder as Adapter.Progress_bar_row_view_holder).on_selected()
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
    {
        super.clearView(recyclerView, viewHolder)
        // notify when a row is deselected
        (viewHolder as Adapter.Progress_bar_row_view_holder).on_cleared()
    }
}
