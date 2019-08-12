package com.example.aaron.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import static com.example.aaron.myapplication.RunsContract.DB_URI;
import static com.example.aaron.myapplication.RunsContract.KEY_ID;
import static com.example.aaron.myapplication.RunsContract.TABLE_NAME;
import static com.example.aaron.myapplication.RunsContract.RUN_NUM;

public class ServiceClass extends Service {

    //Strings for stop/start cases
    public static final String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final String ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE";

    protected LocationManager locationManager;
    protected MyLocationListener locationListener = new MyLocationListener();

    RunsDBHelper runsDBHelper;
    SQLiteDatabase db;

    Intent intent;
    Location locVar;
    //Initialise total distance ran to 0
    float totalDistanceVar = 0;
    //Set default channel ID for notifications
    private final String CHANNEL_ID = "100";
    //Set default notification ID for notifications
    int NOTIFICATION_ID = 001;
    //Decale intResult for use in SQL table, being assigned values to differentiate each ru
    int intResult = 0;
    //Declare start time used in each run
    String startTime;
    //Declare date used in each run
    String formattedDate;
    //Declare distance ran variable time used in each run
    float distanceVar;


    @Override
    public void onCreate()
    {
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId)
    {
        //Initialise location manager, database and database helper
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        runsDBHelper = new RunsDBHelper(this);
        db = runsDBHelper.getWritableDatabase();

        //If intent contains an action
        if(intent != null) {
            String action = intent.getAction();
            switch (action) {
                //Case for starting foreground service
                case ACTION_START_FOREGROUND_SERVICE:
                    startForegroundService(locationManager,locationListener);
                    break;
                //Case for ending foreground service
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForegroundService(locationManager,locationListener);
                    break;
            }
        }
    }

    @Override
    public void onDestroy()
    {
        //Stop location listener for destroy service
        stopLocationUpdates();
        super.onDestroy();
    }

    private void startForegroundService( LocationManager locationManager, MyLocationListener locationListener)
    {
        //Run the intent in the foregorund
        intent = new Intent(ServiceClass.this, MainActivity.class);
        StartUpForeground(intent);

        //Set minimum frequency of the updates
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    5, // minimum time interval between updates, in ms
                    5, // minimum distance between updates, in metres
                    locationListener);

            //Assing location last to last known location - Used for inserting values into DB
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //locvar assigned location as part of logic to calculate distance travlled
            locVar = location;
        }
        catch (SecurityException e)
        {
            Log.d("g53mdp", "EXCEPTION" + e.toString());
        }

        //Seperate method for location service SQL code - Was quite large hence the separate method
        startLocationServiceSQL();

    }

    private void startLocationServiceSQL()
    {
        //result variable used to check if value is first in database
        String result = "0";
        //Query the RUN_NUM, a variable use to keep count of seperate runs
        Cursor c =  db.rawQuery(("SELECT " + RUN_NUM + " FROM " + TABLE_NAME + " ORDER BY "+ KEY_ID + " DESC LIMIT 1"), null);

        //If statement to stop null exception, to check there was something returned to the cursor
        if(c.getCount() > 0)
        {
            //Go to first returned row, and obtain RUN_NUM (what run it is)
            c.moveToFirst();
            result = c.getString(0);
        }

        //Convert the RUN_NUM into an int
        intResult = Integer.valueOf(result);

        //IF This RUN_NUM as been assigned a value
        if (intResult>=1)
        {
            //Increment RUN_NUM to identify new run
            //Ran once at the start of each service start / real world run
            intResult+=1;
        }

        else {
            //if this RUN_NUM hasn't been assinged a value (is the first row in table)
            intResult=1;
        }

        // Set/assign date and assign it a format
        Date date = new Date();
        String strDateFormat = "dd-MM-yy";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        formattedDate= dateFormat.format(date);

        // Set/assign time
        startTime = getCurrentTimeUsingDate();

        //init distance variable as part of distance ran logic
        distanceVar = 0;

    }

    private void stopForegroundService(LocationManager locationManager, MyLocationListener locationListener)
    {
        // Stop foreground service and remove the notification.
        stopForeground(true);

        // Stop the foreground service.
        stopSelf();
    }

    public void stopLocationUpdates()
    {
        //Stops location manager updating
        locationManager.removeUpdates(locationListener);
    }

    public class MyLocationListener implements LocationListener {
    //Various overridden methods from super class
        @Override
        public void onLocationChanged(Location location) {
            // Call location update method, whenever the location changes
            LocationUpdate(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    }

    public void LocationUpdate(Location location){
        //Create class for stroing values that the ContentResolver can process
        ContentValues newValues = new ContentValues();
        //Insert values to Content Values class
        newValues.put(RunsContract.LAT, location.getLatitude());
        newValues.put(RunsContract.LONGI, location.getLongitude());
        newValues.put(RunsContract.TIME, getCurrentTimeUsingDate());
        newValues.put(RunsContract.START_TIME, startTime);
        newValues.put(RunsContract.DATE, formattedDate);
        newValues.put(RunsContract.RUN_NUM, intResult);

        //Some logic for calculating distance travelled
        distanceVar = location.distanceTo(locVar);
        totalDistanceVar += distanceVar;
        newValues.put(RunsContract.DIST, totalDistanceVar);
        locVar = location;

        //Insert new values for uri
        Uri uri = getContentResolver().insert(DB_URI, newValues);
        // Start intent for listPop/listBuild() in main Activity to update distance value on screen
        Intent intent = new Intent("com.example.listPop");
        //Send broadcast - to be received and processed in main activity
        sendBroadcast(intent);
    }

    public static String getCurrentTimeUsingDate() {
        //Separate method for getting time
        Date date = new Date();
        String dateFormatString = "HH:mm:ss";
        DateFormat dateFormat = new SimpleDateFormat(dateFormatString);
        String dateFormatted= dateFormat.format(date);
        return dateFormatted;
    }

    private void StartUpForeground(Intent intent)
    {
        //Implement notification for service/notification manager for it
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Create the NotificationChannel, ensure software is API 26+ as this won't work otherwise
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "channel name";
            String channelDescription = "channel description";
            int importanceLevel = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, channelName,importanceLevel);
            notificationChannel.setDescription(channelDescription);
            notificationManager.createNotificationChannel(notificationChannel);
        }


        //Set flag for notification
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //Pending intent (reference) for notification
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //Build notification and set varying attributes
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Running tracker is running")
                .setContentText("Click here to return to app and stop")
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        //Start notification in foreground
        startForeground(NOTIFICATION_ID, mBuilder.build());
    }

    //Default override for super method
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
