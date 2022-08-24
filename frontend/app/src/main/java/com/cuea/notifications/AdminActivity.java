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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AdminActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //set different title
        this.setTitle("CUEA Admin");
        //get users
        new getAllUsers().execute();
    }

    //action bar hacks
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_profile_menu,menu);
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

    //function to add users
    public void doAddUser(MenuItem item) {
        Intent intent = new Intent(this,AdminAddUser.class);
        startActivity(intent);
    }


    //end action bar hacks



    //class to get the the user from DB
    class getAllUsers extends AsyncTask<Void,Void,String>{
        //get session manager
        SessionManager sessionManager = new SessionManager(AdminActivity.this);
        //user instance
        User user = sessionManager.getUser();
        //get the token
        String token = user.getToken();
        //create a progress dialog
        ProgressDialog progressDialog = new ProgressDialog(AdminActivity.this);

        //create a list to hold the responses from the server
        List<String> maintitle = new ArrayList<String>();
        List<String> subtitle = new ArrayList<String>();
        List<Integer> userid = new ArrayList<Integer>();
        List<Integer> icons = new ArrayList<Integer>();

        //pre execute

        @Override
        protected void onPreExecute() {
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
                        //create another json obect
                        JSONObject object = userarray.getJSONObject(i);
                        //add the admiission number
                        maintitle.add(object.getString("AdmissionNo"));
                        //get the firstname ad lastname
                        subtitle.add(object.getString("FirstName")+" "+object.getString("LastName"));
                        userid.add(object.getInt("AdmissionNo"));
                        //check if is student or lecturer
                        if(object.getString("UserType").equals("student")){
                            icons.add(R.drawable.ic_student);
                        }else if(object.getString("UserType").equals("lecturer")){
                            icons.add(R.drawable.ic_lecturer);
                        }else if(object.getString("UserType").equals("admin")){
                            icons.add(R.drawable.ic_admin);
                        }else{
                            icons.add(android.R.drawable.ic_delete);
                        }
                    }
                    //now use our listview to show the data
                    //convert arraylist to sting list
                    String [] x = maintitle.toArray(new String[0]);
                    String [] y = subtitle.toArray(new String[0]);
                    Integer [] z = icons.toArray(new Integer[0]);

                    //get the list view
                    ListView listView = (ListView) findViewById(R.id.admin_user_list);
                    //array adapter
                    AdminListAdapter adminListAdapter = new AdminListAdapter(AdminActivity.this,x,y,z);
                    //connect adapter to listview
                    listView.setAdapter(adminListAdapter);
                    //create an onclick  listener for the list

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //Toast.makeText(AdminActivity.this, subtitle.get(i), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(AdminActivity.this,AdminEditUser.class);
                            intent.putExtra("id",maintitle.get(i));
                            startActivity(intent);

                        }
                    });


                }catch(JSONException e){
                    Toast.makeText(AdminActivity.this, "Something went wrong try again later", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    //function to perform logout
    public Void perFormLogout(){

        //build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
        builder.setMessage("Do you really want to logout?");
        //add a positive action
        builder.setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //if the user has selected to logout clear the tokens in db and finish activity
                //create an instance of session manager and clear
                SessionManager sessionManager = new SessionManager(AdminActivity.this);
                sessionManager.logout();
                //say good bye
                Toast.makeText(AdminActivity.this, "Good Bye", Toast.LENGTH_LONG).show();
                //redirect to main activity
                Intent intent = new Intent(AdminActivity.this,MainActivity.class);
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

}