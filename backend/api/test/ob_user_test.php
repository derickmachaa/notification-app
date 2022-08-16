<?php
include '../../config/config.php';
include ROOT.'lib/mongo/autoload.php';
include ROOT.'api/objects/database.php';
include ROOT.'api/objects/user.php';

$database=new Database();
$user=new User($database);
$user->setAdmissionNo(15);
$user->setUserProfile();
$value=$user->getUserByAdmission();
print_r($value);
echo $user->getFistName();
?>