package com.wispend.wispend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wispend.wispend.messageDatabase.MessageContract;
import com.wispend.wispend.messageDatabase.MessageDbHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Not using a presenter because not enough time
 * Created by christophE on 2016-10-16.
 */

public class MainActivityModel {



    private Context mContext;

    public MainActivityModel(Context context){
        this.mContext = context;
    }

    public List<MessageItem> getListData(){
        /** Read from database **/
        List<MessageItem> list = new ArrayList<>();
        MessageDbHelper mDbHelper = new MessageDbHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = new String[]{
                MessageContract.FeedEntry._ID,
                MessageContract.FeedEntry.COLUMN_NAME_ICON,
                MessageContract.FeedEntry.COLUMN_NAME_MESSAGE,
                MessageContract.FeedEntry.COLUMN_NAME_DATE,
                MessageContract.FeedEntry.COLUMN_NAME_READ
        };

        // Filter results WHERE "title" = 'My Title'
        String selection = MessageContract.FeedEntry.COLUMN_NAME_READ+ " != ?";
        String[] selectionArgs = { "TRUE" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                MessageContract.FeedEntry.COLUMN_NAME_DATE + " ASC";

        Cursor c = db.query(
                MessageContract.FeedEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        try {
            while (c.moveToNext()) {
                long itemId = c.getLong(c.getColumnIndexOrThrow(MessageContract.FeedEntry._ID));
                String icon = c.getString(c.getColumnIndexOrThrow(MessageContract.FeedEntry.COLUMN_NAME_ICON));
                String message = c.getString(c.getColumnIndexOrThrow(MessageContract.FeedEntry.COLUMN_NAME_MESSAGE));
                String date = c.getString(c.getColumnIndexOrThrow(MessageContract.FeedEntry.COLUMN_NAME_DATE));
                list.add(new MessageItem(message, icon, date, itemId));
            }
        } finally {
            c.close();
        }
        //Sort the list
        Collections.sort(list, new Comparator<MessageItem>(){
            public int compare(MessageItem msg1, MessageItem msg2) {
                // ## Ascending order
                //return emp1.getFirstName().compareToIgnoreCase(emp2.getFirstName()); // To compare string values
                String t1 = msg1.getDate();
                String t2 = msg2.getDate();
                return Integer.valueOf(t2).compareTo(Integer.valueOf(t1)); // To compare integer values

                // ## Descending order
                // return Integer.valueOf(emp2.getFirstName()).compareToIgnoreCase(Integer.valueOf(emp1.getFirstName())); // To compare string values
                // return Integer.valueOf(emp2.getId()).compareTo(emp1.getId()); // To compare integer values
            }
        });
        return list;
    }

    public void readItem(long id){
        MessageDbHelper mDbHelper = new MessageDbHelper(mContext);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(MessageContract.FeedEntry.COLUMN_NAME_READ, "TRUE");

        // Which row to update, based on the title
        String selection = MessageContract.FeedEntry._ID + " = " + id;

        //Possible to delete here; -> or delete all read items
        int count = db.update(
                MessageContract.FeedEntry.TABLE_NAME,
                values,
                selection,
                null);
    }

}
