package com.cuea.notifications;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StaffSendToSchoolActivity extends AppCompatActivity {
    Button btnsend;
    EditText description;
    EditText editxtsms;
    ProgressDialog progressDialog;

    String token;
    //myobjects
    SessionManager sessionManager;
    User user;
    RequestHandler requestHandler=new RequestHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //create some instances
        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        token = user.getToken();
        setContentView(R.layout.activity_lec_send);
        //set the action bar to new message
        this.setTitle("New Notification To School");
        btnsend=(Button) findViewById(R.id.btnsend);
        description=(EditText) findViewById(R.id.edtxtdescription);
        editxtsms=(EditText) findViewById(R.id.editxtsms);
        //hide some views
        TextView textdst= (TextView) findViewById(R.id.textdestination);
        EditText eddst = (EditText) findViewById(R.id.edtxtadmission);
        textdst.setVisibility(View.INVISIBLE);
        eddst.setVisibility(View.INVISIBLE);

        progressDialog = new ProgressDialog(StaffSendToSchoolActivity.this);


        ////add an onclic listener for sending text
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendSMS();
            }
        });
    }
    public void sendSMS(){
        MyValidation verification = new MyValidation();
        //split using , for sendtomany option
        boolean isdestinationvalid=true;
//        for(int i=0;i<receivers.length;i++){
//            //check if each digit is valid
//            if(!verification.isAdmissionNoValid(receivers[i])){
//                isdestinationvalid=false;
//                break;
//            }
//        }
        String desc = description.getText().toString();
        String sms = editxtsms.getText().toString();
//        if(!isdestinationvalid){
//            destination.setError("Invalid admission detected");
//            destination.requestFocus();
//        }else
//        if (!verification.isDescriptionValid(desc)) {
//            description.setError("Description should more than two characters and less than 60 characters");
//            description.requestFocus();
//        } else if (!verification.isSMSValid(sms)) {
//            editxtsms.setError("Sms is too short");
//            editxtsms.requestFocus();
//        } else {
            //call the async class now
            new doSendNotification().execute(sms,desc);
//        }
    }



    class doSendNotification extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Sending message");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
                String sms=strings[0];
                String desc=strings[1];

                //create a json object to hold the message
                try {
                    JSONObject json = new JSONObject();
                    json.put("message",sms);
                    json.put("description",desc);
                    json.put("toschool",true);
                    //now post
                    return requestHandler.PostRequest(MyLinks.LEC_URL_SEND,json,token);

                }catch (JSONException e){
                    return null;
                }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            try {
                JSONObject json = new JSONObject(s);
                Toast.makeText(StaffSendToSchoolActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                //clear the writing
                if(json.getString("message").contains("successful")) {
                    editxtsms.setText("");
                    description.setText("");
                }

            }catch(Exception e)
            {
                Toast.makeText(StaffSendToSchoolActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


}