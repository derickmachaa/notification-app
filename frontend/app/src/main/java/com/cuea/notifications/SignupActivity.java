package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;


public class SignupActivity extends AppCompatActivity {
    EditText edIdNo;
    Button btnsignin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        edIdNo = (EditText) findViewById(R.id.edAdmission);
        btnsignin = (Button) findViewById(R.id.btnsignin);

        btnsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DoSingin(view);
            }
        });
    }

    //This function will handle what action to take when button has been pressed
    public void DoSingin(View view) {
        //select edit text
        //do some validation before posting
            String admissionno = edIdNo.getText().toString();
            MyValidation myverification = new MyValidation();
        if (!myverification.isAdmissionNoValid(admissionno)) {
            edIdNo.setError("invalid admission");
            edIdNo.requestFocus();
        } else {
            int adm = Integer.parseInt(admissionno);
            new DoRequestSignup().execute(adm);
        }

    }
    class DoRequestSignup extends AsyncTask<Integer,Void,String>{
        ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
        //json object to hold our json objects
        JSONObject json = new JSONObject();

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
            int idno = integers[0];
            //create json object
            try {
                //put data in json
                json.put("IdNo", idno);
                //create request instance
                RequestHandler requestHandler = new RequestHandler();
                //call request
                String response = requestHandler.PostRequest(MyLinks.URL_REGISTER,json," ");
                //return response
                return response;
            }catch (Exception e){
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //stop the progress dialog
            progressDialog.dismiss();

            //check if response was successful
            if(s.equals("Error")){
                Toast.makeText(SignupActivity.this, "Something Went Wrong Check internet or try later", Toast.LENGTH_LONG).show();
//
            }
            else{
                try{
                    //decode if is js
                    JSONObject jsonresponse = new JSONObject(s);
                    String success = jsonresponse.getString("message");
                    if(!success.contains("successful")) {
                        edIdNo.setError(success);
                        edIdNo.requestFocus();
                    }
                    else {
                        String firstname = jsonresponse.getString("firstname");
                        String phoneno = jsonresponse.getString("phoneno");
                        //launch the verify activity
                        Intent intent = new Intent(SignupActivity.this, VerifyActivity.class);
                        intent.putExtra("FirstName", firstname);
                        intent.putExtra("PhoneNo", phoneno);
                        intent.putExtra("IdNo", json.getInt("IdNo"));
                        startActivity(intent);
                        finish();//finish current activity
                    }

                } catch (Exception e) {
                    Toast.makeText(SignupActivity.this, "Something Went Wrong", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
