<?php
//file to be used by students in reading the notifications

//include necessary file
include_once "../../../config/config.php";
include_once ROOT."api/objects/auth.php";
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


//check if auhtorization field is present
if(isset($_SERVER['HTTP_AUTHORIZATION'])){
    //proceed for check authorization
    $session_field = $_SERVER['HTTP_AUTHORIZATION'];
    //get the bearer
    $bearer = explode(" ",$session_field)[1];
    //try to decode it
    $predata=$auth->Decode($bearer);
    //decode json associative array
    $data=json_decode($predata,true);

    if($data){
        //assign values
        $role=$data['UserType'];
        $admissionNo=$data['AdmissionNo'];
        //check if truly is a student
        if($role=="student"){
            //then check whether to read one or many
            if(isset($_REQUEST['id'])){
                //get one record
                $sms=$notification->studentGetNotificationById($_REQUEST['id'],$admissionNo);
                //responde
                if($sms){
                    http_response_code(200);
                    echo json_encode($sms);
                    }else{
                        http_response_code(204);
                        echo json_encode(array("message"=>"Not found"));
                    }
            }else
            {
                //respond
            http_response_code(200);
            echo json_encode($notification->studentGetNotification($admissionNo));
            }
        }
        else{
            http_response_code(403);
            echo json_encode(array("message"=>"Not Allowed"));
        }

    }else{
        //say invalid token provided
        http_response_code(403);
        $message="Invalid token";
        echo json_encode(array("message"=>$message));
    }
}
else{
    //warn not authorized
    $message="Authorization Required";
    //set response code and send response 
    http_response_code(400);
    echo json_encode(array("message"=>$message));
}


?>