package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {
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
    //Floating button
    FloatingActionButton fab_add,fab_useradd,fab_facultyadd;
    TextView adduseraction,addfacultyaction;
    // to check whether sub FAB buttons are visible or not.
    Boolean isAllFabsVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //create progress dialog
        progressDialog = new ProgressDialog(this);
        sessionManager = new SessionManager(this);
        //create sessionmanager
        user = sessionManager.getUser();
        //get user token
        token = user.getToken();
        //array list instance
        arrayList = new ArrayList<HomeView>();
        setContentView(R.layout.activity_admin);

        //set different title
        this.setTitle("CUEA Admin");
        //get users
        new getAllUsers().execute();

        fab_add = (FloatingActionButton) findViewById(R.id.admin_fab);
        fab_useradd = (FloatingActionButton) findViewById(R.id.admin_fab_adduser);
        fab_facultyadd = (FloatingActionButton) findViewById(R.id.admin_fab_addfaculty);
        adduseraction = (TextView) findViewById(R.id.admintxtuser);
        addfacultyaction = findViewById(R.id.admintxtfabfaculty);

        //hide the floating buttons until we click it
        fab_facultyadd.setVisibility(View.GONE);
        fab_useradd.setVisibility(View.GONE);
        adduseraction.setVisibility(View.GONE);
        addfacultyaction.setVisibility(View.GONE);
        isAllFabsVisible=false;

        //floating bar onlick
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isAllFabsVisible){
                    //show when clicked
                    fab_facultyadd.show();
                    fab_useradd.show();
                    //change bckground icon to cancel
                    fab_add.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                    addfacultyaction.setVisibility(View.VISIBLE);
                    adduseraction.setVisibility(View.VISIBLE);
                    isAllFabsVisible=true;
                }
                else{
                    //hide because clicked
                    fab_facultyadd.hide();
                    fab_useradd.hide();
                    //return default image
                    fab_add.setImageResource(android.R.drawable.ic_input_add);
                    //
                    addfacultyaction.setVisibility(View.GONE);
                    adduseraction.setVisibility(View.GONE);
                    isAllFabsVisible=false;
                }
            }
        });

        //add user onclick
        fab_useradd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this,AdminAddUser.class);
                startActivity(intent);
            }
        });

    }


    //action bar hacks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_profile_menu,menu);
        return true;
    }

    public void doReport(MenuItem item) {
        Intent intent = new Intent(AdminActivity.this,AdminReportActivity.class);
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
    //end action bar hacks

    //public clear list
    public void clearlist(){
        arrayList.clear();
    }



    //function to display user list
    public void doDisplayUsers(){
        //get the list view
        listView = (ListView) findViewById(R.id.admin_user_list);
        //search view instance
        searchView=(SearchView) findViewById(R.id.admin_searchView);
        homeViewAdapter=new HomeViewAdapter(this,arrayList);


        //create handle on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //get the homeview
                HomeView homeView = arrayList.get(i);
                Intent intent = new Intent(AdminActivity.this,AdminEditUser.class);
                intent.putExtra("id",arrayList.get(i).getObjectid());
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                HomeView homeView = arrayList.get(i);
                doDeleteUser(homeView.getObjectid());
                homeViewAdapter.notifyDataSetChanged();
                return true;
            }
        });
        //connect to the  adapter now
        listView.setAdapter(homeViewAdapter);
        //add search options
        searchView.setQueryHint("Enter Name/Id");
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

    //refresh data on resume
    @Override
    protected void onResume() {
        super.onResume();
        new getAllUsers().execute();
    }

    //function to delete the user
    public void doDeleteUser(String id){
    }

    //class to get the the user from DB
    class getAllUsers extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            //clear the list that holds data
            clearlist();
            super.onPreExecute();
            progressDialog.setMessage("Getting users");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            //get the request handler
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.GetRequest(MyLinks.ADMIN_READ_USER,token);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //close the progress dialog
            progressDialog.dismiss();
            clearlist();
            //Toast.makeText(AdminActivity.this, s, Toast.LENGTH_SHORT).show();
            //get the reponse
            if(s.equals("Error")){
                Toast.makeText(AdminActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
            else if(s.equals("notfound")||s.equals("notauthorized")){
                Toast.makeText(AdminActivity.this, "Not authorized", Toast.LENGTH_SHORT).show();
            }
            else{
                //try to parse the json now
                try{
                    JSONObject json= new JSONObject(s);
                    //get the result array
                    JSONArray userarray = json.getJSONArray("result");
                    ///iterate through the user array adding maintitle and subtitle
                    for(int i=0;i<userarray.length();i++){
                        int imageviewid;
                        String maintitle;
                        String subtitle;
                        String Objectid;

                        //create another json obect
                        JSONObject object = userarray.getJSONObject(i);

                        //add the admission number
                        maintitle = object.getString("IdNo");

                        //get the firstname ad lastname
                        subtitle=object.getString("FirstName")+" "+object.getString("LastName");

                        //get the icon to use
                        Objectid=Integer.toString(object.getInt("IdNo"));

                        //check if is student or lecturer
                        if(object.getString("UserType").equals("student")){
                            imageviewid = R.drawable.ic_student;
                        }else if(object.getString("UserType").equals("staff")){
                            imageviewid = R.drawable.ic_staff;
                        }else{
                            imageviewid=android.R.drawable.ic_delete;
                        }
                        //add the detailes to the homeview arraylist;
                        arrayList.add(new HomeView(imageviewid,maintitle,subtitle,Objectid));

                    }
                    //call the display function
                    doDisplayUsers();


                }catch(JSONException e){
                    Toast.makeText(AdminActivity.this, "Something went wrong try again later", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}