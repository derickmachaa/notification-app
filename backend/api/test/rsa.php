<?php

if (isset($_SERVER['HTTPS']) )
{
    echo "SECURE: This page is being accessed through a secure connection.<br><br>";
}
else
{
    echo "UNSECURE: This page is being access through an unsecure connection.<br><br>";
}


$config = array(
    "digest_alg" => "sha512",
    "private_key_bits" => 512,
    "private_key_type" => OPENSSL_KEYTYPE_RSA,
);

// Create the keypair
$res=openssl_pkey_new($config);

// Get private key
openssl_pkey_export($res, $privatekey);

// Get public key
$publickey=openssl_pkey_get_details($res);
$publickey=$publickey["key"];

echo "Private Key:<BR>$privatekey<br><br>Public Key:<BR>$publickey<BR><BR>";

$cleartext = '1234 5678 9012 3456';

echo "Clear text:<br>$cleartext<BR><BR>";

openssl_public_encrypt($cleartext, $crypttext, $publickey);

print_r(base64_encode($crypttext));

openssl_private_decrypt($crypttext, $decrypted, $privatekey);

echo "Decrypted text:<BR>$decrypted<br><br>";
?>

