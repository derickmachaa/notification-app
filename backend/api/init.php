<?php
//This file is for initilisation

include '../config/config.php';
require_once ROOT.'/api/objects/database.php';


$database=new Database();
$db=$database->getConnection();

$compscience="Computer and Information Science";
$community="Community Health and Development";
$maths="Mathematics and Acturial Science";
$nursing="Nursing";
$naturalsci="Natural Sciences";
$cps="CONTINUING PROFESSIONAL DEVELOPMENT PROJECTS AND RESEARCH"; 
$socialsci="Social Sciences and Development Studies"; 
$religious="Religious Studies"; 
$humanities="Humanities";
$economics="Economics" ;
$theology="Biblical Theology";
$dogmatic="Dogmatic Theology";
$moral="Moral Theology";
$pastoral="Pastoral Theology";
$ictdep="ICT";
$creditdep="Credit Control";
$registrydep="Registry";
$psychology="Counseling Psychology"; 


//create faculty
$faculty=[
    ["FacultyName"=>"Faculty of Science","Department"=>array(["DepartmentName"=>"Computer and Information Science"],["DepartmentName"=>"Community Health and Development"],["DepartmentName"=>"Mathematics and Actuarial Science"],["DepartmentName"=>"Natural Sciences"],["DepartmentName"=>"Nursing"])],
    ["FacultyName"=>"Faculty of Law","Department"=>array(["DepartmentName"=>"CONTINUING PROFESSIONAL DEVELOPMENT PROJECTS AND RESEARCH"])],
    ["FacultyName"=>"Faculty of Arts & Social Science","Department"=>array(["DepartmentName"=>"Social Sciences and Development Studies"],["DepartmentName"=>"Religious Studies"],["DepartmentName"=>"Economics"],["DepartmentName"=>"Counseling Psychology"],["DepartmentName"=>"Humanities"])],
    ["FacultyName"=>"Faculty of Theology","Department"=>array(["DepartmentName"=>"Biblical Theology"],["DepartmentName"=>"Dogmatic Theology"],["DepartmentName"=>"Moral Theology"],["DepartmentName"=>"Pastoral Theology"])],
];

$staffdepartment=[
    ["DepartmentName"=>"ICT"],
    ["DepartmentName"=>"Credit Control"],
    ["DepartmentName"=>"Registry"],
];

//create collection of Students
//add some users
$student=[
    ["_id"=>1000,"FirstName"=>"Derick","LastName"=>"Macharia","PhoneNo"=>"254711218298","DepartmentName"=>$compscience,"Gender"=>"Male"],
    ["_id"=>1001,"FirstName"=>"Juma","LastName"=>"Mwaure","PhoneNo"=>"254712345670","DepartmentName"=>$economics,"Gender"=>"Male"],
    ["_id"=>1002,"FirstName"=>"Wema","LastName"=>"Wamboi","PhoneNo"=>"254712345671","DepartmentName"=>$community,"Gender"=>"Female"],
    ["_id"=>1003,"FirstName"=>"Samuel","LastName"=>"John","PhoneNo"=>"254712345672","DepartmentName"=>$socialsci,"Gender"=>"Male"],
    ["_id"=>1004,"FirstName"=>"Zipporah","LastName"=>"Wanjiru","PhoneNo"=>"254712345673","DepartmentName"=>$maths, "Gender"=>"Female"],
    ["_id"=>1005,"FirstName"=>"Brenda","LastName"=>"kivuva","PhoneNo"=>"254712345674","DepartmentName"=>$naturalsci, "Gender"=>"Female"],
    ["_id"=>1006,"FirstName"=>"Anne","LastName"=>"Luke","PhoneNo"=>"254712345675","DepartmentName"=>$humanities, "Gender"=>"Female"],
    ["_id"=>1007,"FirstName"=>"Diana","LastName"=>"Peter","PhoneNo"=>"254712345676","DepartmentName"=>$theology, "Gender"=>"Female"],
    ["_id"=>1008,"FirstName"=>"Denzel","LastName"=>"Michael","PhoneNo"=>"254712345677","DepartmentName"=>$religious, "Gender"=>"Male"],
    ["_id"=>1009,"FirstName"=>"Daniel","LastName"=>"Mike","PhoneNo"=>"254712345678","DepartmentName"=>$pastoral, "Gender"=>"Male"]
];


