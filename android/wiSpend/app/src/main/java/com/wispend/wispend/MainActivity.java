package com.wispend.wispend;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wispend.wispend.service.MessageService;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    protected Intent serviceIntent;
    private DataUpdateReceiver dataUpdateReceiver;
    private ListView mListView;
    private MessageAdapter mAdapter;
    private MainActivityModel mModel;
    private List<MessageItem> mList = new ArrayList<MessageItem>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mModel = new MainActivityModel(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Start a service
        serviceIntent = new Intent(MainActivity.this, MessageService.class);
        startService(serviceIntent);

        mListView = (ListView)findViewById(R.id.message_list);
        mListView.setEmptyView(findViewById(R.id.empty_id));
        mAdapter = new MessageAdapter(this,mList);
        mListView.setAdapter(mAdapter);

        //mList.clear();
        //mList.addAll(mModel.getListData());

        //Dissapears the item once clicked
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long itemId = mList.get(position).getID();
                NotificationManager nMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                nMgr.cancel((int)itemId);
                mModel.readItem(itemId);
                mList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {



            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    22);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_off) {
            stopService(serviceIntent);
        }else if(id == R.id.action_on){
            if(serviceIntent==null) {
                serviceIntent = new Intent(MainActivity.this, MessageService.class);
            }
            startService(serviceIntent);
        }else if(id == R.id.action_refresh){
            reloadData();
        }else if(id == R.id.action_clear){
            for(MessageItem mitem: mList) {
                NotificationManager nMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                long itemId = mitem.getID();
                mModel.readItem(itemId);
                nMgr.cancel((int)itemId);
            }
            mList.clear();
            mAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }
    private class DataUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("com.wispend.reloadmessages")) {
                reloadData();
            }
        }
    }
    private void reloadData(){
        mList.clear();
        mList.addAll(mModel.getListData());
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onResume(){
        if (dataUpdateReceiver == null) dataUpdateReceiver = new DataUpdateReceiver();
        IntentFilter intentFilter = new IntentFilter("com.wispend.reloadmessages");
        registerReceiver(dataUpdateReceiver, intentFilter);
        reloadData();
        super.onResume();
    }
    @Override
    public void onPause(){
        if (dataUpdateReceiver != null) unregisterReceiver(dataUpdateReceiver);
        super.onPause();
    }
}
