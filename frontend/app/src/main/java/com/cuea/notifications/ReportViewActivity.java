package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class ReportViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_view);
        Intent myintent=getIntent();
        String senders[]=myintent.getStringArrayExtra("senders");
        String contents[]=myintent.getStringArrayExtra("contents");
        String dates[]=myintent.getStringArrayExtra("dates");

        Toast.makeText(this, senders[0]+contents[0], Toast.LENGTH_SHORT).show();
        ListView listView = (ListView) findViewById(R.id.rplistview);
        ReportListAdapter reportListAdapter = new ReportListAdapter(this,senders,contents,dates);
        listView.setAdapter(reportListAdapter);
    }
}