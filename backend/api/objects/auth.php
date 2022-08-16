<?php
//This file will handle authentication features 
class Auth{
    private $key;
    private $iv;
    private $encryptionScheme;


    function __construct()
    {
        $this->key=KEY;
        $this->iv="1578d6fc8db1221df16d79b324a647ca";
        $this->encryptionScheme="aes-128-cbc";
    }
    //function to encode the token
    public function Encode($data){
        $cipher=openssl_encrypt($data,$this->encryptionScheme,$this->key,$options=0,$this->iv);
        return base64_encode($cipher);
    }

    //function to decrypt the cipher to get the token
    public function Decode($cipher){
        $data=base64_decode($cipher);
        if($data){
            $output=openssl_decrypt($data,$this->encryptionScheme,$this->key,$options=0,$this->iv);
            if($output){return $output;}else{echo 'not decoded';}
        }else{
            return FALSE;
        }
    }

    //function to check if is an admin

    public function CheckRole($cipher){
        $decoded=$this->Decode($cipher);
        if($decoded){
            return $decoded['UserType'];
        }
    }
}
?>