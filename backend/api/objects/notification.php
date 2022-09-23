<?php
//file to handle the notifications
class Notification{
    private $_Content;
    private $_Sender;
    private $_NotificationID;
    private $_Recipient;
    private $_Database;
    private $_Notifications;
    private $_CollectionName;

    function __construct($database,$user)
    {
        $this->_Database=$database;
        $this->user=$user;
        $this->_CollectionName="notification";
    }
    
    //function to get notificationbyid
    public function getNotificationById($notificationid){
        $_CollectionName='Notifications';
        $values=["_id"=>new MongoDB\BSON\ObjectId($notificationid)];
        $options=[];
        $result=$this->_Database->queryData($_CollectionName,$values,$options);
        if($result){
            //return the message gotten
            return $result[0];
        }
        else{
            return FALSE;
        }
    }

    //public function generate report
    public function studentGenerateReport($admissionNo,$startdate,$enddate){
        //create an array to hold the results
        $sms=array();
        $sms['result']=array();
        $values=["SendDate"=>['$gte'=>$startdate,'$lte'=>$enddate],"RecipientId"=>$admissionNo];
        $options=[];
        $result=$this->_Database->queryData($this->_CollectionName,$values,$options);
        if($result){
            //iterate though each message
            foreach($result as $row){
                //get sender id
                $sender=$row->SenderId;
                //set user profile inorder to see the sender
                $this->user->setUserProfile($sender);
    
                $sms_list=array(
                    "Date" =>$row->SendDate,
                    "Content" =>$row->Content,
                    "FullNames"=>$this->user->getFirstName().' '.$this->user->getLastName(),
                    );
                array_push($sms['result'],$sms_list);
                }
                return $sms;
        }
        else{
            return FALSE;
        }
    }



        //function to mark sms as read
    public function markRead($notificationid,$admissionNo){
            $from=[
                "RecipientId"=>$admissionNo,
                "NotificationId"=>new MongoDB\BSON\ObjectId($notificationid),
                "CurrentStatus"=>['$lt'=>3]
            ];
            $to=['$set'=>[
                "CurrentStatus"=>3,"ReadDate"=>time()
            ]];
            $result=$this->_Database->updateOne('notificationstatus',$from,$to);
            if($result){
                return TRUE;
            }else{
                return FALSE;
            }
        }

        //function for reciver to mark notification as delivered
    public function markDelivered($notificationid,$receiverno){
            $from=[
                "RecipientId"=>$receiverno,
                "NotificationId"=>new MongoDB\BSON\ObjectId($notificationid),
                "CurrentStatus"=>['$lt'=>2]
            ];
            $to=['$set'=>
                            [
                                "CurrentStatus"=>2,"DeliveredDate"=>time()
                            ]
                ];
            $result=$this->_Database->updateOne('notificationstatus',$from,$to);
            if($result){
                return TRUE;
            }else{
                return FALSE;
            }
        }

    //function to allow sender to see the notifications sent
    public function senderGetAll($identNo){
        //declare an array to hold the notifications
        $sms=array();
        $sms['result']=array();
        $values=["SenderId"=>$identNo];
        $options=[
            //sort by date descending
            "sort"=>["SendDate"=>-1],
            "projection"=>["SenderId"=>0]
        ];
        //get notifications sent
        $notifications=$this->_Database->queryData($this->_CollectionName,$values,$options);
        foreach($notifications as $row){
            $sms_list=array(
            "Id"=>$row->_id->__toString(),
            "Content"=>$row->Content,
            "Description"=>$row->Description,
            "Date" =>$row->SendDate,
            "Status"=>$this->getNotificationStatus($row->_id->__toString())
            );
        array_push($sms['result'],$sms_list);
        }
        return $sms;
    }

