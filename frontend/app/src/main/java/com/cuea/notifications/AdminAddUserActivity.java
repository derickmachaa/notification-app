package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class AdminAddUserActivity extends AppCompatActivity {

    TextView edadmission ;
    TextView edfirstname;
    TextView edlastname ;
    TextView edusertype ;
    TextView edphone ;
    TextView eddepartment;
    TextView edfaculty;
    TextView edgender;
    Button btnupdate;
    Button btndelete;
    SwitchCompat islec;
    Boolean is_lec;
    MyVerification myVerification = new MyVerification();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //change title
        setTitle("Add User");
        setContentView(R.layout.activity_admin_manage_users);
        is_lec=false;
        edadmission = (TextView) findViewById(R.id.edadmissionno);
        edfirstname = (TextView) findViewById(R.id.edfirstname);
        edlastname = (TextView) findViewById(R.id.edlastname);
        edusertype = (TextView) findViewById(R.id.edusertype);
        edphone = (TextView) findViewById(R.id.edphone);
        eddepartment = (TextView) findViewById(R.id.eddepartment);
        edfaculty = (TextView) findViewById(R.id.edfaculty);
        edgender = (TextView) findViewById(R.id.edgender);
        btnupdate = (Button) findViewById(R.id.btnupdate);
        btndelete = (Button) findViewById(R.id.btndeleteuser);
        islec = findViewById(R.id.btnislec);

        islec.setVisibility(View.GONE);
        //hide the update button
        btnupdate.setVisibility(View.INVISIBLE);
        btndelete.setText("Add User");

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
                    String adm,fname,lname,usrty,phone,dep,fac,gender;
                    adm=edadmission.getText().toString();
                    fname=edfirstname.getText().toString();
                    lname=edlastname.getText().toString();
                    usrty=edusertype.getText().toString();
                    phone=edphone.getText().toString();
                    dep=eddepartment.getText().toString();
                    fac=edfaculty.getText().toString();
                    gender=edgender.getText().toString();
                    new CreateUser().execute(adm,fname,lname,usrty,phone,dep,fac,gender);
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
        ProgressDialog progressDialog = new ProgressDialog(AdminAddUserActivity.this);
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
                json.put("IdNo", adm);
                json.put("FirstName", strings[1]);
                json.put("LastName", strings[2]);
                json.put("UserType", strings[3]);
                json.put("PhoneNo", strings[4]);
                json.put("DepartmentName", strings[5]);
                json.put("Faculty", strings[6]);
                json.put("Gender",strings[7]);
                if(islec.getVisibility()==View.VISIBLE){
                    is_lec=islec.isChecked();
                }
                json.put("islec",is_lec);
                SessionManager sessionManager = new SessionManager(AdminAddUserActivity.this);
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
                Toast.makeText(AdminAddUserActivity.this, "User Has Been Created", Toast.LENGTH_SHORT).show();
            }
            else if(s.equals("notmodified")){
                Toast.makeText(AdminAddUserActivity.this, "User Already up to date", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(AdminAddUserActivity.this, "Something Went Wrong Try Again Later", Toast.LENGTH_SHORT).show();
            }
        }
    }
}