package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminEditUser extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_user);
        //get the intent
        Intent intent = getIntent();
        Integer adm =  Integer.parseInt(intent.getStringExtra("id"));
        Toast.makeText(this, adm.toString(), Toast.LENGTH_SHORT).show();
        new getUserDetails().execute(adm);

    }

    public void doUpdateUser(View view) {
       //myverification class
        MyVerification myVerification = new MyVerification();
        TextView edadmission = (TextView) findViewById(R.id.edadmissionno);
        TextView edfirstname = (TextView) findViewById(R.id.edfirstname);
        TextView edlastname = (TextView) findViewById(R.id.edlastname);
        TextView edusertype = (TextView) findViewById(R.id.edusertype);
        TextView edphone = (TextView) findViewById(R.id.edphone);
        TextView eddepartment = (TextView) findViewById(R.id.eddepartment);
        TextView edfaculty = (TextView) findViewById(R.id.edfaculty);

        if(!myVerification.isAdmissionNoValid(edadmission.getText().toString())){
            edadmission.setError("Invalid Admission Number");
            edadmission.requestFocus();
        }else
        if(!myVerification.isFirstNameValid(edfirstname.getText().toString())){
                edfirstname.setError("Invalid firstname");
                edfirstname.requestFocus();
        }else
        if(!myVerification.isLastNameValid(edlastname.getText().toString())){
                    edlastname.setError("Invalid LastName");
                    edlastname.requestFocus();
        }else
        if(!myVerification.isUsertypeValid(edusertype.getText().toString())){
            edusertype.setError("Invalid UserType");
            edusertype.requestFocus();
        }else
        if(!myVerification.isDepartmentValid(eddepartment.getText().toString())){
            eddepartment.setError("Invalid Department");
            eddepartment.requestFocus();
        }else
        if(!myVerification.isFacultyValid(edfaculty.getText().toString())){
            edfaculty.setError("Invalid Faculty");
        }else

        if(!myVerification.isPhoneNoValid(edphone.getText().toString())){
            edphone.setError("Invalid Phone");
        }
        else
        {
            String adm,fname,lname,usrty,phone,dep,fac;
            adm=edadmission.getText().toString();
            fname=edfirstname.getText().toString();
            lname=edlastname.getText().toString();
            usrty=edusertype.getText().toString();
            phone=edphone.getText().toString();
            dep=eddepartment.getText().toString();
            fac=edfaculty.getText().toString();
            new updateUserDetails().execute(adm,fname,lname,usrty,phone,dep,fac);
        }

    }
    ///function to get the user profile
    class getUserDetails extends AsyncTask<Integer,Void,String>{
        TextView edadmission = (TextView) findViewById(R.id.edadmissionno);
        TextView edfirstname = (TextView) findViewById(R.id.edfirstname);
        TextView edlastname = (TextView) findViewById(R.id.edlastname);
        TextView edusertype = (TextView) findViewById(R.id.edusertype);
        TextView edphone = (TextView) findViewById(R.id.edphone);
        TextView eddepartment = (TextView) findViewById(R.id.eddepartment);
        TextView edfaculty = (TextView) findViewById(R.id.edfaculty);
        ProgressDialog progressDialog = new ProgressDialog(AdminEditUser.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //create process
            progressDialog.setMessage("Getting details");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            String admissionno = Integer.toString(integers[0]);
            //get session manager
            SessionManager sessionManager = new SessionManager(AdminEditUser.this);
            User user = sessionManager.getUser();
            String token=user.getToken();
            RequestHandler requestHandler= new RequestHandler();
            return requestHandler.GetRequest(MyLinks.ADMIN_READ_ONE_USER+admissionno,token);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            //Toast.makeText(AdminEditUser.this, s, Toast.LENGTH_SHORT).show();
            try{
                JSONObject json= new JSONObject(s);
                Integer admission = json.getInt("AdmissionNo");
                String firstname = json.getString("FirstName");
                String lastname = json.getString("LastName");
                String usertype = json.getString("UserType");
                String phoneno = json.getString("PhoneNo");
                String department = json.getString("Department");
                String faculty = json.getString("Faculty");
                ///fill current screen with required info
                edadmission.setText(admission.toString());
                edfirstname.setText(firstname);
                edlastname.setText(lastname);
                eddepartment.setText(department);
                edusertype.setText(usertype);
                edphone.setText(phoneno);
                edfaculty.setText(faculty);


            }catch (JSONException e){
                e.printStackTrace();
                Toast.makeText(AdminEditUser.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }


    ///function to get the user profile
    class updateUserDetails extends AsyncTask<String,Void,String>{
        ProgressDialog progressDialog = new ProgressDialog(AdminEditUser.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //create process
            progressDialog.setMessage("Getting details");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String fname, lname, usrty, phone, dep, fac;
            int adm;
            adm = Integer.parseInt(strings[0]);
            fname = strings[1];
            try {
                JSONObject json = new JSONObject();
                json.put("AdmissionNo", adm);
                json.put("FirstName", strings[1]);
                json.put("LastName", strings[2]);
                json.put("UserType", strings[3]);
                json.put("PhoneNo", strings[4]);
                json.put("Department", strings[5]);
                json.put("Faculty", strings[6]);
                SessionManager sessionManager = new SessionManager(AdminEditUser.this);
                User user = sessionManager.getUser();
                String token = user.getToken();
                RequestHandler requestHandler = new RequestHandler();
                return requestHandler.PostRequest(MyLinks.ADMIN_UPDATE_USER, json, token);
            } catch (JSONException e) {
                e.printStackTrace();
                return  "Error";
            }
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s.equals("created")){
                Toast.makeText(AdminEditUser.this, "User Has Been updated", Toast.LENGTH_SHORT).show();
            }
            else if(s.equals("notmodified")){
                Toast.makeText(AdminEditUser.this, "User Already up to date", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(AdminEditUser.this, "Something Went Wrong Try Again Later", Toast.LENGTH_SHORT).show();
            }
        }
    }

}