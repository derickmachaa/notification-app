package com.cuea.notifications;

public class MyLinks {
    private static final String ROOT_URL = "http://192.168.8.100:8000/api/";
    private static final String NOTIFICATION_ROOT_URL = ROOT_URL + "notification/";
    public static final String URL_REGISTER = ROOT_URL + "user/login.php";
    public static final String URL_VERIFY = ROOT_URL + "user/verify.php";
    //notification links
    public static final String STUDENT_URL_READ = NOTIFICATION_ROOT_URL + "student/read.php";
    public static String STUDENT_URL_READ_ONE = NOTIFICATION_ROOT_URL + "student/read.php?id=";

    public static final String LEC_URL_READ = NOTIFICATION_ROOT_URL + "lecturer/read.php";
    public static String LEC_URL_READ_ONE = NOTIFICATION_ROOT_URL + "lecturer/read.php?id=";

    //admin links
    public static final String ADMIN_ROOT_URL = ROOT_URL + "admin/";
    public static final String  ADMIN_READ_USER = ADMIN_ROOT_URL + "read_user.php";
    public static final String  ADMIN_READ_ONE_USER = ADMIN_ROOT_URL + "read_user.php?id=";
    public static final String  ADMIN_UPDATE_USER = ADMIN_ROOT_URL + "update_user.php";

}
