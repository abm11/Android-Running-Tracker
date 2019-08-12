package com.example.aaron.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static com.example.aaron.myapplication.RunsContract.DATABASE_VERSION;
import static com.example.aaron.myapplication.RunsContract.DATE;
import static com.example.aaron.myapplication.RunsContract.LAT;
import static com.example.aaron.myapplication.RunsContract.LOC;
import static com.example.aaron.myapplication.RunsContract.LONGI;
import static com.example.aaron.myapplication.RunsContract.TABLE_NAME;
import static com.example.aaron.myapplication.RunsContract.TIME;
import static com.example.aaron.myapplication.RunsContract.DIST;
import static com.example.aaron.myapplication.RunsContract.KEY_ID;
import static com.example.aaron.myapplication.RunsContract.RUN_NUM;
import static com.example.aaron.myapplication.RunsContract.START_TIME;

public class RunsDBHelper extends SQLiteOpenHelper {

    public RunsDBHelper(Context context) {
        super(context, "DBHelper", null, DATABASE_VERSION);
    }

    //SQL code for creating the table with the needed columns
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +" (" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + LOC + " VARCHAR (128), " + TIME + " VARCHAR(32), " + START_TIME + " VARCHAR(32), "
                + DATE + " VARCHAR(32), " + LAT + " INT(128), " + LONGI + " INT(128), " + DIST + " INT(128)," + RUN_NUM + " INT(128)"  + ");");
    }

    //SQL code to drop the old table when the table is upgraded
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //SQL code to drop the old table when the table is downgraded
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
