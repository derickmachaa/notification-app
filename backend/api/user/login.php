<?php
//This file will be used during sign in

///include the neccesary files
include '../../config/config.php';
include ROOT.'lib/mongo/autoload.php';
include ROOT.'api/objects/database.php';
include ROOT.'api/objects/user.php';

// required headers
header("Access-Control-Allow-Origin: ".URL);
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

//function to send sms to
function send_sms($destination,$token){
$sms="Your CUEA code:\n".$token."\n";
echo $sms;
}
//initialize the classes
$database = new Database();
$user = new User($database);

//Get data send from post object
$data = json_decode(file_get_contents("php://input"));

if(isset($data->AdmissionNo)){
    //Check if the admission exists in the database
    $exists=$user->setUserProfile($data->AdmissionNo);
    if($exists){
        //generate a token for the user 
        $token=rand(100,999).'-'.rand(100,999);
        //insert token to db
        if($user->setToken($token)){
            send_sms(1,$token);
            http_response_code(200);
            echo json_encode(array("message"=>"proceed to verification"));
        }
    }
    else{
        http_response_code(400);
        echo json_encode(array("message"=>"user_does_not_exist"));
    }
}
else{
    http_response_code(404);
    echo json_encode(array("message"=>"missing _AdmissionNo "));
}

?>