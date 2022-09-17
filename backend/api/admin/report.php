<?php
//file to generate report students side

//include the necessary files
include '../../config/config.php';
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
        //extract raw data from data from the body
        $postdata=json_decode(file_get_contents("php://input"));
        //decode
        if(isset($postdata->startid) && isset($postdata->stopid)){
            //return specific id
            if($role=='admin'){
		    $users=$user->adminGenerateReport($postdata->startid,$postdata->stopid);
		    if($users){
			    //create my own CSV add the headers
			    $header='_id,FirstName,LastName,is_lec,PhoneNo,DepartmentName,Gender,UserType,Faculty'."\n";
			    $body="";
			    //now add the body
			    foreach($users['result'] as $user){
				    $body.=$user->_id.','.$user->FirstName.','.$user->LastName.','.$user->is_lec.','.$user->PhoneNo.','.$user->DepartmentName.','.$user->Gender.','.$user->UserType.','.$user->Faculty."\n";
                    }
                    $final=$header.$body;
                    $uploaddir="../../downloads/";
                    $filename = $uploaddir.md5($final).".csv";
                    file_put_contents($filename,$final);
                    http_response_code(200);
                    echo $filename;
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
