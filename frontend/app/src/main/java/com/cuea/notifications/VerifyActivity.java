package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class VerifyActivity extends AppCompatActivity {
    //declare some variables
    TextView textView;
    EditText editText;
    String firstname;
    int admissionno;
    String token;
    Button btnverify,btnregeneratetk;
    CountDownTimer regenerateTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        //get the firstname from intent
        Intent intent = getIntent();
        this.firstname = intent.getStringExtra("FirstName");
        this.admissionno = intent.getIntExtra("IdNo",-1);
        String phoneno = intent.getStringExtra("PhoneNo");

        //place it on screen
        textView = (TextView) findViewById(R.id.tvfname);
        textView.setText("Hi "+firstname+"\nPlease Enter the verification code sent to your phone: "+phoneno);
        btnregeneratetk = (Button) findViewById(R.id.btnregenerate);
        btnverify = findViewById(R.id.btnverify);
        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doVerify(view);
            }
        });
        startTimer();

    }

    public void startTimer(){
        btnregeneratetk.setAlpha(0.1f);
        btnregeneratetk.setClickable(false);
        btnregeneratetk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new doRequestToken().execute();
            }
        });
        regenerateTimer = new CountDownTimer(90000,1000) {
            @Override
            public void onTick(long millisecondstofinish) {
                btnregeneratetk.setText("Generate in :"+millisecondstofinish/1000+"s");
            }

            @Override
            public void onFinish() {
                btnregeneratetk.setText("New Token");
                btnregeneratetk.setAlpha(1);
                btnregeneratetk.setClickable(true);
            }
        };
        regenerateTimer.start();
    }

    public void doVerify(View view) {
        editText = (EditText) findViewById(R.id.edverifycode);
        ///do some verification
        //check if blank
        if(editText.length()!=7){
            editText.setError("Invalid token size");
            editText.requestFocus();
        }
        else if(! editText.toString().contains("-")){
            editText.setError("Invalid Token Format");
            editText.requestFocus();
        }
        else {
            token = editText.getText().toString();
            //call task async
            new DoRequestVerify().execute(Integer.toString(admissionno), token,firstname);
        }
    }

    class DoRequestVerify extends AsyncTask<String,Void,String> {
        //create a progress dialog instance
        ProgressDialog progressDialog = new ProgressDialog(VerifyActivity.this);
        JSONObject json = new JSONObject();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //create the dialog
            progressDialog.setMessage("Attempting login");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            //get the url
            int admission = Integer.parseInt(strings[0]);
            String token = strings[1];
            String firstname = strings[2];
            //use request handler
            RequestHandler requestHandler = new RequestHandler();
            //put data to json
            try{
                json.put("IdNo", admission);
                json.put("Token", token);
                return requestHandler.PostRequest(MyLinks.URL_VERIFY,json,""); //return data to postexecute
            }catch (JSONException e){
                e.printStackTrace();
                return "Error";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //stop the dialog
            progressDialog.dismiss();
            //process the recieved string
            if(s.equals("Error")){
                //Toast about an error
                Toast.makeText(VerifyActivity.this,"Something Went Wrong Check internet or Try later ",Toast.LENGTH_LONG).show();
            }
            else if(s=="unauthorized"){
                editText = (EditText) findViewById(R.id.edverifycode);
                editText.setError("Invalid Token");
                editText.requestFocus();
            }
            else{
                //try to form json object
                try{
                    JSONObject jsonresponse = new JSONObject(s);
                    String lastname=jsonresponse.getString("lastname");
                    String token=jsonresponse.getString("bearer");
                    String usertype=jsonresponse.getString("usertype");
                    Boolean islec=jsonresponse.getBoolean("is_lec");
                    //create a user instance
                    User user = new User(firstname,lastname,usertype,token,islec);
                    //create session
                    SessionManager sessionManager = new SessionManager(VerifyActivity.this);
                    sessionManager.storeSession(user);
                    //start
                    if(usertype.equals("student")){
                        /// redirect to student
                        Intent stintent = new Intent(VerifyActivity.this, StudentHomeActivity.class);
                        startActivity(stintent);
                        finish();

                    }else
                    if(usertype.equals("staff")){
                        Intent intent = new Intent(VerifyActivity.this, StaffHomeActivity.class);
                        startActivity(intent);
                        finish();//finish current
                    }else
                        if(usertype.equals("admin")){
                            Intent intent=new Intent(VerifyActivity.this, AdminHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }

    class doRequestToken extends  AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            JSONObject json = new JSONObject();
            try {
                //put data in json
                json.put("IdNo", admissionno);
                //create request instance
                RequestHandler requestHandler = new RequestHandler();
                //call request
                String response = requestHandler.PostRequest(MyLinks.URL_REGISTER, json, " ");
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
            startTimer();
        }
    }
}