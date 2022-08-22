<?php
//This file will be used by the admin to manage users in the system
//include necessary file
include_once "../../../config/config.php";
include_once ROOT."api/objects/auth.php";
include_once ROOT."lib/mongo/autoload.php";
include_once ROOT."api/objects/user.php";
include_once ROOT."api/objects/notification.php";
include_once ROOT."api/objects/database.php";

//set the necessary headers
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");

//instance
$auth = new Auth();
$database=new Database();
$user=new User($database);
$notification=new Notification($database,$user);


?>