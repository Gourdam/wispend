package com.wispend.wispend.service;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.wispend.wispend.MainActivity;
import com.wispend.wispend.StaticsVariables;
import com.wispend.wispend.messageDatabase.MessageContract;
import com.wispend.wispend.messageDatabase.MessageDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.wispend.wispend.StaticsVariables.GET_LENGTH;

/**
 * Created by christophE on 2016-10-15.
 */
public class MessageService extends Service {


    private static int number;
    private Handler timerHandler;
    private ApiRequestRun timerRunnable;
    public final int TIMER_CHECK_DELAY = 10000;
    /**
     * Called when the service has been started from an activity
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        super.onStartCommand(intent, flags, startId);
        return START_STICKY; //Only stop explicitly
    }

    /**
     * Activities cannot bind to this service
     */
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    /**
     * One time setup commands
     */
    public void onCreate(){
        timerHandler = new Handler();
        timerRunnable = new ApiRequestRun();
        timerHandler.postDelayed(timerRunnable,0);
        super.onCreate();
    }

    /**
     * Destruction commands
     */
    public void onDestroy(){
        super.onDestroy();
        timerHandler.removeCallbacks(timerRunnable);
    }

    class ApiRequestRun implements Runnable{
        @Override
        public void run(){

            /**Get messages**/
            ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String networkString = downloadUrl(StaticsVariables.URL);
                            if(!networkString.isEmpty()) {
                                try {

                                    String icon = "invalid";
                                    String message = "invalid";
                                    String date = "invalid";
                                    /**Add messages to database**/
                                    MessageDbHelper mDbHelper = new MessageDbHelper(getApplicationContext());
                                    // Gets the data repository in write mode
                                    SQLiteDatabase db = mDbHelper.getWritableDatabase();
                                    JSONObject obj = new JSONObject(networkString);
                                    JSONArray array = obj.getJSONArray("notifications");
                                    for(int n = 0; n < array.length(); n++)
                                    {

                                        // Create a new map of values, where column names are the keys
                                        JSONObject object = array.getJSONObject(n);
                                        icon = object.getString("image");
                                        message = object.getString("title");
                                        date = object.getString("timestamp");
                                        ContentValues values = new ContentValues();
                                        values.put(MessageContract.FeedEntry.COLUMN_NAME_ICON, icon);
                                        values.put(MessageContract.FeedEntry.COLUMN_NAME_MESSAGE, message);
                                        values.put(MessageContract.FeedEntry.COLUMN_NAME_DATE, date);
                                        values.put(MessageContract.FeedEntry.COLUMN_NAME_READ, "FALSE");

                                        // Insert the new row, returning the primary key value of the new row
                                        long newRowId = db.insert(MessageContract.FeedEntry.TABLE_NAME, null, values);
                                        /**Notify user of messages**/
                                        generateNotification(message,(int)newRowId);
                                    }


                                } catch (JSONException j) {
                                    j.printStackTrace();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(run);
                thread.start();
            }
            timerHandler.postDelayed(this, TIMER_CHECK_DELAY);
        }

    }

    protected void generateNotification(String body,int id){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setSmallIcon(android.R.drawable.sym_def_app_icon);
        mBuilder.setContentTitle("wiSpend");
        mBuilder.setContentText(body);
        // Sets an ID for the notification
        int mNotificationId = id;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent intent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
        mBuilder.setContentIntent(intent);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("com.wispend.reloadmessages");
        sendBroadcast(broadcastIntent);
    }

    /**
     * Network call
     */
    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = GET_LENGTH;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            int j = response;
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
