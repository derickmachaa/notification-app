<?php
class Database{
    	private $database;
	private $conn;

	function __construct(){
        $this->database=DB_NAME;
	echo DB_HOST;
        $pass=DB_USER;
        $user=DB_PASS;
        $host=DB_HOST;
        $port=DB_PORT;

        //Connecting to MongoDB
        if(empty($user && empty($pass))){
            $uri="mongodb://$host:$port/".$this->database;
        }
        if(!empty($user) && empty($pass)){
            $uri="mongodb://$user@$host:$port/".$this->database;
        }
        if(!empty($user) && !empty($pass)){
            $uri="mongodb://$user:$pass@$host:$port/".$this->database;
        }
        try {
			//Establish database connection
            $this->conn = new MongoDB\Driver\Manager($uri);
        }catch (MongoDBDriverExceptionException $e) {
            echo $e->getMessage();
			echo nl2br("n");
        }
    }

	function getConnection() {
		return $this->conn;
	}

    function createRecord($collection_name,$fields){
        //this function will be called multiple number of times to insert records according to the collection name specified and the fileds given
        $InsertRecord = new MongoDB\Driver\BulkWrite;
        $writeConcern = new MongoDB\Driver\WriteConcern(MongoDB\Driver\WriteConcern::MAJORITY, 100);
        $InsertRecord->insert($fields);
        try {
            $result=$this->conn->executeBulkWrite($this->database.'.'.$collection_name, $InsertRecord,$writeConcern);
        } catch (MongoDB\Driver\Exception\BulkWriteException $e) {
            $result = $e->getWriteResult();
            // Check if any write operations did not complete at all
            foreach ($result->getWriteErrors() as $writeError) {
                printf("Operation#%d: %s (%d)\n",
                $writeError->getIndex(),
                $writeError->getMessage(),
                $writeError->getCode());
            }
        }
        if($result->getInsertedCount()==1)
        {
            return TRUE;
        }else
        {
            return FALSE;
        }
    }

    function deleteRecord($collection_name,$field,$option){
        //this function deletes a record in the db according to the field
        $DeleteRecord = new MongoDB\Driver\BulkWrite;
        $DeleteRecord->delete($field,$option);
        $result = $this->conn->executeBulkWrite($this->database.'.'.$collection_name,$DeleteRecord);
        if($result->getDeletedCount()>0)
        {
            return TRUE;
        }else
        {
            return FALSE;
        }
    }
    
    function queryData($collection_name,$filter,$option){
        //this function executes a query and returns the result as an array
        $read = new MongoDB\Driver\Query($filter, $option);
        $records = $this->conn->executeQuery($this->database.'.'.$collection_name, $read);
        return $records->toArray();
    }
    
    function updateOne($collection_name,$filter,$values){
        //this function changes a database record to the values given
        $update = new MongoDB\Driver\BulkWrite;
        $writeConcern = new MongoDB\Driver\WriteConcern(MongoDB\Driver\WriteConcern::MAJORITY, 100);
        $update->update($filter,$values);
        $result = $this->conn->executeBulkWrite($this->database.'.'.$collection_name, $update,['multi'=>false,'upsert'=>false]);
        if($result->getModifiedCount() == 1){
            return TRUE;
        }
        else
        {
            return FALSE;
        }
    }
    function updateMany($collection_name,$filter,$values){
        //this function changes a database record to the values given
        $update = new MongoDB\Driver\BulkWrite;
        $writeConcern = new MongoDB\Driver\WriteConcern(MongoDB\Driver\WriteConcern::MAJORITY, 100);
        $update->update($filter,$values);
        $result = $this->conn->executeBulkWrite($this->database.'.'.$collection_name, $update,['multi'=>true,'upsert'=>false]);
        if($result->getModifiedCount() > 0){
            return TRUE;
        }
        else
        {
            return FALSE;
        }
    }
}
?>
