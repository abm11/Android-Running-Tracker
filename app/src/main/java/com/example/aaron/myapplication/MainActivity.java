package com.example.aaron.myapplication;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import static com.example.aaron.myapplication.RunsContract.DB_URI;
import static com.example.aaron.myapplication.RunsContract.DIST;

public class MainActivity extends AppCompatActivity {
    Cursor c; //Cursor for retrieving distance values
    int state = 1; //Int for mainting state of service (1 = ready to run, 0 = Ready to end)
    TextView distanceValue; //Variable to distance value onscreen/UI

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //Ask user for SD card access and location acess
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.ACCESS_FINE_LOCATION}, 0);

        setContentView(R.layout.activity_main);
        distanceValue = (TextView)findViewById(R.id.distanceValue);

        //Intent filter for receiving system broadcast (listPop call from service)
        IntentFilter filter = new IntentFilter("com.example.listPop");
        //Register Subclass Broadcast receiver
        registerReceiver(myReceiver, filter);
   }

   //Broadcast sub class receiver designed to call listBuild (meth updates distance on screen)
    private BroadcastReceiver myReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {listBuild();}
    };

    //Method for updating running distance value on screen
    public void listBuild()
    {
        //Build cursor for query resust
        c =  getContentResolver().query(DB_URI,null, null, null, null);

        //Iterate through list as it updates
        while (c.moveToNext()) {
            //Obtain distance column index
            int distanceRan = c.getColumnIndex(DIST);
            //Use distance column index to obtain distance column values
            String getValues = c.getString(distanceRan);
            //Insert distance column value's to on screen UI
            distanceValue.setText(getValues + " (m)");
        }
    }

    //onClick method for starting and stopping the service class for location listener, updating database, etc
    public void serviceStartStop(View v)
    {
        //Logic for checking if the service is not running
        if (state==1) //Service is not running
        {
            //New intent for service class
            Intent intent = new Intent(this, ServiceClass.class);
            //Insert case for starting service
            intent.setAction(ServiceClass.ACTION_START_FOREGROUND_SERVICE);
            //Start service
            startService(intent);
            //Update state to reflect service running
            state = 0;
            //Exit method
            return;
        }
        //Logic for checking if the service is still running
        if (state==0) //If service is running
        {
            //New intent for service class
            Intent intent = new Intent(this, ServiceClass.class);
            //Insert case for ending service
            intent.setAction(ServiceClass.ACTION_STOP_FOREGROUND_SERVICE);
            //Start service
            startService(intent);
            //Update state to reflect service running
            state = 1;
            //Exit method
            return;
        }
    }

    public void historyActivity(View v)
    {
        //Logic for checking if the service is still running
        if (state==0) //If service is running
        {
            //New intent for service class
            Intent intent = new Intent(this, ServiceClass.class);
            //Insert case for ending service
            intent.setAction(ServiceClass.ACTION_STOP_FOREGROUND_SERVICE);
            //Start service
            startService(intent);
            //Update state to reflect service running
            state = 1;
            //Exit method
        }
        //Intent for changing activity to history
        Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
        //Start history activity
        startActivity(intent);
    }
}
