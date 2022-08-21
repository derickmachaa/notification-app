package com.cuea.notifications;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class StudentListAdapter extends ArrayAdapter<String> {
    //array of main title
    private final String[] maintitle;
    //array of subtitle
    private final String[] subtitle;
    //array of imagename
    private final Integer[] imagename;
    //activity
    private final Activity context;

    public StudentListAdapter(Activity context,String[] maintitle, String[] subtitle, Integer[] imagename){
        super(context,R.layout.reciever_sms,maintitle);
        this.context=context;
        this.maintitle=maintitle;
        this.subtitle=subtitle;
        this.imagename=imagename;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //inflate the view
        LayoutInflater inflater = context.getLayoutInflater();
        View rowview = inflater.inflate(R.layout.reciever_sms,null,true);
        TextView header = (TextView) rowview.findViewById(R.id.st_sms_title);
        TextView subheader = (TextView) rowview.findViewById(R.id.st_sms_subtitle);
        ImageView img = (ImageView) rowview.findViewById(R.id.img_sms_status);

        header.setText(maintitle[position]);
        subheader.setText(subtitle[position]);
        img.setImageResource(imagename[position]);
        //return rowview
        return  rowview;
    }
}