$staff=[
    ["_id"=>8000,"FirstName"=>"Edward","LastName"=>"Maina","is_lec"=>true,"PhoneNo"=>"254712345679","is_admin"=>false,"DepartmentName"=>$compscience,"Gender"=>"Male","JobTitle"=>"Lecturer"],
    ["_id"=>8001,"FirstName"=>"Francis","LastName"=>"Mwangi","is_lec"=>true,"PhoneNo"=>"254712345679","is_admin"=>false,"DepartmentName"=>$theology, "Gender"=>"Male","JobTitle"=>"Lecturer"],
    ["_id"=>8002,"FirstName"=>"Vincent","LastName"=>"Kamau","is_lec"=>false,"PhoneNo"=>"254712345679","is_admin"=>false,"DepartmentName"=>$creditdep, "Gender"=>"Male","JobTitle"=>"Credit Controller"],
    ["_id"=>8003,"FirstName"=>"James","LastName"=>"Njoroge","is_lec"=>false,"PhoneNo"=>"254712345679","is_admin"=>false,"DepartmentName"=>$registrydep, "Gender"=>"Male","JobTitle"=>"Registrar"],
    ["_id"=>8004,"FirstName"=>"Mercy","LastName"=>"Wamboi","is_lec"=>true,"PhoneNo"=>"254712345679","is_admin"=>false,"DepartmentName"=>$maths,"Gender"=>"Female","JobTitle"=>"Lecturer"],
    ["_id"=>8005,"FirstName"=>"Diana","LastName"=>"Akinyi","is_lec"=>true,"PhoneNo"=>"254712345679","is_admin"=>false,"DepartmentName"=>$theology, "Gender"=>"Female","JobTitle"=>"Lecturer"],
    ["_id"=>8006,"FirstName"=>"Derick","LastName"=>"Kamoro","is_lec"=>false,"PhoneNo"=>"254701873605","is_admin"=>true,"DepartmentName"=>$ictdep, "Gender"=>"Male","JobTitle"=>"Admin"],

];

$notify=new MongoDB\BSON\ObjectId();
$notify0=new MongoDB\BSON\ObjectId();
$notify1=new MongoDB\BSON\ObjectId();
$notify2=new MongoDB\BSON\ObjectId();
$notify3=new MongoDB\BSON\ObjectId();
$notify4=new MongoDB\BSON\ObjectId();
$notify5=new MongoDB\BSON\ObjectId();
$notify6=new MongoDB\BSON\ObjectId();
$notify7=new MongoDB\BSON\ObjectId();
$notify8=new MongoDB\BSON\ObjectId();
$notify9=new MongoDB\BSON\ObjectId();
$notify10=new MongoDB\BSON\ObjectId();
$notify11=new MongoDB\BSON\ObjectId();
$notify12=new MongoDB\BSON\ObjectId();


