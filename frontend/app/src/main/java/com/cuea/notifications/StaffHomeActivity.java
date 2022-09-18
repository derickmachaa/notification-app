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
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class StaffHomeActivity extends AppCompatActivity {
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
    //Boolean
    Boolean is_lec,isAllFabsVisible;
    //floating buttons
    FloatingActionButton fab_newsms,fab_smsto_one,fab_smsto_dep,fab_smsto_faculty;
    TextView txt_fab_smsto_one, txt_fab_smsto_dep, txt_fab_smsmto_faculty;



    public void clearlist(){
        arrayList.clear();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set content
        setContentView(R.layout.activity_staff_home);

        //create progress dialog
        progressDialog = new ProgressDialog(StaffHomeActivity.this);
        sessionManager = new SessionManager(StaffHomeActivity.this);
        //create sessionmanager
        user = sessionManager.getUser();
        //get user token
        token = user.getToken();
        //array list instance
        arrayList = new ArrayList<HomeView>();
        //get the boolean if islec
        is_lec=user.getIs_lec();

        //change the title accordingly
        if(is_lec){
            setTitle("Lecturer Profile");
        }else{
            setTitle("Staff Profile");
        }
        //get notififcations
        new NotificationsGet().execute();


        //floating buttons settings
        fab_newsms = (FloatingActionButton) findViewById(R.id.staff_fab);
        fab_smsto_one = (FloatingActionButton) findViewById(R.id.fab_staff_btn_one);
        fab_smsto_dep = (FloatingActionButton) findViewById(R.id.fab_staff_btndep);
        fab_smsto_faculty = (FloatingActionButton) findViewById(R.id.fab_staff_btnfaculty);

        txt_fab_smsto_one = (TextView) findViewById(R.id.txt_staff_fab_sendtoone);
        txt_fab_smsto_dep = (TextView) findViewById(R.id.txt_staff_fab_sentodep);
        txt_fab_smsmto_faculty = (TextView) findViewById(R.id.txt_staff_fab_sendtofaculty);

        //hide the floating buttons until we click it
        fab_smsto_one.setVisibility(View.GONE);
        fab_smsto_dep.setVisibility(View.GONE);
        fab_smsto_faculty.setVisibility(View.GONE);
        txt_fab_smsto_one.setVisibility(View.GONE);
        txt_fab_smsto_dep.setVisibility(View.GONE);
        txt_fab_smsmto_faculty.setVisibility(View.GONE);
        isAllFabsVisible=false;

        //floating bar onlick
        fab_newsms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAllFabsVisible){
                    //show when clicked
                    fab_smsto_one.show();
                    fab_smsto_dep.show();
                    fab_smsto_faculty.show();
                    //change bckground icon to cancel
                    fab_newsms.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                    //show text view
                    txt_fab_smsto_one.setVisibility(View.VISIBLE);
                    txt_fab_smsto_dep.setVisibility(View.VISIBLE);
                    txt_fab_smsmto_faculty.setVisibility(View.VISIBLE);
                    //toggle they are visible
                    isAllFabsVisible=true;
                }
                else{
                    //hide because clicked
                    fab_smsto_one.hide();
                    fab_smsto_dep.hide();
                    fab_smsto_faculty.hide();
                    //return default image
                    fab_newsms.setImageResource(android.R.drawable.ic_dialog_email);
                    //

                    txt_fab_smsto_one.setVisibility(View.GONE);
                    txt_fab_smsto_dep.setVisibility(View.GONE);
                    txt_fab_smsmto_faculty.setVisibility(View.GONE);
                    isAllFabsVisible=false;
                }
            }
        });

        //add user onclick for send to one
        fab_smsto_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffHomeActivity.this, StaffSendToOneActivity.class);
                startActivity(intent);
            }
        });

    }


    //action bar hacks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.staff_profile_menu,menu);
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
        sessionManager.logout();
    }

    public void doCreateNotification(MenuItem item) {
        Intent intent = new Intent(this, StaffSendToOneActivity.class);
        startActivity(intent);
    }
    public void doAddUser(MenuItem item) {

    }
    //end action bar hacks

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
            listView = (ListView) findViewById(R.id.staff_sms_list);
            homeViewAdapter=new HomeViewAdapter(this,arrayList);
            searchView=findViewById(R.id.staff_app_bar_search);

            //create handle on click
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    //get the homeview
                    HomeView homeView = arrayList.get(i);
                    Intent intent = new Intent(StaffHomeActivity.this, StaffNotificationOpenActivity.class);
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
                Toast.makeText(StaffHomeActivity.this,"Something went Wrong",Toast.LENGTH_LONG).show();
            }else if(s=="unauthorized"||s=="notfound") {
                Toast.makeText(StaffHomeActivity.this, "Not authorized logout", Toast.LENGTH_LONG).show();
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
                    Toast.makeText(StaffHomeActivity.this,"Something went wrong try again",Toast.LENGTH_LONG);
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
                Toast.makeText(StaffHomeActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();

            }catch (Exception e){
                Toast.makeText(StaffHomeActivity.this, "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
