package org.mattvchandler.progressbars;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.mattvchandler.progressbars.databinding.ActivityProgressBarsBinding;

import static android.support.v7.app.AppCompatDelegate.MODE_NIGHT_AUTO;
import static java.lang.Math.random;

public class Progress_bars extends AppCompatActivity
{
    private ActivityProgressBarsBinding binding;
    private RecyclerView.LayoutManager layout_man;
    private Progress_bar_adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_progress_bars);
        setSupportActionBar(binding.progressBarToolbar);
        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_AUTO);

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

        // TODO: switch to readable DB here
        Cursor cursor = db.rawQuery(Progress_bar_contract.Progress_bar_table.SELECT_ALL_ROWS, null);

        binding.mainList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Progress_bar_adapter(cursor);

        binding.mainList.setAdapter(adapter);
        binding.mainList.addItemDecoration(new DividerItemDecoration(binding.mainList.getContext(), DividerItemDecoration.VERTICAL));

        ItemTouchHelper touch_helper = new ItemTouchHelper(new Progress_bar_row_touch_helper_callback(adapter));
        touch_helper.attachToRecyclerView(binding.mainList);
        // TODO: empty view?
        // binding.mainList.setEmptyView(binding.empty);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.progress_bar_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
        case R.id.add_butt:
            // TODO: implement
            if(random() >= 0.5)
            {
                Toast.makeText(getApplicationContext(), "You tried to add something, but that isn't implemented yet. swapping instead...", Toast.LENGTH_SHORT).show();
                adapter.on_item_move(0, 1);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "You tried to add something, but that isn't implemented yet. deleting instead...", Toast.LENGTH_SHORT).show();
                adapter.on_item_dismiss(0);
            }
            break;
        }
        return false;
    }
}