$notification=[
    ["_id"=>$notify0,"SenderId"=>8000,"Description"=>"Definition of Api","Content"=>"An API is a set of definitions and protocols for building and integrating application software. It’s sometimes referred to as a contract between an information provider and an information user—establishing the content required from the consumer (the call) and the content required by the producer (the response).","Recipients"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659301200],
    
    ["_id"=>$notify,"SenderId"=>8001,"Description"=>"Cat Results are out","Content"=>"Please imporeve","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659342200],
    
    ["_id"=>$notify1,"SenderId"=>8000,"Description"=>"Linux is cool","Content"=>"Linux is one of the most prominent examples of free and open-source software collaboration. The source code may be used, modified and distributed commercially or non-commercially by anyone under the terms of its respective licenses, such as the GNU General Public License (GPL).","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659343200],    
    
    ["_id"=>$notify2,"SenderId"=>8001,"Description"=>"Super Computers","Content"=>"A supercomputer is a computer that performs at or near the highest operational rate for computers","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659301200],

    ["_id"=>$notify3,"SenderId"=>8002,"Description"=>"Kernel","Content"=>"The kernel is the essential foundation of a computer's operating system (OS). It is the core that provides basic services for all other parts of the OS.","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659501200],

    ["_id"=>$notify4,"SenderId"=>8002,"Description"=>"Machince","Content"=>"a mechanically, electrically, or electronically operated device for performing a task","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659101200],

    ["_id"=>$notify5,"SenderId"=>8003,"Description"=>"Human Error","Content"=>"You might think that your files are safe on your Mac and Windows machine. But hard drives are not perfect. Data can become corrupted, and it's easy to delete the wrong files by mistake","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659101200],
    ["_id"=>$notify6,"SenderId"=>8003,"Description"=>"Resonant Frequency","Content"=>"The frequency where both parameters overlap is known as the resonant frequency of an RLC circuit.","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659201200],

    ["_id"=>$notify7,"SenderId"=>8004,"Description"=>"Physics","Content"=>"Physics is the natural science that studies matter, its fundamental constituents, its motion and behavior through space and time, and the related entities of energy and force","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659301200],
    ["_id"=>$notify8,"SenderId"=>8004,"Description"=>"Solar","Content"=>"solar energy, radiation from the Sun capable of producing heat, causing chemical reactions, or generating electricity","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659201200],

    ["_id"=>$notify9,"SenderId"=>8005,"Description"=>"Whoami CMD","Content"=>"whoami is a command found on most Unix-like operating systems, Intel iRMX 86, every Microsoft Windows[1] operating system since Windows Server 2003, and on ReactOS. It is a concatenation of the words Who am I? and prints the effective username of the current user when invoked. ","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659300200],

    ["_id"=>$notify10,"SenderId"=>8005,"Description"=>"ls CMD","Content"=>"In computing, ls is a command to list computer files in Unix and Unix-like operating systems. ls is specified by POSIX and the Single UNIX Specification","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659301200],
    ["_id"=>$notify11,"SenderId"=>8006,"Description"=>"stat CMD","Content"=>"stat() is a Unix system call that returns file attributes about an inode","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659305200],

    ["_id"=>$notify12,"SenderId"=>8006,"Description"=>"Did not do cat","Content"=>"Make up cat available at Jubilee Hall from 9am wednesday","RecipientId"=>[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009],"SendDate"=>1659304200],
    
    
];



////create a collection of faculties
//foreach($faculty as $row){
//	$database->createRecord('faculty',$row);
//}
////create departments
//foreach($staff as $row){
//    $database->createRecord('staff',$row);
//}
//
//foreach($staffdepartment as $row){
//    $database->createRecord('staffdepartment',$row);
//}
//
//foreach($student as $row){
//    $database->createRecord('student',$row);
//}
//
foreach($notification as $row){
    $database->createRecord('notification',$row);
}

$status=[$notify,$notify0,$notify1,$notify2,$notify3,$notify4,$notify5,$notify6,$notify7,$notify8,$notify9,$notify10,$notify11,$notify12];
$recipients=[1000,1001,1002,1003,1004,1005,1006,1007,1008,1009];
foreach($status as $row){
	foreach($recipients as $reciever){
		$values=["RecipientId"=>$reciever,"DeliveredDate"=>0,"ReadDate"=>0,"CurrentStatus"=>1,"NotificationId"=>$row];
		$database->createRecord('notificationstatus',$values);
	}
}


?>
