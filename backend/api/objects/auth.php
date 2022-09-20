<?php
//This file will handle authentication features 
include_once 'database.php';
class Auth{
    private $key;
    private $iv;
    private $encryptionScheme;


    function __construct()
    {
        //password to use for encryption
        $this->key=base64_decode("TKgneppD3RDCndTJDJyzMQFR2BQZ3rUZtSZaLB4pJwI=");
        //initialization vector to use for encryption
        $this->iv=base64_decode("0LcRklpBwIH97gE150cXJQ==");
        $this->encryptionScheme="aes-256-cbc";
    }
    //function to encode the token
    public function Encode($data){
        $cipher=openssl_encrypt($data,$this->encryptionScheme,$this->key,$options=0,$this->iv);
        return $cipher;
    }

    //function to decrypt the cipher to get the token
    public function Decode($cipher){
        $decrypted=openssl_decrypt($cipher,$this->encryptionScheme,$this->key,$options=0,$this->iv);
        if($decrypted){
    	    $data=json_decode($decrypted);
	    if($this->isValid($data->IdNo)){
		    return $decrypted;
	    }
	    else{
		    return FALSE;
	    }
        }
        else{
            return FALSE;
        }
    }

    //function to check if is an admin

    public function isValid($idNo){
	$database=new Database();
	$usertables=['users','admin'];
   	$filter=['_id'=>$idNo];
      	$options=[];
	foreach ($usertables as $table){
		$result=$database->queryData($table,$filter,$options);
		if($result){
			return TRUE;
		}
	}
    }
}
?>
