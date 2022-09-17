<?php
//This file will be used by the admin to manage users in the system specifically in deleting
//include necessary file
include_once "../../config/config.php";
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
        //check if truly is admin
        if($role=="admin"){
            //then check whether to read one or many
            if(isset($_REQUEST['id'])){
                //get one record
                //cast id to int
                $id=(int) $_REQUEST['id'];
                $result=$user->deleteUserById($id);
                if($result){
                    http_response_code(200);
                    echo json_encode(array("message"=>"user deleted"));
                    }
                    else{
                        //set header to not found
                        http_response_code(404);
                        echo json_encode(array("message"=>"User not found"));
                    }
            }else
            {
                
                    http_response_code(400);
                    echo json_encode(array("message"=>"id is required"));
                }
            }
        else{
            http_response_code(403);
            echo json_encode(array("message"=>"Not Allowed to delete users"));
        }

    }else{
        //say invalid token provided
        http_response_code(401);
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
