<?php
//This file will be used during verification of token

///include the neccesary files
include '../../config/config.php';
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
if(isset($data->IdNo) && isset($data->Token)){
    //set user profile
    $user->setUserProfile($data->IdNo);
    $localToken=$data->Token;
    $realToken=$user->getToken();
    //check if the data provided matches and login
    if($localToken==$realToken->Token){
	    //check if the token is expired    
	    if(time()>$realToken->expiry){
		    http_response_code(400);
		    echo json_encode(array("message"=>"Token has expired please regenerate token"));
	    }
	    else{
        //create keys for usage
        $session=array(
            "IdNo"=>$data->IdNo,
	    "UserType"=>$user->getUserType(),
	    "is_lec" =>$user->getIsLec()
        );
        //create bearer
       $encoded=$auth->Encode(json_encode($session));
       //remove token
       $user->removeToken();
       //set response code
       http_response_code(200);
       //give feedback
       echo json_encode(
        array(
            "message"=>"authentication successful",
	    "bearer"=>$encoded,
	    "lastname"=>$user->getLastName(),
	    "usertype"=>$user->getUserType(),
	    "is_lec" => $user->getIsLec()
            )
        );
	    }
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