    //function to allow reciver to see the notifications sent
    public function studentGetNotification($idNo){
        $sms=array();
        $sms['result']=array();
        $values=["RecipientId"=>$idNo];
        $options=[
            //sort by date descending order
            "sort"=>["SendDate"=>-1]
        ];
        $notifications=$this->_Database->queryData($this->_CollectionName,$values,$options);
       // print_r($notifications);
        foreach($notifications as $row){
            //get sender id
            $sender=$row->SenderId;
            $notificationid=$row->_id->__toString();//id of the message

            //get the current status
            //get the status of the message
	        $values=["NotificationId"=>new MongoDB\BSON\ObjectId($notificationid),"RecipientId"=>$idNo];
            $options=["projection"=>["CurrentStatus"=>1,"_id"=>0]];
            $status=$this->_Database->queryData("notificationstatus",$values,$options);
    
	        //set the profile of the sender so as to know him
            $this->user->setUserProfile($sender);
            $sms_list=array(
                "Id"=>$notificationid,
                "Description"=>$row->Description, //the description
                "Date" =>$row->SendDate, //the senddate
                "FullNames"=>$this->user->getFirstName().' '.$this->user->getLastName(),//sender
                "Status"=>$status[0]->CurrentStatus //current status
            );
            array_push($sms['result'],$sms_list);
            //mark message as delivered 
            $this->markDelivered($notificationid,$idNo);
        }
        return $sms;

    }

    //function to send notification to students
    public function sendNotification($message,$recipients,$sender,$description){
        //create a new id
        $notificationid=new MongoDB\BSON\ObjectId();
        //create a new record in the notification table
        //insert the message to db
            $values=[
                "_id"=>$notificationid,
                "SenderId"=>$sender,
                "RecipientId"=>$recipients,
                "Content"=>$message,
                "Description"=>$description,
                "SendDate"=>time()
            ];
            $result=$this->_Database->createRecord($this->_CollectionName,$values);
            //now iterate through the status db and set as sent
            if($result){
                //update all the status as inserted
                $_CollectionName='notificationstatus';
                foreach($recipients as $receiver){
                    $values=[
                    "NotificationId"=>$notificationid,
                    "RecipientId"=>$receiver,
                    "CurrentStatus"=>1,
                    "ReadDate"=>0,
                    "DeliveredDate"=>0
                    ];
            $this->_Database->createRecord($_CollectionName,$values);
	    }
	    return TRUE;
        }
        else{
            return FALSE;
        }

    }
    
    //function to send to department
    public function sendToDep($message,$departmentname,$sender,$description){
	//get all user id in that department
	    $recipients=array();
	    foreach($departmentname as $department){
	//iterate through the array
	$values=["UserType"=>"student","DepartmentName"=>$department];
	$options=["projection"=>["id"=>1]];
	$result=$this->_Database->queryData("users",$values,$options);
	if($result){
		foreach($result as $student){
			array_push($recipients,$student->_id);
		}
	}else{
		return FALSE;
	}
	}
        //create a new id
        $notificationid=new MongoDB\BSON\ObjectId();
        //create a new record in the notification table
        //insert the message to db
            $values=[
                "_id"=>$notificationid,
                "SenderId"=>$sender,
                "RecipientId"=>$recipients,
                "Content"=>$message,
                "Description"=>$description,
                "SendDate"=>time()
            ];
            $result=$this->_Database->createRecord($this->_CollectionName,$values);
            //now iterate through the status db and set as sent
            if($result){
                //update all the status as inserted
                $_CollectionName='notificationstatus';
                foreach($recipients as $receiver){
                    $values=[
                    "NotificationId"=>$notificationid,
                    "RecipientId"=>$receiver,
                    "CurrentStatus"=>1,
                    "ReadDate"=>0,
                    "DeliveredDate"=>0
                    ];
            $this->_Database->createRecord($_CollectionName,$values);
	    }
	    return TRUE;
        }
        else{
            return FALSE;
        }

    }
    //function to send to school
    public function sendToSchool($message,$sender,$description){
	//get all students in the system
	    $recipients=array();
	    $students=$this->user->getAllStudents();
	    if($students){
	    foreach($students as $student){
		//iterate through the array
		array_push($recipients,$student->_id);
		}
        //create a new id
        $notificationid=new MongoDB\BSON\ObjectId();
        //create a new record in the notification table
        //insert the message to db
            $values=[
                "_id"=>$notificationid,
                "SenderId"=>$sender,
                "RecipientId"=>$recipients,
                "Content"=>$message,
                "Description"=>$description,
                "SendDate"=>time()
            ];
            $result=$this->_Database->createRecord($this->_CollectionName,$values);
            //now iterate through the status db and set as sent
            if($result){
                //update all the status as inserted
                $_CollectionName='notificationstatus';
                foreach($recipients as $receiver){
                    $values=[
                    "NotificationId"=>$notificationid,
                    "RecipientId"=>$receiver,
                    "CurrentStatus"=>1,
                    "ReadDate"=>0,
                    "DeliveredDate"=>0
                    ];
            $this->_Database->createRecord($_CollectionName,$values);
	    }
	    return TRUE;
        }
        else{
            return FALSE;
        }
	    }
	    else{
		    return FALSE;
	    }

    }


