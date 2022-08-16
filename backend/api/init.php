<?php
//This file is for initializing database and users

use MongoDB\Operation\CreateIndexes;

include '../config/config.php';
require_once ROOT.'/lib/mongo/autoload.php';
require_once ROOT.'/api/objects/database.php';


$database=new Database();
$db=$database->getConnection();
//create collection users
$collection=$db->selectCollection('users');
echo "ok<br>";
//create index sorted in descending and unique
$collection->createIndex(["AdmissionNo"=> -1,"PhoneNo"=>1],["unique"=>true]);
echo "ok<br>";

//add some users
$users=[
    ["AdmissionNo"=>10,"FirstName"=>"Derick","LastName"=>"Macharia","UserType"=>"student","PhoneNo"=>"254711218298"],
    ["AdmissionNo"=>11,"FirstName"=>"Juma","LastName"=>"Mwaure","UserType"=>"student","PhoneNo"=>"254712345670"],
    ["AdmissionNo"=>12,"FirstName"=>"Wema","LastName"=>"James","UserType"=>"student","PhoneNo"=>"254712345671"],
    ["AdmissionNo"=>13,"FirstName"=>"Samuel","LastName"=>"John","UserType"=>"student","PhoneNo"=>"254712345672"],
    ["AdmissionNo"=>14,"FirstName"=>"Zipporah","LastName"=>"Mark","UserType"=>"student","PhoneNo"=>"254712345673"],
    ["AdmissionNo"=>15,"FirstName"=>"Whoami","LastName"=>"Matthew","UserType"=>"student","PhoneNo"=>"254712345674"],
    ["AdmissionNo"=>16,"FirstName"=>"Anne","LastName"=>"Luke","UserType"=>"student","PhoneNo"=>"254712345675"],
    ["AdmissionNo"=>17,"FirstName"=>"Diana","LastName"=>"Peter","UserType"=>"student","PhoneNo"=>"254712345676"],
    ["AdmissionNo"=>18,"FirstName"=>"Denzel","LastName"=>"Michael","UserType"=>"student","PhoneNo"=>"254712345677"],
    ["AdmissionNo"=>19,"FirstName"=>"Daniel","LastName"=>"Mike","UserType"=>"student","PhoneNo"=>"254712345678"],
    ["AdmissionNo"=>20,"FirstName"=>"Edward","LastName"=>"Maina","UserType"=>"lecturer","PhoneNo"=>"254712345679"],
    ["AdmissionNo"=>21,"FirstName"=>"Francis","LastName"=>"Mwangi","UserType"=>"lecturer","PhoneNo"=>"254712345679"],
    ["AdmissionNo"=>22,"FirstName"=>"Vincent","LastName"=>"Kamau","UserType"=>"lecturer","PhoneNo"=>"254712345679"],
    ["AdmissionNo"=>23,"FirstName"=>"James","LastName"=>"Njoroge","UserType"=>"lecturer","PhoneNo"=>"254712345679"],
    ["AdmissionNo"=>24,"FirstName"=>"Mercy","LastName"=>"Wamboi","UserType"=>"lecturer","PhoneNo"=>"254712345679"],
    ["AdmissionNo"=>25,"FirstName"=>"Diana","LastName"=>"Akinyi","UserType"=>"lecturer","PhoneNo"=>"254712345679"]


];
$database->createMany('users',$users);
echo "ok<br>";

//create collection of notifications
$collection=$db->selectCollection('Notifications');
//insert some notifications
$notifications=[
    ["SenderId"=>21,"Content"=>"Please see me at my office","Recipients"=>[10,11,12],"Date"=>"1660569195"],
    ["SenderId"=>22,"Content"=>"You are disqualified","Recipients"=>[14,15,16],"Date"=>"1660568195"],
    ["SenderId"=>23,"Content"=>"You have an F","Recipients"=>[17,18,19],"Date"=>"1660567195"],
    ["SenderId"=>24,"Content"=>"You have an A grade","Recipients"=>[10,11,12,13],"Date"=>"1660566195"],
    ["SenderId"=>25,"Content"=>"Cat is next week","Recipients"=>[10,15,18,13],"Date"=>"1660565195"]

];
$database->createMany("Notifications",$notifications);
echo "ok<br>";

//create a collection of Notification_status
$collection=$db->selectCollection('NotificationStatus');
//get notification id_s from db
$notifications=$database->queryRecord('Notifications',[],["projection"=>['_id'=>1]]);
$status=[
    ["NotificationID"=>$notifications[0]['_id'],"Recipient"=>10,"Status"=>3],
    ["NotificationID"=>$notifications[1]['_id'],"Recipient"=>12,"Status"=>2],
    ["NotificationID"=>$notifications[2]['_id'],"Recipient"=>19,"Status"=>1],
    ["NotificationID"=>$notifications[3]['_id'],"Recipient"=>15,"Status"=>1],
    ["NotificationID"=>$notifications[4]['_id'],"Recipient"=>11,"Status"=>3]

];
$database->createMany("NotificationStatus",$status);
echo "ok<br>";
?>
