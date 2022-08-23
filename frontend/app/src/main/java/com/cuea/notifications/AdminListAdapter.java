package com.cuea.notifications;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class AdminListAdapter extends ArrayAdapter<String> {
        //array of main title
        private final String[] maintitle;
        //array of subtitle
        private final String[] subtitle;
        //array of imagename
        private final Integer[] imagename;
        //activity
        private final Activity context;

        public AdminListAdapter(Activity context,String[] maintitle, String[] subtitle, Integer[] imagename){
            super(context,R.layout.admin_array_list,maintitle);
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
            View rowview = inflater.inflate(R.layout.admin_array_list,null,true);
            TextView header = (TextView) rowview.findViewById(R.id.user_title);
            TextView subheader = (TextView) rowview.findViewById(R.id.user_subtitle);
            ImageView img = (ImageView) rowview.findViewById(R.id.user_image);

            header.setText(maintitle[position]);
            subheader.setText(subtitle[position]);
            img.setImageResource(imagename[position]);
            //return rowview
            return  rowview;
        }
    }