    //function to delete a notification
    public function deleteNotification($notificationid,$admissionNo){
        //where value = id and sender=id
        $values=[
            "_id" => new MongoDB\BSON\ObjectId($notificationid),
            "SenderId" => $admissionNo
        ];
        $options=[];
        $result=$this->_Database->deleteRecord($this->_CollectionName,$values,$options);
        if($result){
            //delete also on status
            $_CollectionName="notificationstatus";
            $values=[
                "NotificationId"=>new MongoDB\BSON\ObjectId($notificationid)
            ];
            $options=[];
            $result=$this->_Database->deleteRecord($_CollectionName,$values,$options);
            if($result){
                return TRUE;
            }
            else{
                return FALSE;
            }
        }
        else{
            return FALSE;
        }
    }

    //function for sender to get a specific notification
    public function getNotificationSentById($notificationid,$admissionNo){
        $sms=array();
        $values=[
            "_id"=>new MongoDB\BSON\ObjectId($notificationid),
            "SenderId"=>$admissionNo];
            $options=[
                "projection"=>['Content'=>1,'SendDate'=>1]
            ];
            $result=$this->_Database->queryData($this->_CollectionName,$values,$options);
            if($result){
                $sms['result']=array(
                    "Content"=>$result[0]->Content,
                    "Date"=>$result[0]->SendDate,
                    "Status"=>$this->getNotificationStatus($notificationid)
                );
                return $sms;
            }
            else{
                return FALSE;
            }
    }

    //function to get the status of a message from notification table
    public function getNotificationStatus($notificationid){
        //some variables
        $sent=0;
        $delivered=0;
        $read=0;
        $_CollectionName="notificationstatus";
        $values=["NotificationId"=>new MongoDB\BSON\ObjectId($notificationid)];
        $options=["projection"=>["CurrentStatus"=>1,"_id"=>0]];
        $result=$this->_Database->queryData($_CollectionName,$values,$options);
	//create a value to hold the total of the messages
	$total=count($result)*3; //multiply  by 3 since a message that is fully processed should have a value of 3
	$prg=0;
	if($result){
		foreach ($result as $row) {
                if($row->CurrentStatus==1){
			$sent+=1;
			$prg+=1;
                }else if($row->CurrentStatus==2){
			$delivered+=1;
			$prg+=2;
                }
                else if($row->CurrentStatus==3){
			$read+=1;
			$prg+=3;
                }
		}

		$progress=(int)($prg/$total*100); //calculate the progress of the message done

		return array("sent"=>$sent,"delivered"=>$delivered,"read"=>$read,"progress"=>$progress);
    }
    else{
        return FALSE;
    }
}

    //function for student to get a specific notification
    public function studentGetNotificationById($notificationid,$admissionNo){
        $sms=array();
        $values=[
            "_id"=>new MongoDB\BSON\ObjectId($notificationid),
            "RecipientId"=>$admissionNo,
         ];
         $options=["projection"=>['Recipients'=>0]];
         $result=$this->_Database->queryData($this->_CollectionName,$values,$options);
         if($result){
            $smsdetails=$result[0];
            // get the sender details from sender id
            //get sender id
            $sender=$smsdetails->SenderId;

            //get the message status
            $values=["NotificationId"=>new MongoDB\BSON\ObjectId($notificationid),"RecipientId"=>$admissionNo];
            $options=["projection"=>["CurrentStatus"=>1,"_id"=>0]];
            $status=$this->_Database->queryData("notificationstatus",$values,$options);


            //set user profile
            $this->user->setUserProfile($sender);
            $sms['result']=array(
                    "Content"=>$smsdetails->Content,
                    "Date"=>$smsdetails->SendDate,
                    "FullNames"=>$this->user->getFirstName().' '.$this->user->getLastName(),
                    "Faculty"=>$this->user->getFaculty(),
                    "Department"=>$this->user->getDepartmentName(),
                    "Status"=>$status[0]->CurrentStatus //status of message
                );
                //mark notification as read
                $this->markRead($notificationid,$admissionNo);
                return $sms;
            }
            else{
                return FALSE;
            }

    }
    //function to generate report according to date

