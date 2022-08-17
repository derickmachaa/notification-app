<?php
//This file will be used during verification of token

///include the neccesary files
include '../../config/config.php';
include ROOT.'lib/mongo/autoload.php';
include ROOT.'api/objects/database.php';
include ROOT.'api/objects/user.php';
include ROOT.'api/objects/auth.php';

//initialize classes
$database=new Database();
$user=new User($database);
$auth=new Auth();

// required headers
header("Access-Control-Allow-Origin: ".URL);
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

//get data posted
$data=json_decode(file_get_contents("php://input"));
//check if required values are present
if(isset($data->AdmissionNo) && isset($data->Token)){
    //set user profile
    $user->setUserProfile($data->AdmissionNo);
    $localToken=$data->Token;
    $realToken=$user->getToken();
    //check if the data provided matches and login
    if($localToken==$realToken){
        
        //create keys for usage
        $session=array(
            "AdmissionNo"=>$data->AdmissionNo,
            "UserType"=>$user->getUserType()
        );
       $encoded=$auth->Encode(json_encode($session));
       //set response code
       http_response_code(200);
       //give feedback
       echo json_encode(
        array(
            "message"=>"authentication succesful",
            "bearer"=>$encoded
            )
        );
    }
    else{
        http_response_code(403);
        echo json_encode(array("message"=>"Invalid Token"));
    }
}
else{
    http_response_code(404);
    echo json_encode(array("message"=>"missing admission or token"));
}