package com.example.aaron.myapplication;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.Arrays;

import static com.example.aaron.myapplication.RunsContract.DIST;
import static com.example.aaron.myapplication.RunsContract.START_TIME;
import static com.example.aaron.myapplication.RunsContract.DATE;
import static com.example.aaron.myapplication.RunsContract.LAT;
import static com.example.aaron.myapplication.RunsContract.LONGI;
import static com.example.aaron.myapplication.RunsContract.TABLE_NAME;
import static com.example.aaron.myapplication.RunsContract.RUN_NUM;

public class HistoryActivity extends AppCompatActivity {
    //Instantiate database helper
    RunsDBHelper runsDBHelper;
    //Instantiate database
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set content view to
        setContentView(R.layout.activity_history);
        listBuild();
    }

    public void listBuild(){
        //Assign database helper the context
        runsDBHelper = new RunsDBHelper(this);
        //Opens database
        db = runsDBHelper.getWritableDatabase();
        //Cursor for returning the history of runs, but in GROUP BY TABLE_NUM form, this displays only summaries
        // of each run, rather than the individual rows that compose each run.
        Cursor c =  db.rawQuery(("SELECT *" + " FROM " + TABLE_NAME + " GROUP BY " + RUN_NUM), null);


        SimpleCursorAdapter dataAdapter;

        //Add desired table columns
        String column[] = new String[]{
                DATE,
                START_TIME,
                DIST,
                RUN_NUM
        };

        //Add desired XML interface
        int[] views = new int[]{
                R.id.DateView,
                R.id.StartTime,
                R.id.DistanceView,
                R.id.RunNumberView
        };

        //Build data adapter using cursor, coluns and XML interface
        dataAdapter = new SimpleCursorAdapter(this, R.layout.db_item, c, column, views,0);
        //Set list view
        ListView listView = (ListView) findViewById(R.id.listView);
        //Apply data adapter to listview
        listView.setAdapter(dataAdapter);
    }

    //Button for returning to main activity
    public void mainActivity (View v)
    {
        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
