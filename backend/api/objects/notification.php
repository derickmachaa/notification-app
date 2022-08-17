<?php
//file to handle the notifications
class Notification{
    private $_Content;
    private $_Sender;
    private $_NotificationID;
    private $_Recipient;
    private $_Database;
    private $_Notifications;

    function __construct($database,$user)
    {
        $this->_Database=$database;
        $this->user=$user;
    }
    
    //function to get notificationbyid
    public function getNotificationById($notificationid){
        $_CollectionName='Notifications';
        $values=["_id"=>new MongoDB\BSON\ObjectId($notificationid)];
        $options=[];
        $result=$this->_Database->queryRecord($_CollectionName,$values,$options);
        if($result){
            //return the message gotten
            return $result[0];
        }
        else{
            return FALSE;
        }
    }


        //function to mark sms as read
    public function markRead($notificationid,$admissionNo){
            $_CollectionName='NotificationStatus';
            $from=[
                "Recipient"=>$admissionNo,
                "NotificationId"=>new MongoDB\BSON\ObjectId($notificationid),
                "Status"=>['$lt'=>3]
            ];
            $to=[
                "Status"=>3,"Date"=>time()
            ];
            $result=$this->_Database->modifyOne($_CollectionName,$from,$to);
            if($result){
                return TRUE;
            }else{
                return FALSE;
            }
        }

        //function for reciver to mark notification as delivered
    public function markDelivered($notificationid,$receiverno){
            $_CollectionName='NotificationStatus';
            $from=[
                "Recipient"=>$receiverno,
                "NotificationId"=>new MongoDB\BSON\ObjectId($notificationid),
                "Status"=>['$lt'=>2]
            ];
            $to=[
                "Status"=>2,"Date"=>time()
            ];
            $result=$this->_Database->modifyOne($_CollectionName,$from,$to);
            if($result){
                return TRUE;
            }else{
                return FALSE;
            }
        }

    //function to allow sender to see the notifications sent
    public function senderGetAll($admissionNo){
        //declare an array to hold the notifications
        $sms=array();
        $sms['result']=array();
        $_CollectionName='Notifications';
        $values=["SenderId"=>$admissionNo];
        $options=[
            //sort by date descending
            "sort"=>["Date"=>-1],
            "projection"=>["SenderId"=>0]
        ];
        //get student notifications
        $notifications=$this->_Database->queryRecord($_CollectionName,$values,$options);
        foreach($notifications as $row){
            $sms_list=array(
            "Id"=>$row->_id->__toString(),
            "Content"=>$row->Content,
            "Description"=>$row->Description,
            "Date" =>$row->Date
            );
        array_push($sms['result'],$sms_list);
        }
        return $sms;
    }

    //function to allow reciver to see the notifications sent
    public function recieverGetAll($admissionNo){
        $sms=array();
        $sms['result']=array();
        $_CollectionName='Notifications';
        $values=["Recipients"=>$admissionNo];
        $options=[
            //sort by date descending order
            "sort"=>["Date"=>-1]
        ];
        $notifications=$this->_Database->queryRecord($_CollectionName,$values,$options);
        foreach($notifications as $row){
            //get sender id
            $sender=$row['SenderId'];
            //get message id
            $id=$row->_id->__toString();
            //set user profile
            $this->user->setUserProfile($sender);

            $sms_list=array(
                "Id"=>$id,
                "Description"=>$row->Description,
                "Date" =>$row->Date,
                "FullNames"=>$this->user->getFirstName().' '.$this->user->getLastName()
            );
            array_push($sms['result'],$sms_list);
            //mark message as delivered
            $this->markDelivered($id,$admissionNo);
        }
        return $sms;

    }

    //function to send notification to students
    public function sendNotification($message,$recipients,$sender,$description){
        //the table/collection name
        $_CollectionName='Notifications';
        //insert the message to db
        $values=[
            "SenderId"=>$sender,
            "Recipients"=>$recipients,
            "Content"=>$message,
            "Description"=>$description,
            "Date"=>time()
        ];
        $result=$this->_Database->createOne($_CollectionName,$values);
        if($result){
            //insert status as pending 
            $_CollectionName='NotificationStatus';
            foreach($recipients as $receiver){
            $values=[
                "NotificationId"=>$result,
                "Status"=>1,
                "Recipient"=>$receiver,
                "Date"=>time()
            ];
            $result=$this->_Database->createOne($_CollectionName,$values);
            if($result){
                return TRUE;
            }
        }
        }
        else{
            return FALSE;
        }

    }

    //function for sender to get a specific notification
    public function getNotificationSentById($notificationid,$admissionNo){
        $sms=array();
        $_CollectionName='Notifications';
        $values=[
            "_id"=>new MongoDB\BSON\ObjectId($notificationid),
            "SenderId"=>$admissionNo];
            $options=[
                "projection"=>['Content'=>1,'Date'=>1]
            ];
            $result=$this->_Database->queryRecord($_CollectionName,$values,$options);
            if($result){
                $sms['results']=array(
                    "Content"=>$result[0]['Content'],
                    "Date"=>$result[0]['Date']
                );
                return $sms;
            }
            else{
                return FALSE;
            }
    }

    //function for reciever to get a specific notification
    public function getNotificationRecievedById($notificationid,$admissionNo){
        $sms=array();
        $_CollectionName='Notifications';
        $values=[
            "_id"=>new MongoDB\BSON\ObjectId($notificationid),
            "Recipients"=>$admissionNo,
         ];
         $options=["projection"=>['Recipients'=>0]];
         $result=$this->_Database->queryRecord($_CollectionName,$values,$options);
         if($result){
            $smsdetails=$result[0];
            //get the message status
            $values=["NotificationId"=>new MongoDB\BSON\ObjectId($notificationid)];
            $options=["projection"=>["Status"=>1,"_id"=>0]];
            $status=$this->_Database->queryRecord("NotificationStatus",$values,$options);
            // get the sender details from sender id
            //get sender id
            $sender=$smsdetails['SenderId'];
            //set user profile
            $this->user->setUserProfile($sender);
            $sms['results']=array(
                    "Content"=>$smsdetails['Content'],
                    "Date"=>$smsdetails['Date'],
                    "FullNames"=>$this->user->getFirstName().' '.$this->user->getLastName(),
                    "Faculty"=>$this->user->getFaculty(),
                    "Department"=>$this->user->getDepartment(),
                    "Status"=>$status[0]->Status
                );
                //mark notification as read
                $this->markRead($notificationid,$admissionNo);
                return $sms;
            }
            else{
                return FALSE;
            }

    }

}
?>