package org.mattvchandler.progressbars;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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


        SQLiteDatabase db = new Progress_bar_DB(this).getWritableDatabase();

        db.execSQL("DELETE FROM " + Progress_bar_contract.Progress_bar_table.TABLE_NAME);

        ContentValues values = new ContentValues();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "ASDF");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: 3 days, 2 eons, and 42 seconds");
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "AC⚡DC");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: 3 days, 2 millibars, and 42 seconds");
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "ABCD");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: 3 days, 2 triganic pu, and 42 seconds");
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 4);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "Queen");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: 3 days, 0 oxford commas and 42 seconds");
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        Cursor cursor = db.rawQuery("SELECT * FROM " + Progress_bar_contract.Progress_bar_table.TABLE_NAME +
                                     " ORDER BY " + Progress_bar_contract.Progress_bar_table.ORDER_COL, null);

        binding.mainList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Progress_bar_adapter(cursor);

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
