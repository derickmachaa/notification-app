<?php
//the connection for the database
include '../../config/config.php';
include '../../api/objects/database.php';

$database = new Database();
//$value=$database->createOne("apps",["username"=>"derick1","password"=>"derick"]);
//$value=$database->createMany("kings",[["username"=>"derick2","password"=>"derick"],["many"=>"mount","whoami"=>"king"]]);
//$value=$database->removeMany("apps",["username"=>"derick1","password"=>"derick"]);
//$value=$database->modifyMany("apps",["username"=>"derick1"],["username"=>"ann","password"=>"derick"]);
//
//{'FacultyName':'Science'},{'Departments':{$elemMatch:{'Name':'Mathematics'}}}


$values=array("FacultyName"=>'Faculty of Science');
$options=array("Department"=>array('$elemMatch'=>array("DepartmentName"=>"Nursing")));
//$options=[];
$value=$database->queryData("faculty",$values,$options);
print_r($value);

echo json_encode($values);
echo json_encode($options)."\n";
echo "{'FacultyName':'Science'},{'Departments':{'elemMatch':{'Name':'Mathematics'}}}\n";
?>
