package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class StaffNotificationOpenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_sent_layout);
        setTitle("Sent Notification");
        //get intent
        Intent intent = getIntent();
        String notificationid=intent.getStringExtra("notificationID");
        new ReadNotification().execute(notificationid);
    }

    //set a different menu item
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.staff_notification_open,menu);
        return true;
    }

    public void doReport(MenuItem mi){
        Intent intent = new Intent(this,StaffNotificationReport.class);
        startActivity(intent);
    }

    class ReadNotification extends AsyncTask<String,Void,String> {
        //create a progress dialog
        ProgressDialog progressDialog = new ProgressDialog(StaffNotificationOpenActivity.this);
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
            SessionManager sessionManager = new SessionManager(StaffNotificationOpenActivity.this);
            User user = sessionManager.getUser();
            String token= user.getToken();
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.GetRequest(MyLinks.LEC_URL_READ_ONE+id,token);

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            try{
                JSONObject json=new JSONObject(s);
                JSONObject content = json.getJSONObject("content");
                JSONObject result = content.getJSONObject("result");
                //get the values
                String sms=result.getString("Content");
                JSONObject status = result.getJSONObject("Status");
                //convert epoc to date
                Long epoc = result.getLong("Date");
                //get the recipient count
                int totalsent,totalread,totaldelivered,all;
                totalsent=status.getInt("sent");
                totaldelivered=status.getInt("delivered");
                totalread=status.getInt("read");
                all=totaldelivered+totalread+totalsent;


                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                String date=simpleDateFormat.format(epoc*1000);

                String day = date.split(" ")[0];
                String time = date.split(" ")[1];

                //put sms to display

                TextView txtsms= (TextView)findViewById(R.id.txt_sender_sms);
                TextView txtday = (TextView)findViewById(R.id.txt_sender_date);
                TextView txttime = (TextView)findViewById(R.id.txt_sender_time);
                TextView txtrep =  (TextView) findViewById(R.id.txt_sender_report);

                txtsms.setText(sms);
                txtday.setText(day);
                txttime.setText(time);
                String report="SentTo: "+Integer.toString(all)+"\n"+"OpenedBy: " + Integer.toString(totalread);
                txtrep.setText(report);

            }catch (JSONException e){e.printStackTrace();
                Toast.makeText(StaffNotificationOpenActivity.this,"Something went wrong try later",Toast.LENGTH_LONG).show();
            }
        }

    }

}