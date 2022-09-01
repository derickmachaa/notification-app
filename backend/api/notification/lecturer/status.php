<?php
//this file will be used by a lecturer for getting the status of a notification

//include the necessary files


include '../../../config/config.php';
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
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");

//check if user is logged in
if(isset($_SERVER['HTTP_AUTHORIZATION'])){
    //check the header of the file
    $bearer=$_SERVER['HTTP_AUTHORIZATION'];
    //get session key
    $token=explode(" ",$bearer)[1];
    $predata=$auth->Decode($token);
    //decode the key to associative array
    $data=json_decode($predata,true);
    if($data){
        $role=$data['UserType'];
        $sender=$data['AdmissionNo'];
        if($role=="lecturer"){
            //get message the message to delete
            if(isset($_REQUEST['id'])){
                $statusid=$_REQUEST['id'];
                $result=$notifications->getNotificationStatus($statusid);
                if($result){
                    //alert true
                    http_response_code(200);
                    echo json_encode($result);
                }
                else
                {
                    echo json_encode($result);
                    http_response_code(204);
                    echo json_encode(array("message"=>"message not found"));
                }

            }else{
                //set an error
                http_response_code(400);
                echo json_encode(array("message"=>"id required"));
            }
        }
        else{
            http_response_code(403);
            echo json_encode(array("message"=>"not allowed to delete"));
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