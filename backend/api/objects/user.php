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
    private $_Faculty;
    private $_Department;

    //function called during initialization of user
    function __construct($db)
    {
        $this->_Database=$db;
        $this->_CollectionName='users';
        
    }
    

    //function to get the and return the user firstname
    public function getFirstName(){
        return $this->_FirstName;
    }
    //get lastname
    public function getLastName(){
        return $this->_LastName;
    }
    //get faculty
    public function getFaculty(){
        return $this->_Faculty;
    }
    //get department
    public function getDepartment(){
        return $this->_Department;
    }

    //get phone number
    public function getPhoneNo(){
        return $this->_PhoneNo;
    }

    //function to get the admission number
    public function getAdmissionNo(){
        return $this->_AdmissionNo;
    }

    //function to get the usertype
    public function getUserType(){
        return $this->_UserType;
    }

    //function to get user by admision number
    public function getUserByAdmission($admissionNo){
        $filter=['AdmissionNo'=>$admissionNo];
        $options=[];
        $result=$this->_Database->queryRecord($this->_CollectionName,$filter,$options);
        if(count($result)==1){
            return $result[0];
        }else{
            return FALSE;
        }
    }

    //function to set the userprofile
    public function setUserProfile($no){
        $row=$this->getUserByAdmission($no);
        if($row){{
                $this->_AdmissionNo = $row->AdmissionNo;
                $this->_FirstName = $row->FirstName;
                $this->_LastName = $row->LastName;
                $this->_UserType = $row->UserType;
                $this->_PhoneNo = $row->PhoneNo;
                $this->_Department=$row->Department;
                $this->_Faculty=$row->Faculty;
                return TRUE;
            }
        }else{
            return FALSE;
        }
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
        $options=["projection"=>["Token"=>1,"_id"=>0]];
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