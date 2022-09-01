<?php

//This file contains the global webstie configurations

//the root dir
define('ROOT', __DIR__ .'/../');

//database is mongo this section includes the mongo config files
define('DB_HOST','localhost'); //mongodb host
define('DB_PORT',27017);//mongo port
define('DB_NAME','notificationsystem');//database name
define('DB_USER','Admin');
define('DB_PASS','CueaNotificationSystem');
//end mongo config

///fix cross-orig errors
define('URL','http://localhost');

//set the default time zone for a better clock
date_default_timezone_set('Africa/Nairobi');
?>