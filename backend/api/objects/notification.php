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
    
    //function to get all notifications in database
    public function getAllNotifications(){
        $_CollectionName='Notifications';
        $values=[];
        $options=[];
        $notifications=$this->_Database->queryRecord($_CollectionName,$values,$options);
        return $notifications;
    }
}
?>