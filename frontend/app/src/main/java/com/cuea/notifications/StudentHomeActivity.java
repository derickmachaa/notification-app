package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DiffUtil;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentHomeActivity extends AppCompatActivity {
    //some variables
    EditText edsearch;
    ProgressDialog progressDialog;
    ListView listView;
    //create list to
    ArrayList<HomeView> arrayList = new ArrayList<HomeView>();
    ArrayList<HomeView> arrayList_copy = new ArrayList<HomeView>();


    //some classes
    //get session manager
    SessionManager sessionManager;
    User user;

    HomeViewAdapter homeViewAdapter;

    //notification count
    int notificationcheck=0;

    //searchview
    SearchView searchView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_profile_menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        //initialise
        sessionManager = new SessionManager(this);
        //create a progress dialog
        progressDialog = new ProgressDialog(StudentHomeActivity.this);
        // create the instance of the ListView
        listView = (ListView) findViewById(R.id.st_sms_list);
        //change title
        this.setTitle("CUEA Student");
        //get notifications
        new NotificationsGet().execute();

    }

    @Override
    protected void onResume(){
        new NotificationsGet().execute();
        super.onResume();
    }

    public void setupSearch(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                homeViewAdapter.filter(s.toString());
                return true;
            }
        });
        searchView.setSubmitButtonEnabled(false);
        searchView.setQueryHint("Enter sender/content to match");
    }


    //do display data
    public void doDisplaySms(){
        // Now create the instance of the homeview adapter and pass
        // the context and arrayList created above
        homeViewAdapter = new HomeViewAdapter(StudentHomeActivity.this, arrayList);
        searchView = (SearchView) findViewById(R.id.student_app_bar_search);

        // set the homeviewadapter for ListView
        listView.setAdapter(homeViewAdapter);
        listView.setTextFilterEnabled(true);
        setupSearch();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ImageView x = (ImageView) view.findViewById(R.id.img_homeview_icon);
                //check if message unread
                if(arrayList.get(i).getImageviewid()==R.drawable.smsnew){
                    //set to read

                    //Toast.makeText(StudentActivity.this, "Am here", Toast.LENGTH_SHORT).show();
                    x.setImageResource(R.drawable.smsread);
                }
                //Toast.makeText(StudentActivity.this,notificationID.get(i),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(StudentHomeActivity.this, StudentNotificationOpenActivity.class);
                intent.putExtra("notificationID",arrayList.get(i).getObjectid());
                startActivity(intent);

            }
        });
    }
    //done display data

    //Async task to request sms
    class NotificationsGet extends AsyncTask<Void,Void,String>{

        ///get return type of user
        User user = sessionManager.getUser();
        //get the token to use for requests
        String token = user.getToken();




        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //create dialog if this is the first time we are checking for messages
            if(notificationcheck==0){
                progressDialog.setMessage("Getting your messages");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
            //add plus one
            notificationcheck+=1;

        }

        @Override
        protected String doInBackground(Void... voids) {
            //do checking
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.GetRequest(MyLinks.STUDENT_URL_READ,token);
        }

        @Override
        protected void onPostExecute(String s) {
            arrayList_copy.clear();
            super.onPostExecute(s);
            //stop process dialog
            progressDialog.dismiss();
            ///process the messages;
            //Toast.makeText(StudentActivity.this,s,Toast.LENGTH_LONG).show();
            if(s.equals("Error")){
                Toast.makeText(StudentHomeActivity.this,"Something went Wrong",Toast.LENGTH_LONG).show();
            }else if(s=="unauthorized"||s=="notfound") {
                Toast.makeText(StudentHomeActivity.this, "Not authorized logout", Toast.LENGTH_LONG).show();
            }else{
                //try to decode the json object and render it
                try{
                    JSONObject json = new JSONObject(s);
                    JSONArray array = json.getJSONArray("result");
                    int imgid;
                    //iterate through the json array
                    for(int i=0;i<array.length();i++){
                        JSONObject object = array.getJSONObject(i);
                        //get the name of the sender
                        String maintitle=object.getString("FullNames");
                        //get the description of the notification
                        String subtitle=object.getString("Description");
                        //get the status of the notification
                        try{
                            int status = object.getInt("Status");
                            if(status!=3){
                                imgid=R.drawable.smsnew;
                            }
                            else{
                                imgid=R.drawable.smsread;
                            }}catch (Exception e){
                            imgid=R.drawable.smsnew;
                        }

                        //get the id
                        String objectid = object.getString("Id");
                        arrayList_copy.add(new HomeView(imgid,maintitle,subtitle,objectid));
                    }
                    //check whether to update or not
                    //check if array not of the same size then we need to update as there is a new file
                    if(arrayList_copy.size()!=arrayList.size()){
                        Toast.makeText(StudentHomeActivity.this, "We are here", Toast.LENGTH_SHORT).show();
                        arrayList.clear();
                        arrayList.addAll(arrayList_copy);
                        doDisplaySms();
                    }
                    //check new messages every one second
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            new NotificationsGet().execute();
//                        }
//                    },1000);

                }catch (JSONException e){
                    Toast.makeText(StudentHomeActivity.this,"Something went wrong try again",Toast.LENGTH_LONG);
                }
            }
        }
    }

    //this function will handle the about page
    public  void doAbout(MenuItem mi){
        //redirect to about acvitity
        Intent intent = new Intent(this,AboutActivity.class);
        startActivity(intent);
    }
    //this function will handle the generate report
    public void doReport (MenuItem mi){
        //start the about activity
        Intent intent = new Intent(this,ReportActivity.class);
        startActivity(intent);
    }

    //this function will be used when user wants to logout
    public void doLogout(MenuItem mi){
        sessionManager.logout();
    }

}