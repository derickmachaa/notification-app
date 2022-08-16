<?php
$plaintext = "message to be encrypted";
$cipher = "aes-128-gcm";
$key="";
if (in_array($cipher, openssl_get_cipher_methods()))
{
    $ivlen = openssl_cipher_iv_length($cipher);
    $iv ="933f4249e37e0b0c659032ae9944edfa";
    $ciphertext = openssl_encrypt($plaintext, $cipher, $key, $options=0, $iv, $tag);
    echo base64_encode($ciphertext);
    $ciphertext=base64_decode('VnpCSGxRcE9pa3RLdzJsZGdRQXd1OWV1WmpJdUZZTT0');
    //store $cipher, $iv, and $tag for decryption later
    $original_plaintext = openssl_decrypt($ciphertext, $cipher, $key, $options=0, $iv, $tag);
    echo $original_plaintext."\n";
}
?>

