<?php
//This file will be used by the admin to manage users in the system especially updating them
//include necessary file
include_once "../../config/config.php";
include_once ROOT."api/objects/auth.php";
include_once ROOT."lib/mongo/autoload.php";
include_once ROOT."api/objects/user.php";
include_once ROOT."api/objects/notification.php";
include_once ROOT."api/objects/database.php";

//set the necessary headers
header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Methods: POST");

//instance
$auth = new Auth();
$database=new Database();
$user=new User($database);
$notification=new Notification($database,$user);


//check if admin is logged in
if(isset($_SERVER['HTTP_AUTHORIZATION'])){
    //check the header of the file
    $sessionheader=$_SERVER['HTTP_AUTHORIZATION'];
    //get session key
    $session=explode(" ",$sessionheader)[1];
    //decode the key
    $decoded=json_decode($auth->Decode($session),true);
    if($decoded){
        $role=$decoded['UserType'];
        if($role=='admin'){
            //get the posted data from 
            $postdata=json_decode(file_get_contents("php://input"));
            //try to get all the data in the form
            $_AdmissionNo=$postdata->AdmissionNo;
            $_FirstName=$postdata->FirstName;
            $_LastName=$postdata->LastName;
            $_UserType=$postdata->UserType;
            $_PhoneNo=$postdata->PhoneNo;
            $_Faculty=$postdata->Faculty;
            $_Department=$postdata->Department;

            //check if all fields required are present
            if(isset($_AdmissionNo)&&isset($_FirstName)&&isset($_LastName)&&isset($_UserType)&&isset($_PhoneNo)&&isset($_Faculty)&&isset($_Department)){
                //set the user
                $user->setAdmissionNo($_AdmissionNo);
                $user->setFirstName($_FirstName);
                $user->setLastName($_LastName);
                $user->setUserType($_UserType);
                $user->setPhoneNo($_PhoneNo);
                $user->setFaculty($_Faculty);
                $user->setDepartment($_Department);

                $result=$user->updateUser();

                if($result){
                    http_response_code(201);
                    echo json_encode(array("message"=>"updated successfully created"));
                }
                else{
                    http_response_code(304);
                    echo json_encode(array("message"=>"duplicate key in body"));
                }
                
            }
            else{
                http_response_code(400);
                echo json_encode(array("message"=>"body cannot be blank"));
            }
        }
        else{
            http_response_code(403);
            echo json_encode(array("message"=>"not allowed to send"));
        }
    }
    else{
        http_response_code(401);
        echo json_encode(array("message"=>"invalid bearer token"));
    }

}
else{
    //return an error
    http_response_code(400);
    echo json_encode(array("message"=>"Authorization required"));
}
?>
