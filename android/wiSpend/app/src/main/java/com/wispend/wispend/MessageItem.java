package com.wispend.wispend;

/**
 * Created by christophE on 2016-10-15.
 */

public class MessageItem {
    private String mTitle;
    private String mImage;
    private String mDate;
    private long mDatabaseID;

    public MessageItem(String title, String image, String date, long databaseID){
        mTitle = title;
        mImage = image;
        mDate = date;
        mDatabaseID = databaseID;
    }
    public String getTitle(){
        return mTitle;
    }
    public String getImage(){
        return mImage;
    }
    public String getDate(){
        return mDate;
    }
    public long getID(){
        return mDatabaseID;
    }
}
