<?php
include '../../config/config.php';
include ROOT.'lib/mongo/autoload.php';
include ROOT.'api/objects/database.php';
include ROOT.'api/objects/user.php';

$database=new Database();
$user=new User($database);
$user->setUserProfile(15);
$value=$user->getUserByAdmission(15);
print_r($value);
echo $user->getFirstName();
?>