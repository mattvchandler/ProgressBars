package org.mattvchandler.progressbars;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class progress_bars extends AppCompatActivity
{
    private TextView text;
    private ListView list;
    private List<String> listValues;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_bars);

        text = (TextView)findViewById(R.id.mainText);
        listValues = new ArrayList<String>();
        listValues.add("ASDF");
        listValues.add("ABCD");
        listValues.add("ACDC");

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(this, R.layout.progress_bar_row,R.id.listText, listValues);

        list = (ListView)findViewById(R.id.mainList);
        list.setAdapter(myAdapter);
        list.setEmptyView(findViewById(R.id.empty));

        list.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        String selected = (String)list.getItemAtPosition(position);
                        text.setText("Selected: " + selected + " at pos: " + position);
                        Snackbar.make(findViewById(R.id.mainList), "you pressed " + selected + "!", Snackbar.LENGTH_LONG)
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
    }
}
