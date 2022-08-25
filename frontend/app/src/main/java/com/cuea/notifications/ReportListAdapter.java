package com.cuea.notifications;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ReportListAdapter extends ArrayAdapter<String>{
    //array of sender
    private final String[] senders;
    //array of dates
    private final String[] contents;
    //array of contents
    private final String[] dates;
    //activity
    private final Activity context;

    public ReportListAdapter(Activity context,String[] senders, String[] contents,String[] dates){
        super(context,R.layout.report_array_list,senders);
        this.context=context;
        this.senders=senders;
        this.contents=contents;
        this.dates=dates;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowview=layoutInflater.inflate(R.layout.report_array_list,null,false);
        TextView date = (TextView) rowview.findViewById(R.id.rptxtdate);
        TextView sender = (TextView) rowview.findViewById(R.id.rptxtsender);
        TextView content = (TextView) rowview.findViewById(R.id.rptxtcontent);
        date.setText(dates[position]);
        sender.setText(senders[position]);
        content.setText(contents[position]);
        return rowview;
    }
}
