package com.cuea.notifications;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//this class will be used for verification
public class MyValidation {
    //the date format i will be using
    final static String DATE_FORMAT = "/MM/dd/yyyy";

    //constructor
    public MyValidation() {
    }

    //check if date is valid
    public boolean isDateValid(String date){
        try {
            DateFormat df = new SimpleDateFormat(DATE_FORMAT);
            //try parse to date
            df.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    //check if admissionnumber is valid
    public boolean isAdmissionNoValid(String admissionno) {
        int length = admissionno.length();
        if (length == 0) {
            return false;
        } else {
            //try to parse it
            try {
                //get admission no
                int adm = Integer.parseInt(admissionno);
                return true;
            }
            //if exception number is not valid
            catch (NumberFormatException e) {
                return false;
            }
        }
    }


    //check if firstname is valid
    public boolean isFirstNameValid(String firstname){
        if(firstname.matches("^[A-Za-z]+$")){
            return true;
        }
        else{
            return false;
        }
    }

    //check if lastname is valid
    public boolean isLastNameValid(String lastname){
        if(lastname.matches("^[A-Za-z]+$")){
            return true;
        }
        else{
            return false;
        }
    }

    //check if usertype is valid
    public boolean isUsertypeValid(String usertype){
        if(usertype.equals("student") || usertype.equals("admin")||usertype.equals("staff")){
            return true;
        }
        else{
            return false;
        }
    }


    //check if firstname is valid
    public boolean isPhoneNoValid(String phoneno){
        if (phoneno.matches("^(254|0)+[0-9]{9}$")){
            return true;
        }
        else{
            return false;
        }
    }

    //check if faculty is valid
    public boolean isFacultyValid(String faculty){
        if(faculty.length()>3 && faculty.length()<50){
            return true;
        }else{
            return false;
        }
    }

    //check if department is valid
    public boolean isDepartmentValid(String department){
        if(department.length()>3 && department.length()<50){
            return true;
        }else {
            return false;
        }
    }

    public boolean isDescriptionValid(String description){
        if(description.length()>2 && description.length()<60){
            return true;
        }
        else{
            return false;
        }
    }


    public boolean isSMSValid(String sms){
        if(sms.equals("")){
            return false;
        }
        else{
            return true;
        }
    }

}
