<?php

class Video {
	public $size;
	public $path;
	public $subsName;
	public $subsSize;
	public $duration = '18000000';
	public $mimeType;
}

function getMillisDuration($duration){
	$durationTokens = date_parse_from_format('H:i:s.u', $duration);
	return (
		$durationTokens['hour'] * 3600 + 
		$durationTokens['minute'] * 60 + 
		$durationTokens['second'] + 
		$durationTokens['fraction']
	) * 1000;
}

$item = NULL;
$requestHeaders = apache_request_headers();
$userAgent = $requestHeaders['User-Agent'];

$mediumTable = $_GET['table'];
$mediumId = $_GET['id'];


if ( is_null($mediumTable) or is_null($mediumId) ) die('Parameter missing');

$mediumId = substr($mediumId, 0, strpos($mediumId, "."));


function userAgentIsNotLGTV(){
	global $userAgent;
	return strpos($userAgent, 'LG NetCast.TV') !== false;
}

function parseDLNAContentFeatures(){
	global $requestHeaders;

	if ( '1' == $requestHeaders['getcontentfeatures.dlna.org'] ){
		header('contentfeatures.dlna.org: DLNA.ORG_OP=01;DLNA.ORG_CI=0;DLNA.ORG_FLAGS=01500000000000000000000000000000');
	}
}

function doHead(){
	global $requestHeaders, $userAgent, $item, $mediumTable;
	
	if (is_null($userAgent) or userAgentIsNotLGTV()){
		parseDLNAContentFeatures();
  
		if ('1' == $requestHeaders['getcaptioninfo.sec'] and ($item->subsName) ) {
			header('CaptionInfo.sec: http://192.168.0.3/ruphus-dlna/stream.php?table=' . $mediumTable . '&id=' . $item->id . '.srt');
		}
	}
}
   
function doGet(){
	global $requestHeaders, $userAgent, $item;
	  
	if (is_null($userAgent) or userAgentIsNotLGTV()){
	    header('EXT: ');
	  
	    parseDLNAContentFeatures();
	    
	    $transferMode = $requestHeaders['transfermode.dlna.org'];
	    if ($transferMode) header('transfermode.dlna.org: ' . $transferMode);
	    
	    if ('1' == $requestHeaders['getmediainfo.sec']) {
	      header('mediainfo.sec: SEC_Duration=' . $item->duration);
	    }
	}

	$fileHandle = fopen($item->path, 'rb');
  
	$range = $requestHeaders['range'];
	if ($range){
	    $startFrom = str_replace(array('bytes=', '-'), '', $range);
	    fseek($fileHandle, $startFrom);
	}
  
	fpassthru($fileHandle);
	fclose($fileHandle);

	exit;
}
function retrieveItem(){
	global $mediumTable, $mediumId;

	$link = mysql_connect('localhost', 'root', 'password');
	if (!$link) die('Could not connect: ' . mysql_error());
  
	$db = mysql_select_db('ruphus_dlna_catalog', $link);
	if (!$db) die('Could not select database: ' . mysql_error());
	
	$mediumQuery = "SELECT * FROM " . $mediumTable . " WHERE id = '" . $mediumId ."'";
	
	$risultato = mysql_query($mediumQuery, $link);
	if (!$risultato){
		mysql_close($link);
		die('Query non valida: ' . mysql_error());
	}
	
	$row = mysql_fetch_array($risultato);

	if ($mediumTable == 'videos'){
		$item = new Video();
		$item->subsName = $row['subtitles_title'];
		$item->subsSize = $row['subtitles_size'];
	}
	elseif ($mediumTable == 'songs'){
		$item = new Song();
	}
	elseif ($mediumTable == 'pictures'){
		$item = new Picture();
	} 
	
	$item->id = $row['id'];
	$item->size = $row['size']; 
	$item->path = $row['path'];
	$item->mimeType = $row['mime_type'];
	$item->duration = getMillisDuration( $row['duration'] );
	
	return $item;
}

$item = retrieveItem();

header('Cache-Control: no-cache');
header('Content-Length: ' . $item->size);
header('Content-Type: ' . $item->mimeType);
header('Content-Type: video/x-matroska');

$requestMethod = $_SERVER['REQUEST_METHOD'];
if ($requestMethod == 'HEAD') doHead();
elseif ($requestMethod == 'GET') doGet();

?>