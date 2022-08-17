<?php
//The Database class to intercat with the database
class Database{
    private $conn;

    //constuctor class called when initializing db
    function __construct()
    {
        // Creating Connection to mongodb
        $uri="mongodb://". DB_HOST . ':' . DB_PORT;
        
        //select a database
        $this->db = new MongoDB\Client($uri);
        $this->conn = $this->db->selectDatabase(DB_NAME);
    }

    function getConnection(){
        //get the mongo database connection
        return $this->conn;
    }

    //function to insert one record to the database
    function createOne($collection_name,$values){
        $insertone = $this->conn->$collection_name->InsertOne($values);
        if($insertone->getInsertedCount()==1){
            return $insertone->getInsertedId();
        }
        else{
            return FALSE;
        }
    }

    //fucntion to insert many records to the database
    function createMany($collection_name,$values){
        $insertmany = $this->conn->$collection_name->InsertMany($values);
        if($insertmany->getInsertedCount()>1){
            return $insertmany->getInsertedIds();
        }else{
            return FALSE;
        }
    }

    //function to delete one record from the database
    function removeOne($collection_name,$values){
        $deleteone = $this->conn->$collection_name->deleteOne($values);
        printf("Deleted %d document(s)\n", $deleteone->getDeletedCount());
        if($deleteone->getDeletedCount()==1){
            return TRUE;
        }
        else{
            return FALSE;
        }

    }

    //function to delete many records from the database
    function removeMany($collection_name,$values){
        $deletemany = $this->conn->$collection_name->deleteMany($values);
        if($deletemany->getDeletedCount()>1){
            return TRUE;
        }
        else{
            return FALSE;
        }

    }

    //function to update one record from the database
    function modifyOne($collection_name,$from,$to){
        $updateone= $this->conn->$collection_name->updateOne($from,['$set'=>$to]);
        if($updateone->getModifiedCount()==1){
            return TRUE;
        }
        else{
            return FALSE;
        }
    }

    //function to update many records from the database
    function modifyMany($collection_name,$from,$to){
        $updatemany= $this->conn->$collection_name->updateMany($from,['$set'=>$to]);
        if($updatemany->getModifiedCount()>1){
            return TRUE;
        }
        else{
            return FALSE;
        }
    }

    //function to read from database and return array type
    function queryRecord($collection_name,$value,$options){
        $cursor = $this->conn->$collection_name->find($value,$options);
        return $cursor->toArray();
    }


}
?>