package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Base64InputStream;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.logging.FileHandler;

public class LecReportActivity extends AppCompatActivity {
    EditText edstart,edstop,edfiltername,edfiltervalue;
    Button chooseend,choosestart,choosefilter,generate;
    TextView txtfiltername,txtfiltervalue;
    int mYear,mMonth,mDay;
    SessionManager sessionManager;
    RequestHandler requestHandler = new RequestHandler();
    User user;
    String token;
    final static String DATE_FORMAT = "MM/dd/yyyy";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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


        ///check if i have storage permissions
        String[] permissionsStorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        int requestExternalStorage = 1;
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "No permissions", Toast.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, permissionsStorage, requestExternalStorage);
        }


        ///

        //myclasses
        sessionManager=new SessionManager(this);
        user=sessionManager.getUser();
        token = user.getToken();

        //set other fields invisible
        hideFilter();

        //enable the other fields
        txtfiltername.setText("FileName");
        edfiltername.setHint("The storage filename");

        //add a on click handler for end value
        chooseend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                //create a date picker dialog with default time as now
                DatePickerDialog datePickerDialog = new DatePickerDialog(LecReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                        edstop.setText(1 + monthofyear + "/" + dayofmonth + "/" + year);
                    }
                }, mYear, mMonth, mDay);

                datePickerDialog.show();
            }
        });
        //End of onclick listener


        //choose a start onclick listener
        choosestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                //create a date picker
                DatePickerDialog datePickerDialog = new DatePickerDialog(LecReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                        edstart.setText(1 + monthofyear + "/" + dayofmonth + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        //end of choose start


        //add on button
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call the generate report method
                new doGenerateReport().execute();
            }
        });
    }

        //method to hide and show filter value
        public void hideFilter(){
            //hide the filter field
            txtfiltervalue.setVisibility(View.INVISIBLE);
            edfiltervalue.setVisibility(View.INVISIBLE);
            choosefilter.setVisibility(View.INVISIBLE);

        }





        class doGenerateReport extends AsyncTask<Void,Void,String>{
            JSONObject json = new JSONObject();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {

                //create json body object
                try{

                    //convert date to epoc
                    DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                    long startdate = df.parse(edstart.getText().toString()).getTime()/1000;
                    long enddate=df.parse(edstop.getText().toString()).getTime()/1000;
                    //place in json
                    json.put("startdate",startdate);
                    json.put("enddate",enddate);
                    //now post
                    return requestHandler.PostRequest(MyLinks.LEC_URL_REPORT,json,token);
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                if(s.contains("csv")) {
                    //get the default external storage dir
                    File dir = Environment.getExternalStorageDirectory();
                    //create a new file object with the default st
                    File storagename = new File(dir + "/" + Environment.DIRECTORY_DOWNLOADS, edfiltername.getText().toString()+".csv");
                    //use the download manager to download
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(MyLinks.DOC_ROOT + s);

                    //downloadmanager to download the files
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationUri(Uri.fromFile(storagename));
                    long reference=manager.enqueue(request);
                    manager.addCompletedDownload("Report","downloaded",true,"application/octet-stream",storagename.toString(),reference,true);
                }
                else{
                    Toast.makeText(LecReportActivity.this,"No reports available", Toast.LENGTH_LONG).show();
                }
            }
        }
}