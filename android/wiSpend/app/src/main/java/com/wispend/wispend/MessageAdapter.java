package com.wispend.wispend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by christophE on 2016-10-15.
 */

public class MessageAdapter extends ArrayAdapter<MessageItem> {

    SimpleDateFormat sdf = new SimpleDateFormat("MMM W, yyyy, h:mm a");

    public MessageAdapter(Context context, List<MessageItem> list){
        super(context,0,list);
    }


    private static class ViewHolder{
        TextView messageDisplay;
        ImageView icon;
        TextView timestamp;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        View v = convertView;
        MessageItem item = getItem(position);
        ViewHolder holder;
        if(convertView==null){
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.message_list_view, null);
            holder = new ViewHolder();
            holder.messageDisplay = (TextView)v.findViewById(R.id.message_field);
            holder.timestamp = (TextView)v.findViewById(R.id.date_field);
            holder.icon = (ImageView)v.findViewById(R.id.icon_field);
            v.setTag(holder);
        }else{
            holder = (ViewHolder) v.getTag();
        }
        if(item!=null) {
            holder.messageDisplay.setText(item.getTitle());
            long ls = Long.parseLong(item.getDate());
            //long ls = 1;
            ls = ls*1000;
            holder.timestamp.setText(sdf.format(new Date(ls)));
            //TODO:Set icon here too
        }
        return v;
    }
}
