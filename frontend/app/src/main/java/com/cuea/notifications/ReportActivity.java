package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportActivity extends AppCompatActivity {
    final static String DATE_FORMAT = "MM/dd/yyyy";
    ///create some variables
    Button btnstart,btnend,btngenerate;
    EditText edstart,edstop;
    private int mYear,mMonth,mDay;


    //create a date validator and return to long
    public static Long isDateValid(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
            //parse to date
            Date dat = df.parse(date);
            //get time in epoc
            long epoc = dat.getTime()/1000;
            return epoc;
        } catch (ParseException e) {
            return 1234567890L;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        edstart=(EditText) findViewById(R.id.edstarttime);
        edstop=(EditText) findViewById(R.id.edenddate);
    }



    public void chooseEnd(View view) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        //create a date picker dialog with default time as now
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                edstop.setText(1+monthofyear+"/"+dayofmonth+"/"+year);
            }
        },mYear,mMonth,mDay);

        datePickerDialog.show();
    }

    public void chooseStart(View view) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        //create a date picker dialog with default time as now
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                edstart.setText(1+monthofyear+"/"+dayofmonth+"/"+year);
            }
        },mYear,mMonth,mDay);

        datePickerDialog.show();
    }

    public void doGenerate(View view) {
        //before generating check if the dates are present
        if(edstart.getText().length()<1){
            ///alert that the date cant be empty
            edstart.setError("start date cant be empty");
            edstart.requestFocus();
        }else
        if(edstop.getText().length()<1){
            edstop.setError("End date cant be empty");
            edstop.requestFocus();
        }else
        //check if the date is valid
        if(isDateValid(edstart.getText().toString())==1234567890L){
            edstart.setError("Invalid date");
            edstart.requestFocus();
        }else
        if(isDateValid(edstop.getText().toString())==1234567890L){
            edstop.setError("Invalid date");
            edstop.requestFocus();
        }else{
            //TO DO request report
            //assign values
            long startingdate = isDateValid(edstart.getText().toString());
            long endingdate = isDateValid(edstop.getText().toString());
            if(endingdate<startingdate){
                edstop.setError("Ending date cannot be higher than starting date");
                edstop.requestFocus();
            }
            else{
                    //post the data to async task
                    new generateReport().execute(startingdate,endingdate);

            }
            //Toast.makeText(this, isDateValid(edstart.getText().toString()).toString(), Toast.LENGTH_SHORT).show();
        }

    }

    class generateReport extends AsyncTask<Long,Void,String>{
        //create a list variable to hold the values
        List<String> sender = new ArrayList<String>();
        List<String> date = new ArrayList<String>();
        List<String> content = new ArrayList<String>();

        //create progress dialog
        ProgressDialog progressDialog = new ProgressDialog(ReportActivity.this);
        @Override
        protected void onPreExecute() {
            //set processing
            progressDialog.setCancelable(false);
            progressDialog.setMessage("Generating report");
            progressDialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Long... longs) {
            long startdate = longs[0];
            long stopdate= longs[1];

            //request metod
            RequestHandler requestHandler = new RequestHandler();
            //get the token
            SessionManager sessionManager = new SessionManager(ReportActivity.this);
            User user = sessionManager.getUser();
            String token = user.getToken();
            //do request now
            try {
                //create a json object to hold my data
                JSONObject json = new JSONObject();
                json.put("startdate",startdate);
                json.put("enddate",stopdate);
                return requestHandler.PostRequest(MyLinks.STUDENT_URL_REPORT,json,token);
            }catch (JSONException e){
                Toast.makeText(ReportActivity.this, "Something went wrong try again later", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Toast.makeText(ReportActivity.this, s.toString(), Toast.LENGTH_SHORT).show();
            if(s.equals("notfound")){
                Toast.makeText(ReportActivity.this, "No reports for that month", Toast.LENGTH_SHORT).show();
            }else
            if(s.equals("Error")){
                Toast.makeText(ReportActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }else
            //otherwise do the creation of reports
            try{
                //create a new json object
                JSONObject json= new JSONObject(s);
                //get the result
                JSONArray array = json.getJSONArray("result");
                //iterate through the array creating users
                for (int i=0;i<array.length();i++){
                    //get the array object
                    JSONObject object = array.getJSONObject(i);
                    //get the sender
                    sender.add(object.getString("FullNames"));
                    //the content
                    content.add(object.getString("Content"));
                    //get the long date and convert to string
                    Long epoc = object.getLong("Date");
                    //convert epoc to date
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                    String smsdate = simpleDateFormat.format(epoc*1000).toString();
                    //add the date
                    date.add(smsdate);
                }
                //convert the array list to string list
                String[] x = sender.toArray(new String[0]);
                String[] y = content.toArray(new String[0]);
                String[] z = date.toArray(new String[0]);
                //now we have the data feed it to the list view
                Intent intent = new Intent(ReportActivity.this,ReportViewActivity.class);
                intent.putExtra("senders",x);
                intent.putExtra("contents",y);
                intent.putExtra("dates",z);
                startActivity(intent);
            }
            catch (JSONException e){
                Toast.makeText(ReportActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
