package com.cuea.notifications;

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
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class StaffReportActivity extends AppCompatActivity {
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

        //
        toggleFilter(View.INVISIBLE);

        //add a on click handler for end value
        chooseend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);

                //create a date picker dialog with default time as now
                DatePickerDialog datePickerDialog = new DatePickerDialog(StaffReportActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(StaffReportActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                        edstart.setText(1 + monthofyear + "/" + dayofmonth + "/" + year);
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
        //end of choose start



        //create listener for filter
        choosefilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set the filter visible
                toggleFilter(View.VISIBLE);
                //create a dialog to select
                edfiltername.setText("Status");
                String status[]=new String[]{"sent","delivered","read"};
                boolean statuschoosen[] = new boolean[status.length];

                //initialise the dialog now
                AlertDialog.Builder builder = new AlertDialog.Builder(StaffReportActivity.this);
                //choose the title
                builder.setTitle("Choose Status");
                //choose an icon
                builder.setIcon(R.drawable.ic_single_grey_tick);
                //create a multichoice
                builder.setMultiChoiceItems(status, statuschoosen, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        //set to checked
                        statuschoosen[i]=b;
                    }
                });
                //set not cancelable
                builder.setCancelable(false);

                //handle the positive button
                builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //now iterate all the elements that are checked and append to filter
                        for(int j=0;j<statuschoosen.length;j++){
                            if(statuschoosen[j]){
                                edfiltervalue.setText(edfiltervalue.getText()+status[j]+",");
                            }
                        }
                    }
                });
                //end of postive

                //start of negative button
                builder.setNegativeButton("Cancel",null);

                //create the dialog and show
                 builder.create();
                 builder.show();
            }
        });

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
        public void toggleFilter(int visibility){
            //hide the filter value
            edfiltervalue.setVisibility(visibility);
            txtfiltervalue.setVisibility(visibility);
            txtfiltername.setVisibility(visibility);
            edfiltername.setVisibility(visibility);

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
                    //check if filter is present
                    if(edfiltername.getVisibility()==View.VISIBLE){
                        //take the values placed and put them in an array
                        String filters[] = edfiltervalue.getText().toString().split(",");
                        JSONArray array = new JSONArray();
                        //now create an array int with the status code
                        //iterate through each
                        for(int i=0;i<filters.length;i++) {

                            //iterate though the filters and add them
                            if (filters[i].equals("sent")) {
                                array.put(1);
                            }
                            if (filters[i].equals("delivered")) {
                                array.put(2);
                            }
                            if (filters[i].equals("read")) {
                                array.put(3);
                            }
                        }
                        json.put("Status",array);
                    }
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
                    File storagename = new File(dir + "/" + Environment.DIRECTORY_DOWNLOADS, "output.csv");
                    //use the download manager to download
                    DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(MyLinks.DOC_ROOT + s);
                    Toast.makeText(StaffReportActivity.this, s, Toast.LENGTH_SHORT).show();
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                    request.setVisibleInDownloadsUi(true);
                    request.setDestinationUri(Uri.fromFile(storagename));
                    long reference=manager.enqueue(request);
                    manager.addCompletedDownload("Report","downloaded",true,"",storagename.toString(),reference,true);
                }
                else{
                    Toast.makeText(StaffReportActivity.this,"No reports available", Toast.LENGTH_LONG).show();
                }
            }
        }
}