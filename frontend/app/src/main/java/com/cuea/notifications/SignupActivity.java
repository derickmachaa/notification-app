package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
    }

    //This function will handle what action to take when button has been pressed
    public void DoSingin(View view) {
        //select edit text
        EditText edAmission = (EditText) findViewById(R.id.edAdmission);
        //do some validation before posting
        //check if empty
        int length = edAmission.length();
        if(length==0){
            edAmission.setError("Admission number cannot be blank");
            edAmission.requestFocus();
        }
        else {
            //check validity of the admission no
            String admissionno = edAmission.getText().toString();
            try {

                //get admission no
                int adm = Integer.parseInt(admissionno);

                //call async task
                new DoRequestSignup().execute(adm);
            }
            //if number is invalid alert
            catch (NumberFormatException e) {
                edAmission.setError("Invalid admission No");
                edAmission.requestFocus();
            }
        }

    }
    class DoRequestSignup extends AsyncTask<Integer,Void,String>{
        ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //create a progress dialog
            progressDialog.setMessage("processing");
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            //get admission no
            int admissionno = integers[0];
            //create hashmap for post request
            HashMap data = new HashMap<String,String>();
            data.put("AdmissionNo",admissionno);

            //create request instance
            RequestHandler requestHandler = new RequestHandler();
            //call request
            String response=requestHandler.PostRequest(MyLinks.URL_REGISTER,data,"");

            //send the result to on post execute
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //stop the progress dialog
            progressDialog.dismiss();

            //check if response was successful
            if(s==""){
                EditText edAmission = (EditText) findViewById(R.id.edAdmission);
                edAmission.setError("The admission number does not exist");
                edAmission.requestFocus();
            }
            else {
                try {
                    //decode if is js
                    JSONObject json = new JSONObject(s);
                    String response = json.getString("firstname");
                    //launch the verify activity
                    Intent intent = new Intent(SignupActivity.this,VerifyActivity.class);
                    intent.putExtra("FirstName",response);
                    startActivity(intent);


                } catch (Exception e) {
                    Toast.makeText(SignupActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}