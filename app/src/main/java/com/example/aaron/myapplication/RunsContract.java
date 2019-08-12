package com.example.aaron.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RunsContract {

    //These are the constants used in the database.

    public static final String AUTHORITY = "com.example.aaron.myapplication.DBProvider";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "RunningDB";
    public static final String TIME = "Time";
    public static final String START_TIME = "StartTime";
    public static final String DATE = "DATE";
    public static final String LAT = "Latitude";
    public static final String LONGI = "Longitude";
    public static final String DIST = "Distances";
    public static final String LOC = "Location";
    public static final Uri DB_URI = Uri.parse("content://"+AUTHORITY+"/"+TABLE_NAME);
    public static final String KEY_ID = "_id";
    public static final String RUN_NUM = "RunNumber";

}
