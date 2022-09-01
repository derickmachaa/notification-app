package com.cuea.notifications;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class ReportCreator{
    private Context context;
    int mYear;
    int mMonth;
    int mDay;
    String ChosenStartdate;


    public ReportCreator(Context context) {
        this.context = context;
    }


    public String chooseEnd(View view) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        //create a date picker dialog with default time as now
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthofyear, int dayofmonth) {
                ChosenStartdate=1+monthofyear+"/"+dayofmonth+"/"+year;
            }
        },mYear,mMonth,mDay);

        datePickerDialog.show();
        return ChosenStartdate;
    }
}
