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
    ["AdmissionNo"=>0,"FirstName"=>"Derick","LastName"=>"Kamoro","UserType"=>"admin","PhoneNo"=>"254701873605","Faculty"=>"Law","Department"=>""],
    ["AdmissionNo"=>10,"FirstName"=>"Derick","LastName"=>"Macharia","UserType"=>"student","PhoneNo"=>"254711218298","Faculty"=>"Science","Department"=>"Computer Science"],
    ["AdmissionNo"=>11,"FirstName"=>"Juma","LastName"=>"Mwaure","UserType"=>"student","PhoneNo"=>"254712345670","Faculty"=>"Social Sciences","Department"=>"Literature"],
    ["AdmissionNo"=>12,"FirstName"=>"Wema","LastName"=>"James","UserType"=>"student","PhoneNo"=>"254712345671","Faculty"=>"Agriculture","Department"=>"Food Science"],
    ["AdmissionNo"=>13,"FirstName"=>"Samuel","LastName"=>"John","UserType"=>"student","PhoneNo"=>"254712345672","Faculty"=>"Education","Department"=>"Physical Education"],
    ["AdmissionNo"=>14,"FirstName"=>"Zipporah","LastName"=>"Mark","UserType"=>"student","PhoneNo"=>"254712345673","Faculty"=>"Engineering","Department"=>"Mechanical Engineering"],
    ["AdmissionNo"=>15,"FirstName"=>"Whoami","LastName"=>"Matthew","UserType"=>"student","PhoneNo"=>"254712345674","Faculty"=>"Business","Department"=>"Finance"],
    ["AdmissionNo"=>16,"FirstName"=>"Anne","LastName"=>"Luke","UserType"=>"student","PhoneNo"=>"254712345675","Faculty"=>"Science","Department"=>"Acturial Science"],
    ["AdmissionNo"=>17,"FirstName"=>"Diana","LastName"=>"Peter","UserType"=>"student","PhoneNo"=>"254712345676","Faculty"=>"Science","Department"=>"Computer Science"],
    ["AdmissionNo"=>18,"FirstName"=>"Denzel","LastName"=>"Michael","UserType"=>"student","PhoneNo"=>"254712345677","Faculty"=>"Science","Department"=>"Computer Science"],
    ["AdmissionNo"=>19,"FirstName"=>"Daniel","LastName"=>"Mike","UserType"=>"student","PhoneNo"=>"254712345678","Faculty"=>"Business","Department"=>"Business Administration"],
    ["AdmissionNo"=>20,"FirstName"=>"Edward","LastName"=>"Maina","UserType"=>"lecturer","PhoneNo"=>"254712345679","Faculty"=>"Engineering","Department"=>"Electrical Engineering"],
    ["AdmissionNo"=>21,"FirstName"=>"Francis","LastName"=>"Mwangi","UserType"=>"lecturer","PhoneNo"=>"254712345679","Faculty"=>"Education","Department"=>"Educational Foundation"],
    ["AdmissionNo"=>22,"FirstName"=>"Vincent","LastName"=>"Kamau","UserType"=>"lecturer","PhoneNo"=>"254712345679","Faculty"=>"Agriculture","Department"=>"Plant Science"],
    ["AdmissionNo"=>23,"FirstName"=>"James","LastName"=>"Njoroge","UserType"=>"lecturer","PhoneNo"=>"254712345679","Faculty"=>"Art","Department"=>"Psychology"],
    ["AdmissionNo"=>24,"FirstName"=>"Mercy","LastName"=>"Wamboi","UserType"=>"lecturer","PhoneNo"=>"254712345679","Faculty"=>"Science","Department"=>"Computer Science"],
    ["AdmissionNo"=>25,"FirstName"=>"Diana","LastName"=>"Akinyi","UserType"=>"lecturer","PhoneNo"=>"254712345679","Faculty"=>"LAW","Department"=>""]


];
$database->createMany('users',$users);
echo "ok<br>";

//create collection of notifications
$collection=$db->selectCollection('Notifications');
//insert some notifications
$notifications=[
    ["SenderId"=>20,"Description"=>"Be serious","Content"=>"Please see me at my office","Recipients"=>[10,11,12],"Date"=>1660569195],
    ["SenderId"=>20,"Description"=>"Things are bad","Content"=>"You are expelled","Recipients"=>[13,14,12],"Date"=>1660469195],
    ["SenderId"=>21,"Description"=>"Things are bad","Content"=>"You are disqualified","Recipients"=>[14,15,16],"Date"=>1660368195],
    ["SenderId"=>21,"Description"=>"Cat Marks","Content"=>"Come for your cat papers","Recipients"=>[17,15,16],"Date"=>660268195],
    ["SenderId"=>23,"Description"=>"Exam Results","Content"=>"You have an F","Recipients"=>[17,18,19],"Date"=>1660367195],
    ["SenderId"=>23,"Description"=>"Exam with B","Content"=>"You have an B","Recipients"=>[13,10,11],"Date"=>1660467195],
    ["SenderId"=>23,"Description"=>"Exam with A","Content"=>"You have an A grade","Recipients"=>[10,11,12,13],"Date"=>1660564195],
    ["SenderId"=>24,"Description"=>"Prepare for cat","Content"=>"Cat is next week","Recipients"=>[10,15,18,13],"Date"=>1660565195],
    ["SenderId"=>24,"Description"=>"Exam with A","Content"=>"You have an A grade","Recipients"=>[10,11,12,13],"Date"=>1660567195],
    ["SenderId"=>25,"Description"=>"Prepare for cat","Content"=>"Cat is next week","Recipients"=>[10,15,18,13],"Date"=>1660568195]

];
$database->createMany("Notifications",$notifications);
echo "ok<br>";

//create a collection of Notification_status
$collection=$db->selectCollection('NotificationStatus');
//get notification id_s from db
$notifications=$database->queryRecord('Notifications',[],["projection"=>['_id'=>1]]);
$status=[
    ["NotificationId"=>$notifications[0]['_id'],"Recipient"=>10,"Status"=>1,"Date"=>1660715195],
    ["NotificationId"=>$notifications[1]['_id'],"Recipient"=>12,"Status"=>2,"Date"=>1660726195],
    ["NotificationId"=>$notifications[2]['_id'],"Recipient"=>14,"Status"=>1,"Date"=>1660737195],
    ["NotificationId"=>$notifications[3]['_id'],"Recipient"=>15,"Status"=>2,"Date"=>1660748195],
    ["NotificationId"=>$notifications[4]['_id'],"Recipient"=>17,"Status"=>3,"Date"=>1660759195],
    ["NotificationId"=>$notifications[5]['_id'],"Recipient"=>11,"Status"=>3,"Date"=>1660759195],
    ["NotificationId"=>$notifications[6]['_id'],"Recipient"=>10,"Status"=>3,"Date"=>1660759195],
    ["NotificationId"=>$notifications[7]['_id'],"Recipient"=>13,"Status"=>3,"Date"=>1660759195],
    ["NotificationId"=>$notifications[8]['_id'],"Recipient"=>12,"Status"=>3,"Date"=>1660759195],
    ["NotificationId"=>$notifications[9]['_id'],"Recipient"=>10,"Status"=>3,"Date"=>1660759195]

];
$database->createMany("NotificationStatus",$status);
echo "ok<br>";
?>
