package org.mattvchandler.progressbars;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.mattvchandler.progressbars.databinding.ActivityProgressBarsBinding;

import java.util.ArrayList;
import java.util.List;

public class Progress_bars extends AppCompatActivity
{
    private List<Progress_bar_data> listValues;
    private ActivityProgressBarsBinding binding;
    private RecyclerView.LayoutManager layout_man;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_progress_bars);

        listValues = new ArrayList<Progress_bar_data>();
        listValues.add(new Progress_bar_data("ASDF", "3 days and 42 seconds"));
        listValues.add(new Progress_bar_data("ABCD", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("AC⚡DC", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Queen", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Weird Al", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Led Zepplin", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Rolling Stones", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Foreigner", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Eagles", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Sixpence none the richer", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Bon Jovi", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("David Bowie", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Talking Heads", "3 days, 2 minutes, and 42 seconds"));
        listValues.add(new Progress_bar_data("Toto", "3 days, 2 minutes, and 42 seconds"));

        layout_man = new LinearLayoutManager(this);
        binding.mainList.setLayoutManager(layout_man);
        adapter = new Progress_bar_adapter(listValues);

        binding.mainList.setAdapter(adapter);
        binding.mainList.addItemDecoration(new DividerItemDecoration(binding.mainList.getContext(), DividerItemDecoration.VERTICAL));
        // binding.mainList.setEmptyView(binding.empty);

        /*
        binding.mainList.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        String selected = (String)binding.mainList.getItemAtPosition(position);
                        Snackbar.make(binding.mainList, "you pressed " + selected + "!", Snackbar.LENGTH_LONG)
                                .setAction("Oops!",
                                        new View.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(View v)
                                            {
                                                Toast.makeText(getApplicationContext(), "Too bad. You're stuck with it now", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                .show();
                    }
                }
        );
        */
    }
}
