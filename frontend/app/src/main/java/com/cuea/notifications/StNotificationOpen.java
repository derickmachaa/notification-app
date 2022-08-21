package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class StNotificationOpen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_st_notification_open);

        //get intent
        Intent intent = getIntent();
        String notificationid=intent.getStringExtra("notificationID");
        new ReadNotification().execute(notificationid);
    }

    class ReadNotification extends AsyncTask<String,Void,String>{
        //create a progress dialog
        ProgressDialog progressDialog = new ProgressDialog(StNotificationOpen.this);
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Opening message");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            //get url
            String id= strings[0];
            //get token
            SessionManager sessionManager = new SessionManager(StNotificationOpen.this);
            User user = sessionManager.getUser();
            String token= user.getToken();
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.GetRequest(MyLinks.URL_READ_ONE+id,token);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            try{
                JSONObject json=new JSONObject(s);
                JSONObject result = json.getJSONObject("result");
                //get the values
                String sms=result.getString("Content");
                String sender=result.getString("FullNames");
                String faculty=result.getString("Faculty");
                String department= result.getString("Department");
                Long epoc = result.getLong("Date");

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date=simpleDateFormat.format(epoc*1000);
                //build string
                String fullsms=sms+"\n\n\n\nFaculty:"+faculty+"\nDepartment:"+department+"\nSender:"+sender+"\nDate:"+date;

                TextView txtview= (TextView)findViewById(R.id.st_sms_read);
                txtview.setText(fullsms);
            }catch (JSONException e){e.printStackTrace();
            Toast.makeText(StNotificationOpen.this,"Something went wrong try later",Toast.LENGTH_LONG).show();
            }
        }
    }
}