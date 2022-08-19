package com.cuea.notifications;
//This is the user class to handle users
public class User {
    private String firstname;
    private String lastname;
    private String token;
    private String usertype;

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    //constructor class
    public User(String firstname, String lastname,String usertype, String token) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.usertype = usertype;
        this.token = token;

    }

    //Get firstname
    public String getFirstname() {
        return firstname;
    }

    //Set firstname
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    //Get lastname
    public String getLastname() {
        return lastname;
    }

    //set lastname
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    //get token
    public String getToken() {
        return token;
    }

    //set token
    public void setToken(String token) {
        this.token = token;
    }
}
