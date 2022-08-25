package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.util.Arrays;
import java.util.List;

public class LecSendActivity extends AppCompatActivity {
    Button btnsend;
    EditText destination;
    EditText description;
    EditText editxtsms;
    boolean sendtomany;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lec_send);
        //set the action bar to new message
        this.setTitle("New Notification");
        btnsend=(Button) findViewById(R.id.btnsend);
        description=(EditText) findViewById(R.id.edtxtdescription);
        destination=(EditText) findViewById(R.id.edtxtadmission);
        editxtsms=(EditText) findViewById(R.id.editxtsms);

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
        MyVerification verification = new MyVerification();
        //split using , for sendtomany option
        boolean isdestinationvalid=true;
        String[] receivers=destination.getText().toString().split(",");
        for(int i=0;i<receivers.length;i++){
            //check if each digit is valid
            if(!verification.isAdmissionNoValid(receivers[i])){
                isdestinationvalid=false;
                break;
            }
        }
        String allreceivers=destination.getText().toString();
        String desc = description.getText().toString();
        String sms = editxtsms.getText().toString();
        if(!isdestinationvalid){
            destination.setError("Invalid admission detected");
            destination.requestFocus();
        }else
        if (!verification.isDescriptionValid(desc)) {
            description.setError("Description should more than two characters and less than 60 characters");
            description.requestFocus();
        } else if (!verification.isSMSValid(sms)) {
            editxtsms.setError("Sms is too short");
            editxtsms.requestFocus();
        } else {
            //call the async class now
            new doSendNotification().execute(sms,desc,allreceivers);
        }
    }



    //function to add users to sending list

    public void selectDestination(MenuItem mt){
        // initialise the list items for the alert dialog
        final String[] listItems = "A long list TODO".split(" ");
        final boolean[] checkedItems = new boolean[listItems.length];
        // list of items
        final List<String> selectedItems = Arrays.asList(listItems);
        // initially set the null for the text preview
        destination.setText(null);

        // initialise the alert dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(LecSendActivity.this);
        // set the title for the alert dialog
        builder.setTitle("Choose Students");

        // set the icon for the alert dialog
        builder.setIcon(R.drawable.cuea);

        // now this is the function which sets the alert dialog for multiple item selection ready
        builder.setMultiChoiceItems(listItems, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
                String currentItem = selectedItems.get(which);
            }
        });

        // alert dialog shouldn't be cancellable
        builder.setCancelable(false);

        // handle the positive button of the dialog
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            // @SuppressLint("SetTextI18n")
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < checkedItems.length; i++) {
                    if (checkedItems[i]) {
                        destination.setText(destination.getText() + selectedItems.get(i) + ",");
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
                for (int i = 0; i < checkedItems.length; i++) {
                    checkedItems[i] = false;
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
        ProgressDialog progressDialog = new ProgressDialog(LecSendActivity.this);
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
                String[] receivers= strings[2].split(",");

                 int receiver[]=new int[receivers.length];
                //create an array of receivers
                    for (int i=0;i<receivers.length;i++){
                        receiver[i]=Integer.parseInt(receivers[i]);
                    }


                //create a json object to hold the message
                try {
                    JSONObject json = new JSONObject();
                    json.put("message",sms);
                    json.put("description",desc);
                    JSONArray array = new JSONArray(receiver);
                    json.put("recipients",array);
                    //now post
                    SessionManager sessionManager = new SessionManager(LecSendActivity.this);
                    User user = sessionManager.getUser();
                    String token = user.getToken();
                    RequestHandler requestHandler = new RequestHandler();
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
                Toast.makeText(LecSendActivity.this, json.getString("message"), Toast.LENGTH_LONG).show();
                //clear the writing
                editxtsms.setText("");
                description.setText("");
                destination.setText("");

            }catch(Exception e)
            {
                Toast.makeText(LecSendActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

}