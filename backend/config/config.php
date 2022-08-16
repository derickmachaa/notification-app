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

?>