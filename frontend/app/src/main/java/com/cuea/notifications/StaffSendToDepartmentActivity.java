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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class StaffSendToDepartmentActivity extends AppCompatActivity {
    //some variables
    String departments[];
    boolean departmentschecked[];
    Button btnsend;
    EditText destination;
    EditText description;
    EditText editxtsms;
    boolean  sendtomany;
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
        this.setTitle("New Notification");
        btnsend=(Button) findViewById(R.id.btnsend);
        description=(EditText) findViewById(R.id.edtxtdescription);
        destination=(EditText) findViewById(R.id.edtxtadmission);
        destination.setHint("Department name");
        editxtsms=(EditText) findViewById(R.id.editxtsms);
        progressDialog = new ProgressDialog(StaffSendToDepartmentActivity.this);

        //get the student list
        new doGetDepartments().execute();

        ////add an onclic listener for sending text
        btnsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               sendSMS();
            }
        });
    }

    //change the default action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lec_send_menu,menu);
        return true;
    }

    public void sendSMS(){
        MyValidation verification = new MyValidation();
        //split using , for sendtomany option
        boolean isdestinationvalid=true;
        String[] receivers=destination.getText().toString().split(",");
//        for(int i=0;i<receivers.length;i++){
//            //check if each digit is valid
//            if(!verification.isAdmissionNoValid(receivers[i])){
//                isdestinationvalid=false;
//                break;
//            }
//        }
        String allreceivers=destination.getText().toString();
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
            new doSendNotification().execute(sms,desc,allreceivers);
//        }
    }


    //function to add users to sending list
    public void selectDestination(MenuItem mt){

        // initially set the null for the text preview
        destination.setText(null);
        // initialise the alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(StaffSendToDepartmentActivity.this);
        // set the title for the alert dialog
        builder.setTitle("Choose Department");
      //  Toast.makeText(this, Integer.toString(students.length), Toast.LENGTH_SHORT).show();
        // set the icon for the alert dialog
        builder.setIcon(R.drawable.cuea);
        // now this is the function which sets the alert dialog for multiple item selection ready
        builder.setMultiChoiceItems(departments, departmentschecked, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i, boolean isChecked) {
                departmentschecked[i] = isChecked;
                String currentitem= departments[i];
            }
        });

        // alert dialog shouldn't be cancellable
        builder.setCancelable(false);

        // handle the positive button of the dialog
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            // @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < departmentschecked.length; i++) {
                    if (departmentschecked[i]) {
                        //add to view
                            destination.setText(destination.getText() + departments[i] + ",");
                        }
                    }
            }
        });

        // handle the negative button of the alert dialog
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // handle the neutral button of the dialog to clear
        // the selected items boolean checkedItem
        builder.setNeutralButton("CLEAR ALL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < departmentschecked.length; i++) {
                    departmentschecked[i] = false;
                }
            }
        });

        // create the builder
        builder.create();

        // create the alert dialog with the
        // alert dialog builder instance
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
                String[] departments= strings[2].split(",");

                //create a json object to hold the message
                try {
                    JSONObject json = new JSONObject();
                    json.put("message",sms);
                    json.put("description",desc);
                    json.put("departmentname",departments[0]);
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
                Toast.makeText(StaffSendToDepartmentActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                //clear the writing
                editxtsms.setText("");
                description.setText("");
                destination.setText("");

            }catch(Exception e)
            {
                Toast.makeText(StaffSendToDepartmentActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //get the students
    class doGetDepartments extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            return requestHandler.GetRequest(MyLinks.LEC_URL_LIST_DEPARTMENT,token);
        }

        @Override
        protected void onPostExecute(String s) {
            Toast.makeText(StaffSendToDepartmentActivity.this, s, Toast.LENGTH_SHORT).show();
            super.onPostExecute(s);
            try{
                JSONObject json = new JSONObject(s);
               // Toast.makeText(LecSendActivity.this, s, Toast.LENGTH_SHORT).show();
                JSONArray array = json.getJSONArray("result");
                //create an array of equal size
              departments = new String[array.length()];
              departmentschecked = new boolean[array.length()];
                for(int i=0;i<array.length();i++){
                    departments[i]=(array.getString(i));
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}