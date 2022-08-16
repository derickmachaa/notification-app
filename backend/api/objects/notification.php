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
    
    //function to get sent notifications in database according to admission number and role
    public function getNotifications($admissionNo,$role){
        //declare an array to hold the notifications
        $sms=array();
        $sms['result']=array();
        $_CollectionName='Notifications';
        //options to enable select only the valid sms
        if($role=="student"){
            $values=["Recipients"=>$admissionNo];
            $options=[
                "sort"=>["Date"=>-1],
                "projection"=>["Recipients"=>0]
            ];
            //get student notifications
            $notifications=$this->_Database->queryRecord($_CollectionName,$values,$options);
            foreach($notifications as $row){
                $sms_list=array(
                    "Id"=>$row->_id->__toString(),
                    "Content"=>$row->Content,
                    "Date" =>$row->Date,
                    "Sender" => $row->SenderId
                );
                array_push($sms['result'],$sms_list);
            }
        }//get lecturer notifications
        else if($role=="lecturer"){
            $values=["SenderId"=>$admissionNo];
            $options=[
                "sort"=>["Date"=>-1],
                "projection"=>['Sender'=>0]
        ];
        $notifications=$this->_Database->queryRecord($_CollectionName,$values,$options);
            foreach($notifications as $row){
                $sms_list=array(
                    "Id"=>$row->_id->__toString(),
                    "Content"=>$row->Content,
                    "Date" =>$row->Date
                );
                array_push($sms['result'],$sms_list);
            }

        }
        
        return $sms;
    }
}
?>