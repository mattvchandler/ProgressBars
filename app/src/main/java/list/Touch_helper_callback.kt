/*
Copyright (C) 2020 Matthew Chandler

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

import android.graphics.Canvas
import android.os.Build
import android.util.TypedValue
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import org.mattvchandler.progressbars.R
import kotlin.math.min

// handle drag gestures for reorder and dismiss in RecyclerView
class Touch_helper_callback(private val adapter: Adapter): ItemTouchHelper.Callback()
{
    // long press and drag to reorder list
    override fun onMove(recyclerView: RecyclerView, source: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean
    {
        adapter.move_item(source.adapterPosition, target.adapterPosition)
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
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START or ItemTouchHelper.END)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int)
    {
        super.onSelectedChanged(viewHolder, actionState)
        // notify when a row is selected
        if(actionState != ItemTouchHelper.ACTION_STATE_IDLE)
            adapter.on_selected(viewHolder!!.adapterPosition)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean)
    {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if(isCurrentlyActive)
        {
            if(Build.VERSION.SDK_INT >= 21)
            {
                // raise the selected row up (it will do a little bit of this by default, but we can make it more noticeable
                ViewCompat.setElevation(viewHolder.itemView, recyclerView.context.resources.getDimension(R.dimen.selected_elevation))
            }
            else
            {
                // get the background color
                val tv = TypedValue()
                recyclerView.context.theme.resolveAttribute(android.R.attr.colorBackground, tv, true)
                // make it darker and-semi transparent
                viewHolder.itemView.setBackgroundColor(min(tv.data - 0x40202020, 0))
            }
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder)
    {
        super.clearView(recyclerView, viewHolder)
        // notify when a row is deselected
        adapter.on_cleared(viewHolder.adapterPosition)

        if(Build.VERSION.SDK_INT >= 21)
        {
            // lower selected row
            ViewCompat.setElevation(viewHolder.itemView, 0.0f)
        }
        else
        {
            // reset the original background color
            val tv = TypedValue()
            recyclerView.context.theme.resolveAttribute(android.R.attr.colorBackground, tv, true)
            viewHolder.itemView.setBackgroundColor(tv.data)
        }
    }
}