    public function staffGenerateReportByDate($idNo,$startdate,$enddate){
        //create an array to hold the sms
        $sms=array();
        $values=["SendDate"=>['$gte'=>$startdate,'$lte'=>$enddate],"SenderId"=>$idNo];
        $options=[];
        $result=$this->_Database->queryData('notification',$values,$options);
        if($result){
            foreach($result as $row){
                $notificationid=$row->_id;
                //get the status of each member
                $values=["NotificationId"=>$notificationid];
                $options=["projection"=>["NotificationId"=>0]];
                $status=$this->_Database->queryData('notificationstatus',$values,$options);
                //iterate through all the list
                foreach($status as $info){
                    //create an array for each message and push to sms
			//check the date and set
			if($info->DeliveredDate>0){
				//convert epoc to date time
				$DeliveredDate=date("Y-m-d H:i:s",$info->DeliveredDate);
			}
			else{
				$DeliveredDate=0;
			}

			if($info->ReadDate>0){
				//convert epoc to time
				$ReadDate=date("Y-m-d H:i:s",$info->ReadDate);
			}
			else{
				$ReadDate = 0;
			}
                    $new=array(
                        "NotificationId"=>$notificationid->__toString(),
                        "SendDate"=>date("Y-m-d H:i:s",$row->SendDate),
                        "Content"=>'"'.$row->Content.'"',
                        "Description"=>'"'.$row->Description.'"',
                        "RecipientId"=>$info->RecipientId,
                        "DeliveredDate"=>$DeliveredDate,
                        "ReadDate"=>$ReadDate,
                    );
                    array_push($sms,$new);
                }
            }
            return $sms;
        }

    }

      //public function lecturer generate report
      public function LecturerGenerateReport($admissionNo,$filters){
          //first get all notifications belonging to the lecturer
          $lecsms=$this->_Database->queryData($this->_CollectionName,$filters,[]);
          //iterate through the list
          foreach($lecsms as $row){
              //get the status of each message
              $id=$row->_id;
              echo $id;
              $values=["NotificationId"=>$id];
              $options=["projection"=>["NotificationId"=>0]];
              $report=$this->_Database->queryData('notificationstatus',$values,[]);
              print_r($report);

          }
          return $lecsms;
          $result=$lecsms['result'];
          //now iterate through the result
        // foreach($result as $row){
        //     $datesent=$row['Date'];
        //     $description=$row['Description'];
        //     $content=$row['Content'];
        //     $id=$row['Id'];
        //     //get the recipients of the message depending on the filters
        //     $_CollectionName='NotificationStatus';
        //     $options=[];
        //     $values=["NotificationId"=>new MongoDB\BSON\ObjectId($id)];
        //     $result2=$this->_Database->queryData($_CollectionName,$values,$options);
        //     foreach($result2 as $row2){
        //         $recipient=$row2->Recipient;
        //         $status=$row2->Status;
        //         $dateupdate=$row2->Date;
        //         echo $dateupdate;
        //     }
        }

          //}
        // $_CollectionName='NotificationStatus';
        // //create an array to hold the results
        // $sms=array();
        // $sms['result']=array();
        // $values=["Date"=>['$gte'=>$startdate,'$lte'=>$enddate],"Recipients"=>$admissionNo];
        // $options=[];
        // $result=$this->_Database->queryRecord($_CollectionName,$values,$options);
        // if($result){
        //     //iterate though each message
        //     foreach($result as $row){
        //         //get sender id
        //         $sender=$row['SenderId'];
        //         //get message id
        //         $id=$row->_id->__toString();
        //         //get the status of the message
        //         $values=["NotificationId"=>new MongoDB\BSON\ObjectId($id),"Recipient"=>$admissionNo];
        //         $options=["projection"=>["Status"=>1,"_id"=>0]];
        //         $status=$this->_Database->queryRecord("NotificationStatus",$values,$options);
            
        //         //set user profile
        //         $this->user->setUserProfile($sender);
    
        //         $sms_list=array(
        //             "Id"=>$id,
        //             "Date" =>$row->Date,
        //             "Content" =>$row->Content,
        //             "FullNames"=>$this->user->getFirstName().' '.$this->user->getLastName(),
        //             //"Status"=>$status[0]->Status
        //             );
        //         array_push($sms['result'],$sms_list);
        //         }
        //         return $sms;
        // }
        // else{
        //     return FALSE;
        // }
    }


?>
