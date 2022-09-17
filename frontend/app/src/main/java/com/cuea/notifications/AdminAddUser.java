package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminAddUser extends AppCompatActivity {

    TextView edadmission ;
    TextView edfirstname;
    TextView edlastname ;
    TextView edusertype ;
    TextView edphone ;
    TextView eddepartment;
    TextView edfaculty;
    Button btnupdate;
    Button btndelete;
    MyVerification myVerification = new MyVerification();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_user);
        edadmission = (TextView) findViewById(R.id.edadmissionno);
        edfirstname = (TextView) findViewById(R.id.edfirstname);
        edlastname = (TextView) findViewById(R.id.edlastname);
        edusertype = (TextView) findViewById(R.id.edusertype);
        edphone = (TextView) findViewById(R.id.edphone);
        eddepartment = (TextView) findViewById(R.id.eddepartment);
        edfaculty = (TextView) findViewById(R.id.edfaculty);
        btnupdate = (Button) findViewById(R.id.btnupdate);
        btndelete = (Button) findViewById(R.id.btndeleteuser);

        //hide the update button
        btnupdate.setVisibility(View.INVISIBLE);
        btndelete.setText("Add User");


        btndelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //do add user

                //check verification
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
                    edfaculty.requestFocus();
                }else

                if(!myVerification.isPhoneNoValid(edphone.getText().toString())){
                    edphone.setError("Invalid Phone");
                    edfaculty.requestFocus();
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
                    new CreateUser().execute(adm,fname,lname,usrty,phone,dep,fac);
                }

            }
        });


    }
    public void doUpdateUser(View view){
        //required
        //btndelete
    }
    public void doDelete(View view){
        //
    }

    ///function to get the user profile
    class CreateUser extends AsyncTask<String,Void,String> {
        ProgressDialog progressDialog = new ProgressDialog(AdminAddUser.this);
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
                json.put("DepartmentName", strings[5]);
                json.put("Faculty", strings[6]);
                if(strings[3].equals("student")){
                    json.put("islec",false);
                }
                SessionManager sessionManager = new SessionManager(AdminAddUser.this);
                User user = sessionManager.getUser();
                String token = user.getToken();
                RequestHandler requestHandler = new RequestHandler();
                return requestHandler.PostRequest(MyLinks.ADMIN_CREATE_USER, json, token);
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
                Toast.makeText(AdminAddUser.this, "User Has Been Created", Toast.LENGTH_SHORT).show();
            }
            else if(s.equals("notmodified")){
                Toast.makeText(AdminAddUser.this, "User Already up to date", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(AdminAddUser.this, "Something Went Wrong Try Again Later", Toast.LENGTH_SHORT).show();
            }
        }
    }
}