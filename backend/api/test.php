<?php
$data=json_decode(file_get_contents("php://input"));
$headers=$_SERVER;
if(isset($headers)){
	echo json_encode(array("username"=>$headers));
}
//echo json_encode(array("username"=>1031890,"password"=>$x->login));
?>
