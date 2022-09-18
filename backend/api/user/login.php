<?php
//This file will be used during sign in

///include the neccesary files
include '../../config/config.php';
include ROOT.'api/objects/database.php';
include ROOT.'api/objects/user.php';

// required headers
header("Access-Control-Allow-Origin: ".URL);
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Max-Age: 3600");
header("Access-Control-Allow-Headers: Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

//function to send sms to using adb shell "am start -n com.android.shellsms/.MainActivity --es pass sendsms --es dst +254702480601 --es sms 'Hello'
function send_sms($destination,$token){
	$sms="Your CUEA Code:\n".$token."\n Do not Share this Code";
	$cmd="adb -e shell \"am start -n com.android.shellsms/.MainActivity --es dst $destination --es sms '$sms'\" ";
	//execute
	shell_exec($cmd);
	sleep(1);
}

//function to hide the number before sending it to user
function hide_number($number){
	$length=strlen($number);
	//get first  3
	$f=substr($number,0,3);
	//get last 3
	$l=substr($number,$length-3,$length);
	//generate so many *
	$a="";
	for ($i=0;$i<$length-6;$i++){
		$a=$a."*";	
	}
	return $f.$a.$l;
}

//initialize the classes
$database = new Database();
$user = new User($database);

//Get data send from post object
$data = json_decode(file_get_contents("php://input"));

if(isset($data->IdNo)){
    //Check if the admission exists in the database
    $exists=$user->setUserProfile($data->IdNo);
    if($exists){
	 $fname=$user->getFirstName();
	 $phone=$user->getPhoneNo();
        //generate a token for the user 
        $token=rand(100,999).'-'.rand(100,999);
        //insert token to db
        if($user->setToken($token)){
            send_sms($phone,$token);
            http_response_code(200);
            echo json_encode(array("message"=>"proceed to verification","firstname"=>$fname,"phoneno"=>hide_number($phone)));
        }
    }
    else{
        http_response_code(204);
       // echo json_encode(array("message"=>"user does not exist"));
    }
}
else{
    http_response_code(404);
    echo json_encode(array("message"=>"missing _IdNo "));
}
?>
