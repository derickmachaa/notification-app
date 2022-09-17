package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;

public class AdminReportActivity extends AppCompatActivity {
    EditText edstart,edstop,edfiltername,edfiltervalue;
    Button chooseend,choosestart,choosefilter,generate;
    TextView txtfiltername,txtfiltervalue;
    ProgressDialog progressDialog;
    JSONObject json;
    SessionManager sessionManager;
    RequestHandler requestHandler;
    User user;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = new ProgressDialog(this);
        //change title
        setTitle("Admin User Reports");
        //widgets
        setContentView(R.layout.generate_report_activity);
        edstart = (EditText) findViewById(R.id.edstarttime);
        edstop = (EditText) findViewById(R.id.edenddate);
        edfiltername = (EditText) findViewById(R.id.edfiltername);
        edfiltervalue = (EditText) findViewById(R.id.edfiltervalue);
        txtfiltername = (TextView) findViewById(R.id.txtfiltername);
        txtfiltervalue = (TextView) findViewById(R.id.txtfiltervalue);
        choosestart = (Button) findViewById(R.id.btnstartdate);
        chooseend = (Button) findViewById(R.id.btnenddate);
        choosefilter=(Button)findViewById(R.id.btnfilter);
        generate=(Button)findViewById(R.id.btngenerate);
        //end of widgets

        //sessions
        sessionManager = new SessionManager(this);
        user = sessionManager.getUser();
        token = user.getToken();
        requestHandler = new RequestHandler();

        ///check if i have storage permissions
        String[] permissionsStorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int requestExternalStorage = 1;
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No permissions", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, permissionsStorage, requestExternalStorage);
        }

        //make some changes to widgets
        choosestart.setMinWidth(295);
        choosestart.setText("Start ID");
        edstart.setHint("Enter Start Id");
        chooseend.setWidth(55);
        chooseend.setText("Stop ID");
        edstop.setHint("Enter Stop ID");

        //add generate report button
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new doGenerateUserReport().execute();
            }
        });
    }

    //class for generating the users
    class doGenerateUserReport extends AsyncTask<Void,Void,String>{
        @Override
        protected void onPreExecute() {
            progressDialog.setMessage("Generating Report");
            progressDialog.setCancelable(true);
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                json = new JSONObject();
                json.put("startid",Integer.parseInt(edstart.getText().toString()));
                json.put("stopid",Integer.parseInt(edstop.getText().toString()));
                return requestHandler.PostRequest(MyLinks.ADMIN_URL_REPORT,json,token);
            }catch (JSONException e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s.contains("csv")){
                //download the file now

                //get the download manager service
                DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                //get the download link from response
                Uri uri = Uri.parse(MyLinks.DOC_ROOT+s);
                DownloadManager.Request  request = new DownloadManager.Request(uri);
                //set the description
                request.setDescription("Selected Report is being downloaded");
                request.allowScanningByMediaScanner();
                //add title
                request.setTitle("Requested Report");
                //specifiy the storage dir
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"users.csv");
                //show when done
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadManager.enqueue(request);

            }else {
                Toast.makeText(AdminReportActivity.this, "No Users Found", Toast.LENGTH_SHORT).show();
            }
        }
    }
}