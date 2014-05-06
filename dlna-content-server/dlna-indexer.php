<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html;charset=iso-8859-1" /> 
  <?php 
  error_reporting(E_ERROR);
  include("Telnet.php"); 
  ?>
</head>

<body>

<?php
$requestMsg = $_POST['request'];
$responseMsg = 'Welcome';

try {
  
  $telnet = new Telnet('192.168.0.12', 4444);
  $telnet->setPrompt("\n");

  if (! isset($requestMsg) ) $requestMsg = "getStatus"; 
  
  $responseMsg = $telnet->exec($requestMsg);
}
catch (Exception $e){
  $responseMsg = 'Exception: '.$e->getMessage();
}

$responseMsg = str_replace("\r\n", '', $responseMsg);
?>

  <form action="" method="POST">
  <?php 
    if ($responseMsg == "stopped") {
  ?>
    <div>
  		Start indexing <input type="submit" name="request" value="startIndexing"/>
  	</div>
  <?php
    }
    else if ($responseMsg == "started"){ 
  ?>
  	<div>
  		Stop indexing <input type="submit" name="request" value="stopIndexing"/>
  	</div>
  <?php
  }
  ?>	
  	<div>
  		Clear catalog <input type="submit" name="request" value="clearDatabase"/>
  	</div>
  
  </form>

</body>
</html>