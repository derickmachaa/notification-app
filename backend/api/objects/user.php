<?php
//this file will be the user object
class User{
    private $_AdmissionNo;
    private $_FirstName;
    private $_LastName;
    private $_UserType;
    private $_PhoneNo;
    private $_Database;
    private $_CollectionName;

    //function called during initialization of user
    function __construct($db)
    {
        $this->_Database=$db;
        $this->_CollectionName='users';
        
    }
    
    //function to sanitize data to prevent unwanted injections
    public function sanitize($text){
        return htmlspecialchars(strip_tags($text));
    }

    //function to set the user admission number
    public function setAdmissionNo($no){
        $this->_AdmissionNo=$no;
    }

    //function to get the and return the user firstname
    public function getFistName(){
        return $this->_FirstName;
    }

    //function to get user by admision number
    public function getUserByAdmission(){
        $filter=['AdmissionNo'=>$this->_AdmissionNo];
        $options=[];
        $result=$this->_Database->queryRecord($this->_CollectionName,$filter,$options);
        if(count($result)==1){
            return $result[0];
        }else{
            return FALSE;
        }
    }

    //function to set the userprofile
    public function setUserProfile(){
        $row=$this->getUserByAdmission();
        if($row){{
                $this->_FirstName = $row->FirstName;
                $this->_LastName = $row->LastName;
                $this->_UserType = $row->UserType;
                $this->_PhoneNo = $row->PhoneNo;
            }
        }else{
            return FALSE;
        }
    }
    
    //function to get the usertype
    public function getUserType(){
        return $this->_UserType;
    }

    //function to set the login token for the user
    public function setToken($token){
        $match=["AdmissionNo"=>$this->_AdmissionNo];
        $modify=["Token"=>$token];
        $value=$this->_Database->modifyOne($this->_CollectionName,$match,$modify);
        if($value){
            return TRUE;
        }else{
            return FALSE;
        }
    }

    //function to get and return the user token
    public function getToken(){
        $match=["AdmissionNo"=>$this->_AdmissionNo];
        $options=["projection"=>["Token"=>1]];
        $query=$this->_Database->queryRecord($this->_CollectionName,$match,$options);
        if($query){
            return $query[0]->Token;
        }
        else{
            return FALSE;
        }

    }
}
?>