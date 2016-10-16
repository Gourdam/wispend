package com.wispend.wispend.messageDatabase;

import android.provider.BaseColumns;

/**
 * Created by christophE on 2016-10-15.
 */

public final class MessageContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private MessageContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_NAME_ICON = "icon";
        public static final String COLUMN_NAME_MESSAGE = "message";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_READ = "read";
    }
}