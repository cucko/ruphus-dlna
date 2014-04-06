<?php
$requestParam = $_POST['request'];
$responseMsg = 'Welcome';	
	
function sendRequest($in){
	error_reporting(E_ALL);
	
	$socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP);
	
	if (!$socket) $responseMsg = "socket_create() failed. Reason: " . socket_strerror(socket_last_error($socket));
	else {
		$result = socket_connect($socket, "127.0.0.1", "4444");
		if (!$result) $responseMsg = "socket_connect() failed. Reason: " . socket_strerror(socket_last_error($socket));
		else {
			socket_set_nonblock($socket);
			
			$sent = socket_write($socket, $in, strlen($in));
			if (!$sent) $responseMsg = "Request not sent. Reason: " . socket_strerror(socket_last_error($socket));
			else {
				
				while ($resp = socket_read($socket, 2048)) {
				   $responseMsg .= $resp;
				   if (strpos($responseMsg, "\n") !== false) break;
				}
			}
		}
		
		socket_close($socket);
	}
	
	return $responseMsg;
}


if ( isset($requestParam) ) {
	if ($requestParam == 'startIndexing'){
		echo exec('content-indexer.sh');
	}
	else $responseMsg = sendRequest($requestParam);
}
?>


<html>
<head>

</head>
	
<body>

	<span style="font-weight:bold"><?=$responseMsg ?></span>

	<form action="" method="POST">
		<div>
			Start indexing <input type="submit" name="request" value="startIndexing"/>
		</div>
		
		<div>
			Stop indexing <input type="submit" name="request" value="stopIndexing"/>
		</div>
		
		<div>
			Clear catalog <input type="submit" name="request" value="clearDatabase"/>
		</div>
	
		<!--div>
			Refresh every 
			<input type="text" name="reindex.seconds" value="" /> 
			seconds.
			<input type="submit" name="request" value=""/>
		</div-->
		
	</form>
</body>

</html>