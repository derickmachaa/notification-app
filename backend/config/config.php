<?php

//This file contains the global webstie configurations

//the root dir
define('ROOT', __DIR__ .'/../');

//database is mongo this section includes the mongo config files
define('DB_HOST','localhost'); //mongodb host
define('DB_PORT',27017);//mongo port
define('DB_NAME','machaa');//database name
//end mongo config

///fix cross-orig errors
define('URL','http://localhost');

//add encryption key for auth for testing
define('KEY','VGhpcyBpcyBteSB2ZXJ5IHNlY3VyZSBwYXNzd29yZCBmb3IgZW5jcnlwdGluZyBjb29raWVz');

?>