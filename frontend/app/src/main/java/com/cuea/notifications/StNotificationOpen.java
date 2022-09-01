package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class StNotificationOpen extends AppCompatActivity {
    TextView smsholder,senderholder,dayholder,timeholder,titleholder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_received_layout);
        smsholder=(TextView)findViewById(R.id.txt_receiver_message);
        senderholder=(TextView)findViewById(R.id.txt_receiver_sendername);
        dayholder=(TextView)findViewById(R.id.txt_receiver_date);
        timeholder=(TextView)findViewById(R.id.txt_receiver_time);
        titleholder=(TextView)findViewById(R.id.txt_receiver_faculty);

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
            return requestHandler.GetRequest(MyLinks.STUDENT_URL_READ_ONE+id,token);

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
                //get the date
                String date=simpleDateFormat.format(epoc*1000);
                String day  = date.split(" ")[0];
                String time = date.split(" ")[1];
                smsholder.setText(sms);
                senderholder.setText(sender);
                titleholder.setText(faculty+"\n"+"Department of "+ department);
                dayholder.setText(day);
                timeholder.setText(time);

            }catch (JSONException e){e.printStackTrace();
            Toast.makeText(StNotificationOpen.this,"Something went wrong try later",Toast.LENGTH_LONG).show();
            }
        }

    }
}