package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LecActivity extends AppCompatActivity {
    //define some list array to hold my data
    ArrayList<HomeView> arrayList;
    //create progressdialog object
    ProgressDialog progressDialog;
    //create session manager object
    SessionManager sessionManager;
    //create user object
    User user ;
    //the token to use for requests
    String token;
    //request handler
    RequestHandler requestHandler = new RequestHandler();
    //listview
    ListView listView;
    //adapter
    HomeViewAdapter homeViewAdapter;
    //search view
    SearchView searchView;


    public void clearlist(){
        arrayList.clear();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create progress dialog
        progressDialog = new ProgressDialog(LecActivity.this);
        sessionManager = new SessionManager(LecActivity.this);
        //create sessionmanager
        user = sessionManager.getUser();
        //get user token
        token = user.getToken();
        //array list instance
        arrayList = new ArrayList<HomeView>();
        setContentView(R.layout.activity_lec);
        this.setTitle("Lecturer Profile");
        //get notififcations
        new NotificationsGet().execute();

    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this,v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.new_sms_actions, popup.getMenu());
        popup.show();
    }

    //action bar hacks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lec_profile_menu,menu);
        return true;
    }

    public void doReport(MenuItem item) {
        //start the report activity
        Intent intent = new Intent(this,LecReportActivity.class);
        startActivity(intent);
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
        Intent intent = new Intent(this,LecSendActivity.class);
        startActivity(intent);
    }
    public void doAddUser(MenuItem item) {

    }
    //end action bar hacks


    //function to perform logout
    public void perFormLogout(){

        //build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to logout?");
        //add a positive action
        builder.setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //if the user has selected to logout clear the tokens in db and finish activity
                //create an instance of session manager and clear
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
        builder.setNegativeButton(R.string.Cancel,null);
        //show the dialog
        builder.show();

    }

    //function to perform sms deletion
    public void doDeleteSms(String id){

        //build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete Notification?");
        //add a positive action
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //delete the sms
                new NotificationDelete().execute(id);
                clearlist();
                new NotificationsGet().execute();
            }
        });
        //add a negative action
        builder.setNeutralButton(R.string.Cancel,null);
        //show the dialog
        builder.show();

    }
//end of delete


    //function to display sms
    public void doDisplayNotification(){
            //get the list view
            listView = (ListView) findViewById(R.id.lec_sms_list);
            //search view instance
            searchView=(SearchView) findViewById(R.id.lec_searchView);
            homeViewAdapter=new HomeViewAdapter(this,arrayList);


            //create handle on click
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //get the homeview
                    HomeView homeView = arrayList.get(i);
                    Intent intent = new Intent(LecActivity.this,LecNotificationOpen.class);
                    intent.putExtra("notificationID",homeView.getObjectid());
                    startActivity(intent);
                }
            });
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    HomeView homeView = arrayList.get(i);
                    doDeleteSms(homeView.getObjectid());
                    homeViewAdapter.notifyDataSetChanged();
                    return true;
                }
            });
            //connect to the  adapter now
            listView.setAdapter(homeViewAdapter);
            //add search options
            searchView.setQueryHint("Enter Content/Description");
            searchView.setSubmitButtonEnabled(false); //disable submit button
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                homeViewAdapter.filter(s);
                return true;
            }
        });


    }
    //end display


    //refresh list on resume
    @Override
    protected void onResume() {
        super.onResume();
        new NotificationsGet().execute();

    }

    //Async task to request sms
    class NotificationsGet extends AsyncTask<Void,Void,String> {
        @Override
        protected void onPreExecute() {
            //clear the list before fetching new data
            clearlist();
            super.onPreExecute();
            //create dialog
            progressDialog.setMessage("Getting your messages");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(Void... voids) {
            //do checking
            return requestHandler.GetRequest(MyLinks.LEC_URL_READ,token);
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //clear list that holds the data
            clearlist();
            //stop process dialog
            progressDialog.dismiss();
            if(s.equals("Error")){
                Toast.makeText(LecActivity.this,"Something went Wrong",Toast.LENGTH_LONG).show();
            }else if(s=="unauthorized"||s=="notfound") {
                Toast.makeText(LecActivity.this, "Not authorized logout", Toast.LENGTH_LONG).show();
            }else{
                //try to decode the json object and send it to get rendered
                try{
                    JSONObject json = new JSONObject(s);
                    JSONArray array = json.getJSONArray("result");
                    //iterate through the json array
                    for(int i=0;i<array.length();i++){
                        int imgid;
                        JSONObject object = array.getJSONObject(i);
                        //get the description of the notification
                        String maintitle=object.getString("Description");
                        //get content
                        String subtitle = object.getString("Content");
                        try{
                            JSONObject status = object.getJSONObject("Status");
                            //get the status
                            int progress = status.getInt("progress");
                            //get total
                            if(progress==100){
                                imgid=R.drawable.ic_sms_read;
                            }else
                            if(progress>=50 && progress < 100){
                                imgid=R.drawable.ic_sms_delivered;
                            }
                            else{
                                imgid=R.drawable.ic_sms_sent;
                            }}catch (Exception e){
                            imgid=R.drawable.ic_sms_sent;
                        }//update list with the id
                        String objectid = object.getString("Id");
                        arrayList.add(new HomeView(imgid,maintitle,subtitle,objectid));
                    }
                    //now display
                    doDisplayNotification();
                }catch (JSONException e){
                    Toast.makeText(LecActivity.this,"Something went wrong try again",Toast.LENGTH_LONG);
                }

            }
        }
    }

    //class to delete the notification
    class NotificationDelete extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String notificationid=strings[0];
            return requestHandler.GetRequest(MyLinks.LEC_URL_DELETE+notificationid,token);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject json= new JSONObject(s);
                Toast.makeText(LecActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();

            }catch (Exception e){
                Toast.makeText(LecActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
