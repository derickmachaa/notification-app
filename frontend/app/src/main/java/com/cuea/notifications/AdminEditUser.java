package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminEditUser extends AppCompatActivity {

    TextView edadmission ;
    TextView edfirstname;
    TextView edlastname ;
    TextView edusertype ;
    TextView edphone ;
    TextView eddepartment;
    TextView edfaculty;
    TextView edgender;
    SwitchCompat islec;
    Boolean is_lec;
    //myverification class
    MyVerification myVerification = new MyVerification();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_manage_users);
        edadmission = (TextView) findViewById(R.id.edadmissionno);
        edfirstname = (TextView) findViewById(R.id.edfirstname);
        edlastname = (TextView) findViewById(R.id.edlastname);
        edusertype = (TextView) findViewById(R.id.edusertype);
        edphone = (TextView) findViewById(R.id.edphone);
        eddepartment = (TextView) findViewById(R.id.eddepartment);
        edfaculty = (TextView) findViewById(R.id.edfaculty);
        edgender = (TextView) findViewById(R.id.edgender);
        islec = findViewById(R.id.btnislec);
        is_lec=false;

        //add a text listener to produce the is lec toggle
        edusertype.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.toString().equals("staff")){
                    islec.setVisibility(View.VISIBLE);
                }
                else{
                    islec.setChecked(false);
                    islec.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        //get the intent
        Intent intent = getIntent();
        Integer adm =  Integer.parseInt(intent.getStringExtra("id"));
        new getUserDetails().execute(adm);

    }

    public void doUpdateUser(View view) {
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
        }else

        if(!myVerification.isPhoneNoValid(edphone.getText().toString())){
            edphone.setError("Invalid Phone");
        }
        else
        {
            String adm,fname,lname,usrty,phone,dep,fac,gen;
            adm=edadmission.getText().toString();
            fname=edfirstname.getText().toString();
            lname=edlastname.getText().toString();
            usrty=edusertype.getText().toString();
            phone=edphone.getText().toString();
            dep=eddepartment.getText().toString();
            fac=edfaculty.getText().toString();
            gen=edgender.getText().toString();
            new updateUserDetails().execute(adm,fname,lname,usrty,phone,dep,fac,gen);
        }

    }

    //function to be called when delete button is pressed
    public void doDelete(View view) {
        //build a dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to Delete the user?");
        //add a positive action
        builder.setPositiveButton(R.string.Accept, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(!myVerification.isAdmissionNoValid(edadmission.getText().toString())){
                    edadmission.setError("Invalid Admission Number");
                    edadmission.requestFocus();
                }else{
                    //call delete user
                    new DeleteUSer().execute(edadmission.getText().toString());
                }
            }
        });

        //add a negative action
        builder.setNegativeButton(R.string.Cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });
        //show the dialog
        builder.show();

    }

    ///function to get the user profile
    class getUserDetails extends AsyncTask<Integer,Void,String>{
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
                Integer admission = json.getInt("_id");
                String firstname = json.getString("FirstName");
                String lastname = json.getString("LastName");
                String usertype = json.getString("UserType");
                String phoneno = json.getString("PhoneNo");
                String department = json.getString("DepartmentName");
                String faculty = json.getString("Faculty");
                String gender = json.getString("Gender");
                is_lec = json.getBoolean("is_lec");
                ///fill current screen with required info
                edadmission.setText(admission.toString());
                edfirstname.setText(firstname);
                edlastname.setText(lastname);
                eddepartment.setText(department);
                edusertype.setText(usertype);
                edphone.setText(phoneno);
                edfaculty.setText(faculty);
                edgender.setText(gender);
                islec.setChecked(is_lec);


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

    //class to delete user
    class DeleteUSer extends AsyncTask<String,Void,String> {
        ProgressDialog progressDialog = new ProgressDialog(AdminEditUser.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //create process
            progressDialog.setMessage("Deleting user");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String adm;
            //get admission
            adm = strings[0];
            SessionManager sessionManager = new SessionManager(AdminEditUser.this);
            User user = sessionManager.getUser();
            String token = user.getToken();
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.GetRequest(MyLinks.ADMIN_DELETE_USER+adm, token);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Toast.makeText(AdminEditUser.this, s, Toast.LENGTH_SHORT).show();
            if (s.equals("Error")) {
                Toast.makeText(AdminEditUser.this, "User Already deleted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(AdminEditUser.this, "User has been deleted", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
