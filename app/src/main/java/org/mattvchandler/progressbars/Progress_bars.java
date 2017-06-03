package org.mattvchandler.progressbars;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import org.mattvchandler.progressbars.databinding.ActivityProgressBarsBinding;

public class Progress_bars extends AppCompatActivity
{
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
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, 1496429958);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, 1496429958);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "ASDF");
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, "Time until start: ");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: ");
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, "Completed");
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, "Time since completion: ");
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, 0);
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, 1451937600);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, 1483560000);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "AC⚡DC");
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, "Time until start: ");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: ");
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, "Completed");
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, "Time since completion: ");
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, 0);
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 3);
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, 1388865600);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, 1388865600);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "ABCD");
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, "Time until start: ");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: ");
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, "Completed");
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, "Time since completion: ");
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, 0);
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 4);
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, 1483261200);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, 1514797200);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "Queen");
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, "Time until start: ");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: ");
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, "Completed");
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, "Time since completion: ");
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, 0);
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 5);
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, 551476200);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, 551476200);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "Led Zepplin");
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, "Time until start: ");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: ");
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, "Completed");
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, "Time since completion: ");
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, 0);
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 6);
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, 1496429958);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, 1496429958);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "Guns N' Roses");
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, "Time until start: ");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: ");
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, "Completed");
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, "Time since completion: ");
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, 0);
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 7);
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, 1496429958);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, 1496429958);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "Rolling Stones");
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, "Time until start: ");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: ");
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, "Completed");
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, "Time since completion: ");
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, 0);
        db.insert(Progress_bar_contract.Progress_bar_table.TABLE_NAME, null, values);

        values.clear();
        values.put(Progress_bar_contract.Progress_bar_table.ORDER_COL, 8);
        values.put(Progress_bar_contract.Progress_bar_table.START_TIME_COL, 1496429958);
        values.put(Progress_bar_contract.Progress_bar_table.END_TIME_COL, 1496429958);
        values.put(Progress_bar_contract.Progress_bar_table.TITLE_COL, "Eagles");
        values.put(Progress_bar_contract.Progress_bar_table.PRE_TEXT_COL, "Time until start: ");
        values.put(Progress_bar_contract.Progress_bar_table.COUNTDOWN_TEXT_COL, "Time remaining: ");
        values.put(Progress_bar_contract.Progress_bar_table.COMPLETE_TEXT_COL, "Completed");
        values.put(Progress_bar_contract.Progress_bar_table.POST_TEXT_COL, "Time since completion: ");
        values.put(Progress_bar_contract.Progress_bar_table.PRECISION_COL, 2);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_START_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_END_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_PROGRESS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_YEARS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MONTHS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_WEEKS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_DAYS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_HOURS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_MINUTES_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.SHOW_SECONDS_COL, 1);
        values.put(Progress_bar_contract.Progress_bar_table.TERMINATE_COL, 1);
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
