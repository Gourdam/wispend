package com.wispend.wispend.messageDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by christophE on 2016-10-15.
 */

public class MessageDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MessageDbHelper.db";


    //Database creation methods
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MessageContract.FeedEntry.TABLE_NAME + " (" +
                    MessageContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    MessageContract.FeedEntry.COLUMN_NAME_ICON + TEXT_TYPE + COMMA_SEP +
                    MessageContract.FeedEntry.COLUMN_NAME_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    MessageContract.FeedEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    MessageContract.FeedEntry.COLUMN_NAME_READ + TEXT_TYPE + " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + MessageContract.FeedEntry.TABLE_NAME;

    public MessageDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
