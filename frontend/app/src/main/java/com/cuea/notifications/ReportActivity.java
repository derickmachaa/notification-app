package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ReportActivity extends AppCompatActivity {
    final static String DATE_FORMAT = "dd/MM/yyyy";
    ///create some variables
    Button btnstart,btnend,btngenerate;
    EditText edstart,edstop,edfile;
    private int mYear,mMonth,mDay;


    //create a date validator and return to long
    public static Long isDateValid(String date)
    {
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
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
        edfile=(EditText) findViewById(R.id.edfilename);
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
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofyear) {
                edstop.setText(dayofyear+"/"+monthofyear+"/"+year);
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
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofyear) {
                edstart.setText(dayofyear+"/"+monthofyear+"/"+year);
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
              //check if the filename provided is empty
                if(edfile.length()<3){
                    edfile.setError("File name cannot be empty");
                }
                else{
                    //post the data to async task
                    new generateReport().execute(startingdate,endingdate);
                }

            }
            //Toast.makeText(this, isDateValid(edstart.getText().toString()).toString(), Toast.LENGTH_SHORT).show();
        }

    }

    class generateReport extends AsyncTask<Long,Void,String>{
        //set progress dialog
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
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }

    }
}
