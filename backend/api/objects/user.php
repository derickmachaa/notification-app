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
    

    //getters and setters firstname
    public function getFirstName(){
        return $this->_FirstName;
    }
    
    public function setFirstName($fname){
        $this->_FirstName=$fname;
    }
    
    //get and set for lastname
    public function getLastName(){
        return $this->_LastName;
    }
    public function setLastName($lname){
        $this->_LastName=$lname;
    }
    
    //get and set faculty
    public function getFaculty(){
        return $this->_Faculty;
    }
    public function setFaculty($faculty){
        $this->_Faculty=$faculty;
    }
    
    
    //get and department
    public function getDepartment(){
        return $this->_Department;
    }
    public function setDepartment($department){
        $this->_Department=$department;
    }
    

    //get and set phone number
    public function getPhoneNo(){
        return $this->_PhoneNo;
    }
    public function setPhoneNo($phone){
        $this->_PhoneNo=$phone;
    }
    

    // get and set the admission number
    public function getAdmissionNo(){
        return $this->_AdmissionNo;
    }
    public function setAdmissionNo($admissionno){
      $this->_AdmissionNo=$admissionno;
    }
    

    //function to get the usertype
    public function getUserType(){
        return $this->_UserType;
    }
    public function setUserType($usertype){
        $this->_UserType=$usertype;
    }
    

    //function to create a new user in the system
    public function createUser(){
        //place the appropriate values
        $values=[
            "AdmissionNo"=> $this->getAdmissionNo(),
            "FirstName"=> $this->getFirstName(),
            "LastName"=> $this->getLastName(),
            "UserType"=> $this->getUserType(),
            "PhoneNo" => $this->getPhoneNo(),
            "Faculty"=> $this->getFaculty(),
            "Department"=> $this->getDepartment()
        ];
      
            $result=$this->_Database->createOne($this->_CollectionName,$values);
            if($result){
                return TRUE;
            }
            else{
                return FALSE;
            }
    
    }


    //function to get user by admision number
    public function getUserByAdmission($admissionNo){
        $filter=['AdmissionNo'=>$admissionNo];
        //supress mongo id field together with token
        $options=["projection"=>['_id'=>0,"Token"=>0]];
        $result=$this->_Database->queryRecord($this->_CollectionName,$filter,$options);
        if(count($result)==1){
            return $result[0];
        }else{
            return FALSE;
        }
    }


    //function to delete user by id
    public function deleteUserByAdmission($admissionNo){
        //set the admission number to the required one
        $value=['AdmissionNo'=>$admissionNo];
        $result=$this->_Database->removeOne($this->_CollectionName,$value);
        if($result){
            //now delete the previous messages sent
            $value=['Recipient'=>$admissionNo];
            $result=$this->_Database->removeOne("NotificationStatus",$value);
            //respond
            return TRUE;
        }
        else{
            return FALSE;
        }

    }

    //function to set the userprofile
    public function setUserProfile($no){
        $row=$this->getUserByAdmission($no);
        if($row){{
            $this->setAdmissionNo($row->AdmissionNo);
            $this->setFirstName($row->FirstName);
            $this->setLastName($row->LastName);
            $this->setUserType($row->UserType);
            $this->setPhoneNo($row->PhoneNo);
            $this->setDepartment($row->Department);
            $this->setFaculty($row->Faculty);
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

    //function to get all users in the system
    public function getAllUsers(){
        $values=[];
        $options=[];
        $query=$this->_Database->queryRecord($this->_CollectionName,$values,$options);
        if($query){
            return $query;
        }
        else{
            return FALSE;
        }
    }
}
?>