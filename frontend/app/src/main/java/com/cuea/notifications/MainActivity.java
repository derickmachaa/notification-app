package com.cuea.notifications;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    //create a notification channel for us
    public static final String NOTIFICATION_CHANNEL="cuea_alert";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Check if the user is logged in or not
        SessionManager sessionManager = new SessionManager(this);
        if(sessionManager.checkLoggedin()){
            //if logged in check usertype
            User user = sessionManager.getUser();
            String usertype= user.getUsertype();
            ///if student go to student menu
            if(usertype.equals("student")){
                Intent intent = new Intent(this, StudentHomeActivity.class);
                startActivity(intent);
                finish(); //finish current activity
            }
            else if(usertype.equals("staff")){
                Intent intent = new Intent(this, StaffHomeActivity.class);
                startActivity(intent);
                finish();
            }else if(usertype.equals("admin")){
                Intent intent = new Intent(this, AdminHomeActivity.class);
                startActivity(intent);
                finish();//
            }
        }else{
            //start sign in activity
            //start singup
            Intent intent = new Intent(this,SignupActivity.class);
            startActivity(intent);
            finish(); //finish mainactivity

        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //we need to create a notification channel for android 8 and above
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL,"hello",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.enableVibration(true);
            notificationChannel.enableLights(true);
            notificationManager.createNotificationChannel(notificationChannel);

        }
    }
}