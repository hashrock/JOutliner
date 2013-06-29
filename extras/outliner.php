<?
// Relative path to what the outliner thinks is the top level
// directory.  So, both "outline" and "outliner.php" should be in the
// same directory for the default settings.  Make sure to give the web
// server full r/w access to this directory.

$basedir = "outline";
$filename = $basedir . $HTTP_POST_VARS["path"];

if (isset($HTTP_POST_VARS["rename"]))
{
	// rename a file or directory

	$dest = ereg_replace("/[^/]+$", "/" . $HTTP_POST_VARS["rename"],
						 $filename);

	if (rename($filename, $dest)) {
		print "1";
	} else {
		print "0";
	}

	exit;
}
else if (isset($HTTP_POST_VARS["mkdir"]))
{
	// create a new directory

	if (mkdir($filename, 0777)) {
		print "1";
	} else {
		print "0";
	}

	exit;
}
else if (isset($HTTP_POST_VARS["exists"]))
{
	// does the file exist

	if (file_exists($filename)) {
		print "1";
	} else {
		print "0";
	}
	exit;
}
else if (isset($HTTP_POST_VARS["open"]))
{
	// open a file 

	$fd = fopen ($filename, "r");
	if ($fd)
	{
		while (!feof ($fd)) {
			print stripslashes(str_replace(chr(13),"", fgets($fd, 4096)));
		}
		fclose ($fd);
	}
	exit;
}
else if (isset($HTTP_POST_VARS["save"]))
{
	// save a file

	$save = str_replace(chr(13), "", $save);
	$fd=fopen($filename, "w");
	if($fd){
		// create an unbuffered stream
		set_file_buffer($fd, 0);
		fwrite($fd, $save, strlen($save));
		fclose($fd);
		print "1";
	} else {
		print "0";
	}
	exit;
}
else
{
	// list the files in the directory
		  
	$dir = opendir($filename);
	while($name = readdir($dir))
	{
		// ignore all files that start with "."
		if (ereg("^\\.", $name)) {
			continue;
		}

		if (is_dir("$filename/$name")) {
			print "$name/\n";
		} else {
			print "$name\n";
		}
	}

	closedir($dir);
}
?>
