package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StudentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        new NotificationsGet().execute();
    }


    //Async task to request sms
    class NotificationsGet extends AsyncTask<Void,Void,String>{
        //get session manager instance
        SessionManager sessionManager = new SessionManager(StudentActivity.this);
        ///get return type of user
        User user = sessionManager.getUser();
        //get the token to use for requests
        String token = user.getToken();
        //create a progress dialog
        ProgressDialog progressDialog = new ProgressDialog(StudentActivity.this);
        //create list to hold the responses
        List<String> maintitle = new ArrayList<String>();
        List<String> subtitle = new ArrayList<String>();
        List<Integer> icons = new ArrayList<Integer>();
        List<String> notificationID = new ArrayList<String>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //create dialog
            progressDialog.setMessage("Getting your messages");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... voids) {
            //do checking
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.GetRequest(MyLinks.URL_READ,token);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //stop process dialog
            progressDialog.dismiss();
            ///process the messages;
            //Toast.makeText(StudentActivity.this,s,Toast.LENGTH_LONG).show();
            if(s.equals("Error")){
                Toast.makeText(StudentActivity.this,"Something went Wrong",Toast.LENGTH_LONG).show();
            }else if(s=="unauthorized"||s=="notfound") {
                Toast.makeText(StudentActivity.this, "Not authorized logout", Toast.LENGTH_LONG).show();
            }else{
                //try to decode the json object and render it
                try{
                    JSONObject json = new JSONObject(s);
                    JSONArray array = json.getJSONArray("result");
                    //iterate through the json array
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        //get the name of the sender
                        maintitle.add(object.getString("FullNames"));
                        //get the description of the notification
                        subtitle.add(object.getString("Description"));
                        //get the status of the id
                        try{
                        int status = object.getInt("Status");
                        if(status!=3){
                                icons.add(R.drawable.smsnew);
                        }
                        else{
                                icons.add(R.drawable.smsread);
                            }}catch (Exception e){
                            icons.add(R.drawable.smsnew);
                        }

                        //update list with the id
                        notificationID.add(object.getString("Id"));
                    }
                    //get the list view
                    ListView listView = (ListView) findViewById(R.id.st_sms_list);

                    //convert array list to string list
                    String[] x=maintitle.toArray(new String[0]); //toarray returns object[] so pass an array as argument
                    String[] y=subtitle.toArray(new String[0]);
                    Integer[] z = icons.toArray(new Integer[0]);
                    //connect to the  adapter now
                    StudentListAdapter adapter = new StudentListAdapter(StudentActivity.this,x,y,z);
                    //create handle on click
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            ImageView x = (ImageView) view.findViewById(R.id.img_sms_status);
                            //check if message unread
                            if(icons.get(i)==R.drawable.smsnew){
                                //set to read
                                x.setImageResource(R.drawable.smsread);
                            }
                            //Toast.makeText(StudentActivity.this,notificationID.get(i),Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(StudentActivity.this,StNotificationOpen.class);
                            intent.putExtra("notificationID",notificationID.get(i));
                            startActivity(intent);
                        }
                    });
                    listView.setAdapter(adapter);


                }catch (JSONException e){
                    Toast.makeText(StudentActivity.this,"Something went wrong try again",Toast.LENGTH_LONG);
                }
            }
        }
    }
}