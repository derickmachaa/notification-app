<?php
//file to read notifications

//include the necessary files
include '../../config/config.php';
include ROOT.'lib/mongo/autoload.php';
include ROOT.'api/objects/database.php';
include ROOT.'api/objects/auth.php';
include ROOT.'api/objects/notification.php';
include ROOT.'api/objects/user.php';

//initialize classes
$database=new Database();
$auth=new Auth();
$user = new User($database);
$notification = new Notification($database,$user);

// required headers
header("Access-Control-Allow-Origin: ".URL);
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

//check if user is  logged in
if(isset($_SERVER['HTTP_AUTHORIZATION'])){
    //now check the token provided by the user
    $sessionheader=$_SERVER['HTTP_AUTHORIZATION'];
    //extract token from header
    $session=explode(" ",$sessionheader)[1];
    //try to decode the token
    
    $decoded=json_decode($auth->Decode($session),true);
    if($decoded){
        $role=$decoded['UserType'];
        //if the user is a student return all messages addressed to him
        //set response to true
        http_response_code(200);
        echo json_encode($notification->getNotifications($decoded['AdmissionNo'],$role));
        
    }
    else{
        http_response_code(400);
        echo json_encode(array("message"=>"invalid bearer token"));
    }

}
else{
    http_response_code(403);
    echo json_encode(array("message"=>"Authorization required"));
}
//$authheader = $_SERVER['Bearer'];
//print_r($_SERVER);


?>