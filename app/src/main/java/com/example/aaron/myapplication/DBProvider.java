package com.example.aaron.myapplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.content.UriMatcher;

import static com.example.aaron.myapplication.RunsContract.TABLE_NAME;

public class DBProvider extends ContentProvider {

    RunsDBHelper runsDBHelper;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(RunsContract.AUTHORITY, TABLE_NAME, 1);
        uriMatcher.addURI(RunsContract.AUTHORITY, TABLE_NAME+"/#", 2);
    }


    @Override
    public boolean onCreate() {
        this.runsDBHelper = new RunsDBHelper(this.getContext());
        return true;
    }

    
    @Override
    public Cursor query( Uri uri,  String[] projection,  String selection,  String[] selectionArgs,  String sortOrder) {
        //Override query to use the runsDBHelper
        return this.runsDBHelper.getWritableDatabase().query(TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType( Uri uri) {
        if (uri.getLastPathSegment()==null)
        {
            return "vnd.android.cursor.dir/RunsContract.data.text";
        }
        else
        {
            return "vnd.android.cursor.item/RunsContract.data.text";
        }
    }

    @Override
    public Uri insert( Uri uri,  ContentValues values) {
        //Select database
        SQLiteDatabase db = runsDBHelper.getWritableDatabase();
        //Assign table name
        String tableName = TABLE_NAME;
        //Insert table name
        long tableNameNumber = db.insert(tableName, null, values);
        //Add tablename number to uri path
        Uri contentUri = ContentUris.withAppendedId(uri, tableNameNumber);
        //Return updated URI
        return contentUri;
    }


    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        throw new UnsupportedOperationException("not implemented");
    }


    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        throw new UnsupportedOperationException("not implemented");
    }
}
