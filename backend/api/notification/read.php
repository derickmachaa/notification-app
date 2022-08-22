<?php
//file to read notifications

//include the necessary files
include '../../config/config.php';
include_once ROOT.'lib/mongo/autoload.php';
include_once ROOT.'api/objects/database.php';
include_once ROOT.'api/objects/auth.php';
include_once ROOT.'api/objects/notification.php';
include_once ROOT.'api/objects/user.php';

//initialize classes
$database=new Database();
$auth=new Auth();
$user = new User($database);
$notification = new Notification($database,$user);

// required headers
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: GET");

//check if user is  logged in
if(isset($_SERVER['HTTP_AUTHORIZATION'])){
    //now check the token provided by the user
    $sessionheader=$_SERVER['HTTP_AUTHORIZATION'];
    //extract token from header
    $session=explode(" ",$sessionheader)[1];
    //try to decode the token
    
    $decoded=json_decode($auth->Decode($session),true);
    if($decoded){
        //assignk values
        $role=$decoded['UserType'];
        $admissionNo=$decoded['AdmissionNo'];
        //check if user is requesting specific id
        if(isset($_REQUEST['id'])){
            //return specific id
            $id=$_REQUEST['id'];
            if($role=='student'){
                $sms=$notification->getNotificationRecievedById($id,$admissionNo);
                if($sms){
                http_response_code(200);
                echo json_encode($sms);
                }else{
                    http_response_code(204);
                    echo json_encode(array("message"=>"Not found"));
                }

                echo json_encode($sms);
            }elseif($role=='lecturer'){
                http_response_code(200);
                $sms=$notification->getNotificationSentById($id,$admissionNo);
                echo json_encode($sms);
            }
           
        }
        else{
            //return general messages
            if($role=='lecturer'){
                http_response_code(200);
                echo json_encode($notification->senderGetAll($admissionNo));
            }
            elseif($role='student'){
                http_response_code(200);
                echo json_encode($notification->recieverGetAll($admissionNo));
            }
       
        }        
    }
    else{
        http_response_code(403);
        echo json_encode(array("message"=>"invalid bearer token"));
    }
}
else{
    http_response_code(400);
    echo json_encode(array("message"=>"Authorization required"));
}



?>
