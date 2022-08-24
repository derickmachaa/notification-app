<?php
//file to generate report students side

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
header("Access-Control-Allow-Methods: POST");

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
        //extract raw data from data from the body
        $postdata=json_decode(file_get_contents("php://input"));
        //decode
        if(isset($postdata->startdate) && isset($postdata->enddate)){
            //return specific id
            if($role=='student'){
                $sms=$notification->StudentGenerateReport($admissionNo,$postdata->startdate,$postdata->enddate);
                if($sms){
                http_response_code(200);
                echo json_encode($sms);
                }else{
                    http_response_code(204);
                    echo json_encode(array("message"=>"Not found"));
                }
            }
            else{
                http_response_code(403);
                echo json_encode(array("message"=>"invalid bearer token"));
            }
           
        }
        else{
            http_response_code(400);
            echo json_encode(array("message"=>"invalid body"));
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
