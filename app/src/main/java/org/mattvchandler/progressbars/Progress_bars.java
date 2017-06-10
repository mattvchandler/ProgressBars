package org.mattvchandler.progressbars;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.mattvchandler.progressbars.databinding.ActivityProgressBarsBinding;

public class Progress_bars extends AppCompatActivity
{
    private ActivityProgressBarsBinding binding;
    private Progress_bar_adapter adapter;

    public static final int UPDATE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        int theme = getSharedPreferences(getResources().getString(R.string.shared_prefs), MODE_PRIVATE)
                .getInt(getResources().getString(R.string.theme_pref), R.style.Theme_progress_bars);
        setTheme(theme);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_progress_bars);
        setSupportActionBar(binding.progressBarToolbar);
        binding.mainList.addItemDecoration(new DividerItemDecoration(binding.mainList.getContext(), DividerItemDecoration.VERTICAL));

        // on first run, create a new prog bar if empty
        if(savedInstanceState == null)
        {
            SQLiteDatabase db = new Progress_bar_DB(this).getReadableDatabase();
            Cursor cursor = db.rawQuery(Progress_bar_table.SELECT_ALL_ROWS, null);
            if(cursor.getCount() == 0)
            {
                new Progress_bar_data(this).insert(this);
            }
            else
            {
                // clean up existing data
                Progress_bar_table.cleanup_order(this);
            }
            cursor.close();
            db.close();

            Notification_handler.reset_all_alarms(this);
        }
        adapter = new Progress_bar_adapter(this);

        binding.mainList.setLayoutManager(new LinearLayoutManager(this));
        binding.mainList.setAdapter(adapter);

        ItemTouchHelper touch_helper = new ItemTouchHelper(new Progress_bar_row_touch_helper_callback(adapter));
        touch_helper.attachToRecyclerView(binding.mainList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.progress_bar_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
        case R.id.add_butt:
            startActivityForResult(new Intent(this, Settings.class), UPDATE_REQUEST);
            return true;
        case R.id.settings:
            startActivity(new Intent(this, Progress_bar_prefs.class));
            return true;
        case R.id.about:
            new About_dialog_frag().show(getSupportFragmentManager(), "about");
            return  true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data)
    {
        if(request_code == UPDATE_REQUEST && result_code == RESULT_OK)
        {
            final Progress_bar_data new_data = (Progress_bar_data)data.getSerializableExtra(Settings.RESULT_NEW_DATA);
            final Progress_bar_data old_data = (Progress_bar_data)data.getSerializableExtra(Settings.RESULT_OLD_DATA);

            adapter.resetCursor(null);

            if(old_data == null)
            {
                adapter.notifyItemInserted(adapter.getItemCount());

                Snackbar.make(binding.mainList, getResources().getString(R.string.added_new,  new_data.title), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.undo), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                int pos = adapter.find_by_rowid(new_data.rowid);
                                new_data.delete(Progress_bars.this);

                                adapter.resetCursor(null);
                                adapter.notifyItemRemoved(pos);
                            }
                        }).show();
            }
            else
            {
                adapter.notifyItemChanged(adapter.find_by_rowid(new_data.rowid));

                Snackbar.make(binding.mainList, getResources().getString(R.string.saved, new_data.title), Snackbar.LENGTH_LONG)
                        .setAction(getResources().getString(R.string.undo), new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                old_data.update(Progress_bars.this);

                                adapter.resetCursor(null);
                                adapter.notifyItemChanged(adapter.find_by_rowid(old_data.rowid));
                            }
                        }).show();
            }
        }
    }
}
