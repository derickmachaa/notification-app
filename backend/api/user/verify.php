<?php
//This file will be used during verification of token

///include the neccesary files
include '../../config/config.php';
include ROOT.'lib/mongo/autoload.php';
include ROOT.'api/objects/database.php';
include ROOT.'api/objects/user.php';

//initialize classes
$database=new Database();
$user=new User($database);

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
    //set userNo
    $user->setAdmissionNo($data->AdmissionNo);
    //check if the data provided matches and login
    $localToken=$data->Token;
    $realToken=$user->getToken();
    if($localToken==$realToken){
        //process session keys
        openssl_encrypt("derick","aes-256-gcm","whoami");
        session_start();
        $_SESSION["favcolor"] = "yellow";
        //setcookie('user', $id.';'.md5('derick'), 60*600);
        print_r($_SESSION);
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