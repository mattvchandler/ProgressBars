package org.mattvchandler.progressbars;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.mattvchandler.progressbars.databinding.ProgressBarRowBinding;

import java.util.List;

public class Progress_bar_adapter extends RecyclerView.Adapter<Progress_bar_adapter.Progress_bar_row_view_holder>
{

    public static class Progress_bar_row_view_holder extends RecyclerView.ViewHolder
    {
        private ProgressBarRowBinding row_binding;

        public Progress_bar_row_view_holder(View v)
        {
            super(v);
            row_binding = DataBindingUtil.bind(v);
        }

        public ProgressBarRowBinding getBinding()
        {
            return row_binding;
        }
    }

    private List<Progress_bar_data> progress_bar_list;
    public Progress_bar_adapter(List<Progress_bar_data> list)
    {
        progress_bar_list = list;
    }

    @Override
    public Progress_bar_row_view_holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.progress_bar_row, parent, false);
        Progress_bar_row_view_holder holder = new Progress_bar_row_view_holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(Progress_bar_row_view_holder holder, int position)
    {
        final Progress_bar_data data = progress_bar_list.get(position);
        holder.getBinding().setVariable(BR.progress_bar_data, data);
        holder.getBinding().executePendingBindings();
    }

    @Override
    public int getItemCount()
    {
        return progress_bar_list.size();
    }
}
