<?php
//this file will be the user object
class User{
    private $_Id;
    private $_FirstName;
    private $_LastName;
    private $_UserType;
    private $_PhoneNo;
    private $_Database;
    private $_CollectionName;
    private $_Faculty;
    private $_DepartmentId;
    private $_DepartmentName;

    //function called during initialization of user
    function __construct($db)
    {
        $this->_Database=$db;
        
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
    
    
    //get and department
    public function getDepartmentName(){
        return $this->_DepartmentName;
    }
    
    public function setDepartmentName($name){
        $this->_DepartmentName=$name;
    }

    //get the department id and set faculty from dep name
    public function setFaculty($name){
        //check in faculty record first since it is nested
        $values=["Department.DepartmentName"=>$name];
        //select only the faculty name
        $options=["projection"=>["FacultyName"=>1,"_id"=>0]];
        $record=$this->_Database->queryData('faculty',$values,$options);
        if($record){
            $this->_Faculty=$record[0]->FacultyName;
        }else{
            //check in staffdepartment
            $this->_Faculty="";
        }
        
        }
    
        //get and set faculty
    public function getFaculty(){
        return $this->_Faculty;
    }
    

//get and department
    public function setDepartmentId($id){
        $this->_DepartmentId=$id;
    }

    

    //get and set phone number
    public function getPhoneNo(){
        return $this->_PhoneNo;
    }
    public function setPhoneNo($phone){
        $this->_PhoneNo=$phone;
    }
    

    // get and set the admission number
    public function getIdNo(){
        return $this->_Id;
    }
    public function setIdNo($idno){
      $this->_Id=$idno;
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

    //function to update the user
    public function updateUser(){
        $from=["AdmissionNo"=>$this->getAdmissionNo()];
        $to=[
            "FirstName"=> $this->getFirstName(),
            "LastName"=> $this->getLastName(),
            "UserType"=> $this->getUserType(),
            "PhoneNo" => $this->getPhoneNo(),
            "Faculty"=> $this->getFaculty(),
            "Department"=> $this->getDepartment()
        ];
      
            $result=$this->_Database->modifyOne($this->_CollectionName,$from,$to);
            if($result){
                return TRUE;
            }
            else{
                return FALSE;
            }

    }


    //function to get user by admision number 
    public function getUserById($idNo){
        //go through all the databases to check if a user is valid
        $users=['student','staff'];
        $filter=['_id'=>$idNo];
        //supress mongo id field together with token
        //$options=["projection"=>['_id'=>0,"Token"=>0]];
        $options=[];
        foreach($users as $collection){
            $result=$this->_Database->queryData($collection,$filter,$options);
            if($result){
                //add the database name to the result as a way of showing the user type
                array_push($result,array("UserType"=>$collection));
                return $result;
                break;
            }
        }
        return FALSE;
    }

    //function to get student according to departement
    public function getStudentsByDepartmentName($departmentname){
        $filter=["DepartmentName"=>$departmentname];
        $options=["projection"=>["_id"=>1]];
        $result=$this->_Database->queryData('student',$filter,$options);
        if($result){
            return $result;
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
    public function setUserProfile($idNo){
        $row=$this->getUserById($idNo);
        if($row){{
            $this->setIdNo($row[0]->_id);
            $this->setFirstName($row[0]->FirstName);
            $this->setLastName($row[0]->LastName);
            $this->setUserType($row[1]['UserType']);
            $this->setPhoneNo($row[0]->PhoneNo);
            $this->setDepartmentName($row[0]->DepartmentName);
            $this->setFaculty($row[0]->DepartmentName);
            return TRUE;
            }
        }else{
            return FALSE;
        }
    }


    //function to set the login token for the user
    public function setToken($token){
        $from=["_id"=>$this->_Id];
        $to=['$set'=>["Token"=>$token]];
        $value=$this->_Database->upsertOne('token',$from,$to);
        if($value){
            return TRUE;
        }else{
            return FALSE;
        }
    }

    //function to get and return the user token
    public function getToken(){
        $match=["_id"=>$this->_Id];
        $options=["projection"=>["Token"=>1,"_id"=>0]];
        $query=$this->_Database->queryData('token',$match,$options);
        if($query){
            return $query[0]->Token;
        }
        else{
            return FALSE;
        }

    }

    //function to delete the token
    public function removeToken(){
        $match=["_id"=>$this->_Id];
        $options=[];
        $query=$this->_Database->deleteRecord('token',$match,$options);
        if($query){
            return TRUE;
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