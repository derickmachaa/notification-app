<?php
//file to get students list on behalf of the lecturer

//include the necessary files
include '../../../config/config.php';
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
header("Access-Control-Allow-Methods: GET");

//check if user is  logged in
if(isset($_SERVER['HTTP_AUTHORIZATION'])){
    //now check the token provided by the user
    $sessionheader=$_SERVER['HTTP_AUTHORIZATION'];
    //extract token from header
    $session=explode(" ",$sessionheader)[1];
    //try to decode the token
    
    $decoded=json_decode($auth->Decode($session),true);
    if($decoded){
        //assign values
        $role=$decoded['UserType'];
        $admissionNo=$decoded['AdmissionNo'];
        //check if is lecturer
            if($role=="staff"){
                //get his department
                $user->setUserProfile($admissionNo);
                $departmentname=$user->getDepartmentName();
		echo $departmentname;
                //find all students in the database who are in the same department
                $students=$user->getStudentsByDepartmentName($departmentname);
                if($students){
                    $result=array();
                    //create a list of admission numbers only
                    foreach($students as $row){
                        array_push($result,$row->_id);
                    }
                http_response_code(200);
                echo json_encode(array("result"=>$result));
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
    }
else{
    http_response_code(400);
    echo json_encode(array("message"=>"Authorization required"));
}

?>
