package com.cuea.notifications;

public class MyLinks {
    private static final String ROOT_URL = "http://192.168.8.2:8000/api/";
    public static String DOC_ROOT="http://192.168.8.2:8000/";
    private static final String NOTIFICATION_ROOT_URL = ROOT_URL + "notification/";
    public static final String URL_REGISTER = ROOT_URL + "user/login.php";
    public static final String URL_VERIFY = ROOT_URL + "user/verify.php";

    //notification links
    public static final String STUDENT_URL_READ = NOTIFICATION_ROOT_URL + "student/read.php";
    public static String STUDENT_URL_READ_ONE = NOTIFICATION_ROOT_URL + "student/read.php?id=";
    public static final String LEC_URL_READ = NOTIFICATION_ROOT_URL + "lecturer/read.php";
    public static String LEC_URL_READ_ONE = NOTIFICATION_ROOT_URL + "lecturer/read.php?id=";
    public static final String LEC_URL_SEND = NOTIFICATION_ROOT_URL + "lecturer/send.php";
    public static String LEC_URL_DELETE = NOTIFICATION_ROOT_URL + "lecturer/delete.php?id=";

    //

    //admin links
    public static final String ADMIN_ROOT_URL = ROOT_URL + "admin/";
    public static final String  ADMIN_READ_USER = ADMIN_ROOT_URL + "read_user.php";
    public static final String  ADMIN_READ_ONE_USER = ADMIN_ROOT_URL + "read_user.php?id=";
    public static final String  ADMIN_UPDATE_USER = ADMIN_ROOT_URL + "update_user.php";
    public static final String  ADMIN_DELETE_USER = ADMIN_ROOT_URL + "delete_user.php?id=";
    public static final String  ADMIN_CREATE_USER = ADMIN_ROOT_URL + "create_user.php";
    //

    //report links
    public static final String STUDENT_URL_REPORT = NOTIFICATION_ROOT_URL + "student/report.php";
    public static final String LEC_URL_REPORT = NOTIFICATION_ROOT_URL + "lecturer/report.php";

    //user lists
    public static final String LEC_URL_LIST_STUDENT = NOTIFICATION_ROOT_URL + "lecturer/student_list.php";

}
