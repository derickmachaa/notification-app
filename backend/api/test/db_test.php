<?php
//the connection for the database
include '../../config/config.php';
include ROOT.'api/objects/database.php';

$database = new Database();
//$value=$database->createOne("apps",["username"=>"derick1","password"=>"derick"]);
//$value=$database->createMany("kings",[["username"=>"derick2","password"=>"derick"],["many"=>"mount","whoami"=>"king"]]);
//$value=$database->removeMany("apps",["username"=>"derick1","password"=>"derick"]);
//$value=$database->modifyMany("apps",["username"=>"derick1"],["username"=>"ann","password"=>"derick"]);
$value=$database->queryRecord("apps",["username"=>"ann"],[]);
print_r($value[1]);

?>