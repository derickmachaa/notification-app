package com.cuea.notifications;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HomeViewAdapter extends ArrayAdapter<HomeView> {
    private ArrayList<HomeView> resource;
    private ArrayList<HomeView> resource_copy;

    // invoke the suitable constructor of the ArrayAdapter class
    public HomeViewAdapter(@NonNull Context context, ArrayList<HomeView> resource) {
        // pass the context and arrayList for the super
        // constructor of the ArrayAdapter class
        super(context, R.layout.homeview_array_list, resource);
        //set the resource
        this.resource = resource;
        //create a copy of the resource to help in filtering between the two
        this.resource_copy = new ArrayList<HomeView>();
        this.resource_copy.addAll(resource);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // convertView which is recyclable view
        View currentItemView = convertView;
        // of the recyclable view is null then inflate the custom layout for the same
        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.homeview_array_list, parent, false);
        }
        // get the position of the view from the ArrayAdapter
        HomeView currentNumberPosition = getItem(position);

        // then according to the position of the view assign the desired image for the same
        ImageView icon = currentItemView.findViewById(R.id.img_homeview_icon);
        assert currentNumberPosition != null;
        icon.setImageResource(currentNumberPosition.getImageviewid());

        // then according to the position of the view assign the desired maintitle for the same
        TextView maintitle = currentItemView.findViewById(R.id.txt_homeview_title);
        maintitle.setText(currentNumberPosition.getMaintitle());

        // then according to the position of the view assign the desired subtitle for the same
        TextView subtitle = currentItemView.findViewById(R.id.txt_homeview_subtitle);
        subtitle.setText(currentNumberPosition.getSubtitle());

        // then return the recyclable view
        return currentItemView;
    }

    //implement a function to do the filtering for me
    public void filter(CharSequence filtervalue){
        //convert the value to lower case so as to match
        String charsequence =  filtervalue.toString().toLowerCase();
        //clear the current list
        resource.clear();
        if (charsequence.length()==0 || TextUtils.isEmpty(charsequence)){
            //if the filtervalue is empty just add the whole list from the copy
            resource.addAll(resource_copy);
        }
        else{
            //else iterate searching for texts that match and add them to original resource
            for(int i=0;i<resource_copy.size();i++){
                //homeview object
                HomeView homeView = resource_copy.get(i);
                //get the title and subtitle
                String maintitle=homeView.getMaintitle().toLowerCase();
                String subtitle= homeView.getSubtitle().toLowerCase();
                if((maintitle+subtitle).contains(charsequence)){
                    //if they contain text add them
                    resource.add(homeView);
                }
            }

        }
        //refresh the adapter
        notifyDataSetChanged();
    }
}
