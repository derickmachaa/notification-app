<?php
//this file will be used for sending sms

//include the necessary files
include '../../config/config.php';
include_once ROOT.'lib/mongo/autoload.php';
include_once ROOT.'api/objects/auth.php';
include_once ROOT.'api/objects/database.php';
include_once ROOT.'api/objects/user.php';
include_once ROOT.'api/objects/notification.php';

//instantiate the class objects
$database=new Database();
$user=new User($database);
$auth= new Auth();
$notifications = new Notification($database,$user);

//set the appropriate headers
header("Access-Control-Allow-Origin: ".URL);
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

//check if user is logged in
if(isset($_SERVER['HTTP_AUTHORIZATION'])){
    //check the header of the file
    $sessionheader=$_SERVER['HTTP_AUTHORIZATION'];
    //get session key
    $session=explode(" ",$sessionheader)[1];
    //decode the key
    $decoded=json_decode($auth->Decode($session),true);
    if($decoded){
        $role=$decoded['UserType'];
        $sender=$decoded['AdmissionNo'];
        if($role=='lecturer'){
            //get message from 
            $data=json_decode(file_get_contents("php://input"));
            if($data){
                $message=$data->message;
                $recipients=$data->recipients;
                $description=$data->description;
                $result=$notifications->sendNotification($message,$recipients,$sender,$description);
                if($result){
                    http_response_code(200);
                    echo json_encode(array("message"=>"message successfully created"));
                }
                else{
                    http_response_code(500);
                    echo json_encode(array("message"=>"something went wrong"));
                }
                
            }
            else{
                http_response_code(400);
                echo json_encode(array("message"=>"body cannot be blank"));
            }
        }
        else{
            http_response_code(403);
            echo json_encode(array("message"=>"not allowed to send"));
        }
    }
    else{
        http_response_code(400);
        echo json_encode(array("message"=>"invalid bearer token"));
    }

}
else{
    //return an error
    http_response_code(400);
    echo json_encode(array("message"=>"Authorization required"));
}
?>