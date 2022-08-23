package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LecActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lec);
        this.setTitle("CUEA LECTURER");
        //get notififcations
        new NotificationsGet().execute();
    }

    //action bar hacks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lec_profile_menu,menu);
        return true;
    }

    public void doReport(MenuItem item) {
    }

    public void doAbout(MenuItem item) {
        //start about activity
        //redirect to about acvitity
        Intent intent = new Intent(this,AboutActivity.class);
        startActivity(intent);
    }

    public void doLogout(MenuItem item) {
        perFormLogout();
    }
    public void doCreateNotification(MenuItem item) {

    }
    public void doAddUser(MenuItem item) {

    }
    //end action bar hacks


    //function to perform logout
    public Void perFormLogout(){

        //build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to logout?");
        //add a positive action
        builder.setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //if the user has selected to logout clear the tokens in db and finish activity
                //create an instance of session manager and clear
                SessionManager sessionManager = new SessionManager(LecActivity.this);
                sessionManager.logout();
                //say good bye
                Toast.makeText(LecActivity.this, "Good Bye", Toast.LENGTH_LONG).show();
                //redirect to main activity
                Intent intent = new Intent(LecActivity.this,MainActivity.class);
                startActivity(intent);
                //finish current activity
                finish();//
            }
        });

        //add a negative action
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        //show the dialog
        builder.show();

        return null;
    }





    //Async task to request sms
    class NotificationsGet extends AsyncTask<Void,Void,String> {
        //get session manager instance
        SessionManager sessionManager = new SessionManager(LecActivity.this);
        ///get return type of user
        User user = sessionManager.getUser();
        //get the token to use for requests
        String token = user.getToken();
        //create a progress dialog
        ProgressDialog progressDialog = new ProgressDialog(LecActivity.this);
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
            return requestHandler.GetRequest(MyLinks.LEC_URL_READ,token);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //stop process dialog
            progressDialog.dismiss();
            ///process the messages;
            //Toast.makeText(LecActivity.this,s,Toast.LENGTH_LONG).show();
            if(s.equals("Error")){
                Toast.makeText(LecActivity.this,"Something went Wrong",Toast.LENGTH_LONG).show();
            }else if(s=="unauthorized"||s=="notfound") {
                Toast.makeText(LecActivity.this, "Not authorized logout", Toast.LENGTH_LONG).show();
            }else{
                //try to decode the json object and render it
                try{
                    JSONObject json = new JSONObject(s);
                    JSONArray array = json.getJSONArray("result");
                    //iterate through the json array
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        //get the name of the sender
                        maintitle.add(object.getString("Description"));
                        //get the description of the notification
                        subtitle.add(object.getString("Content"));
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
                    ListView listView = (ListView) findViewById(R.id.lec_sms_list);

                    //convert array list to string list
                    String[] x=maintitle.toArray(new String[0]); //toarray returns object[] so pass an array as argument
                    String[] y=subtitle.toArray(new String[0]);
                    Integer[] z = icons.toArray(new Integer[0]);
                    //connect to the  adapter now
                    LecListAdapter adapter = new LecListAdapter(LecActivity.this,x,y,z);
                    //create handle on click
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            ImageView x = (ImageView) view.findViewById(R.id.img_sms_status);
                            Toast.makeText(LecActivity.this,notificationID.get(i),Toast.LENGTH_LONG).show();
                        }
                    });
                    listView.setAdapter(adapter);


                }catch (JSONException e){
                    Toast.makeText(LecActivity.this,"Something went wrong try again",Toast.LENGTH_LONG);
                }
            }
        }
    }


}
