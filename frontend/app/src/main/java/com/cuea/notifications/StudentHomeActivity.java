package com.cuea.notifications;

import static com.cuea.notifications.MainActivity.NOTIFICATION_CHANNEL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
    int notificationchecksdone =0;

    //searchview
    SearchView searchView;

    //add handler
    Handler handler;
    Runnable runnable;
    int delay=1000;

    //some variables for homeview
    int imgid;
    String maintitle,subtitle,objectid;
    Boolean continuechecking=true;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_profile_menu,menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);
        //add timer for handler
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                new NotificationsGet().execute();
            }
        };
        //initialise
        sessionManager = new SessionManager(this);
        //create a progress dialog
        progressDialog = new ProgressDialog(StudentHomeActivity.this);
        //get notifications
        new NotificationsGet().execute();
        // create the instance of the ListView
        listView = (ListView) findViewById(R.id.st_sms_list);
        //change title
        this.setTitle("CUEA Student");




    }

    public void setupSearch(){
        searchView = (SearchView) findViewById(R.id.student_app_bar_search);
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
        // set the homeviewadapter for ListView
        listView.setAdapter(homeViewAdapter);
        listView.setTextFilterEnabled(true);
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
        setupSearch();
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
            if(notificationchecksdone ==0){
                progressDialog.setMessage("Getting your messages");
                progressDialog.setCancelable(false);
                progressDialog.show();
            }
            //add plus one
            notificationchecksdone +=1;

        }

        @Override
        protected String doInBackground(Void... voids) {
            //do checking
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.GetRequest(MyLinks.STUDENT_URL_READ,token);
        }

        @Override
        protected void onPostExecute(String s) {
            //Toast.makeText(StudentHomeActivity.this, s, Toast.LENGTH_SHORT).show();
            arrayList_copy.clear();
            super.onPostExecute(s);
            //stop process dialog
            progressDialog.dismiss();
            ///process the messages;
            //Toast.makeText(StudentHomeActivity.this,s,Toast.LENGTH_LONG).show();
            if(s.equals("Error") && notificationchecksdone ==1) {
                Toast.makeText(StudentHomeActivity.this, "Something went Wrong Check internet and try later", Toast.LENGTH_LONG).show();
                handler.removeCallbacks(runnable);
            }else {
                //try to decode the values
                String response="empty";
                try {
                    JSONObject json = new JSONObject(s);
                    response = json.getString("message");


                    //check if we were successful
                    if(response.equals("successful")){
                        JSONObject result = json.getJSONObject("content");
                        //parse the result
                        JSONArray array = result.getJSONArray("result");
                        //iterate through the json array and create array list
                        for(int i=0;i<array.length();i++) {
                            JSONObject object = array.getJSONObject(i);
                            //get the name of the sender
                             maintitle = object.getString("FullNames");
                            //get the description of the notification
                             subtitle = object.getString("Description");
                            //get the status of the notification
                            try {
                                int status = object.getInt("Status");
                                if (status != 3) {
                                    imgid = R.drawable.smsnew;
                                } else {
                                    imgid = R.drawable.smsread;
                                }
                            } catch (Exception e) {
                                imgid = R.drawable.smsnew;
                            }
                            //get the id
                             objectid = object.getString("Id");
                            //if first time add directly to list
                            if(notificationchecksdone ==1){
                                arrayList.add(new HomeView(imgid,maintitle,subtitle,objectid));
                            }else {
                                //add to copy for comparison
                                arrayList_copy.add(new HomeView(imgid, maintitle, subtitle, objectid));
                            }
                        }
                        //done iteration

                        //now display
                        //if this is the first run just display
                       if(notificationchecksdone ==1) {
                           doDisplaySms();
                       }
                       else if(arrayList.isEmpty()){
                           if(notificationchecksdone <= 1) {
                               //show progress
                               progressDialog.show();
                               Toast.makeText(StudentHomeActivity.this, "No New Messages", Toast.LENGTH_SHORT).show();
                           }
                           //Toast.makeText(StudentHomeActivity.this, "We", Toast.LENGTH_SHORT).show();
                           arrayList.addAll(arrayList_copy);
                           doDisplaySms();
                       }
                       else{
                            //check whether to update or not
                            //check if array not of the same size then we need to update as there is a new change
                            if (arrayList_copy.size() != arrayList.size() && searchView.isIconified()) {

                                if (arrayList_copy.size() > arrayList.size()) {
                                    //alert the user of a new message
                                    //build our notification give it title
                                    NotificationCompat.Builder notification = new NotificationCompat.Builder(StudentHomeActivity.this, NOTIFICATION_CHANNEL);
                                    notification.setContentTitle("New Notification");
                                    notification.setContentText("Tap to open");
                                    notification.setDefaults(Notification.DEFAULT_ALL);
                                    notification.setSmallIcon(R.drawable.ic_launcher_foreground);
                                    //allow the notification to exit on tap
                                    notification.setAutoCancel(true);
                                    //done
                                    //add action
                                    Intent intent = new Intent(StudentHomeActivity.this, StudentHomeActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(StudentHomeActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
                                    notification.setContentIntent(pendingIntent);
                                    //done
                                    NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(StudentHomeActivity.this);
                                    notificationManagerCompat.notify(1, notification.build());
                                }
                                arrayList.clear();
                                arrayList.addAll(arrayList_copy);
                                doDisplaySms();
                            }
                        }
                    }
                    else{
                        //check what went wrong
                        switch (response) {
                            case "Invalid Token":
                                Toast.makeText(StudentHomeActivity.this, "Not authorized please login", Toast.LENGTH_LONG).show();
                                sessionManager.logout(false);
                                continuechecking=false;
                            case "Not Allowed"://no break
                                Toast.makeText(StudentHomeActivity.this, "Not authorized please login", Toast.LENGTH_LONG).show();
                                sessionManager.logout(false);
                                continuechecking=false;
                                break;
                            case "Not found":
                                if (notificationchecksdone < 1) {
                                    Toast.makeText(StudentHomeActivity.this, "No New messages", Toast.LENGTH_LONG).show();
                                }
                                break;
                            default:
                                if (notificationchecksdone < 1) {
                                    Toast.makeText(StudentHomeActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                }
                        }

                        }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //run every delay
            if(continuechecking){
            handler.postDelayed(runnable,delay);
            }
            else{
                handler.removeCallbacks(runnable);
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
        sessionManager.logout(true);
        //stop the checking
        continuechecking=false;
    }

}