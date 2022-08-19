package com.cuea.notifications;

import android.content.Context;
import android.content.SharedPreferences;

//This class is the session manager to handle user sessions using shared preferences
public class SessionManager {
    private final Context mycontext;
    private User user;
    private static final String mypreference = "sessions";
    private static final String fname="FirstName";
    private static final String lname="LastName";
    private static final String token="Token";
    private static final String usrtyp="UserType";

    //constructor class
    public SessionManager(Context context){
    this.mycontext = context;
    }

    //function to store the user session
    public void storeSession(User user){
        //create preference instance with private mode access
        SharedPreferences sharedPreferences = mycontext.getSharedPreferences(mypreference,mycontext.MODE_PRIVATE);
        //create preference editor
        SharedPreferences.Editor editor = sharedPreferences.edit();
        //put strings
        editor.putString(fname, user.getFirstname());
        editor.putString(lname, user.getLastname());
        editor.putString(usrtyp,user.getUsertype());
        editor.putString(token,user.getToken());
        //finally store them
        editor.commit();

    }
    //function to get the stored session
    public User getUser(){
        //String usertype = "";
        //create preference instance
        SharedPreferences sharedPreferences = mycontext.getSharedPreferences(mypreference,mycontext.MODE_PRIVATE);
        //getsessions
        String firstname = sharedPreferences.getString(fname,null);
        String lastname = sharedPreferences.getString(lname,null);
        String sessionkey = sharedPreferences.getString(token,null);
        String usertype = sharedPreferences.getString(usrtyp,null);

        //return a user object
        return new User(firstname,lastname,usertype,sessionkey);

    }

    //function to check if user is logged in
    public boolean checkLoggedin(){
        SharedPreferences sharedPreferences = mycontext.getSharedPreferences(mypreference,mycontext.MODE_PRIVATE);
        //check if token in storage
        if((sharedPreferences.getString(token,null))!=null){
            return true;
        }else{
            return false;
        }
    }

}
