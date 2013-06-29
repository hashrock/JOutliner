
Web File System
------------------------------------------------------------------------
Outliner can use any PHP enabled Web Server as a web file system.  This
allows you to open and save outlines from a web server, rather than the
local file system.  This is great if you work on outlines from multiple
locations and don't won't to have to email or ftp your files back and
forth.

There's just one PHP script, and it should work as an Apache Module,
or in CGI mode.  The code should work with and PHP3 or PHP4
installation, and has been tested in a limited way on both.

To enabled the web file system, you need to install the outliner.php
file anywhere on your web server and set the base directory, $basedir.
This is a relative path from the php script to the directory you want
used as the repository for all your outlines.  By default, this value
is "outline".  The simplest installation (Apache), is to copy
outliner.php to 

    webserver_dir/htdocs/outliner.php

and create the directory

    webserver_dir/htdocs/outline/

On unix systems, make sure to give the web server full write
permissions on the directory:

    webserver_dir/htdocs> chmod 777 outline


Security
------------------------------------------------------------------------
The web file system can be protected with HTTP Authentication.  This
way, only valid users will have access to your files.  On Apache, you
will need to create a password file and a .htaccess file that protects
the directory.  So your entire web server is not protected, you may
want to place the files in a subdirectory instead of the toplevel.
For example,

    webserver_dir/htdocs/pw/outliner.php
    webserver_dir/htdocs/pw/outliner/
    webserver_dir/htdocs/pw/.htaccess

Where .htaccess is:
#========================================
AuthUserFile /webserver_dir/pw.txt
AuthType Basic
AuthName Registration
<limit GET POST>
require valid-user
</limit>
#========================================

To create a new password file:

  htpasswd -cd /webserver_dir/pw.txt <username>

or to append to an already existing file

  htpasswd -d /webserver_dir/pw.txt <username>


NOTE 1: the password file should not be accessible by the web server.
Make sure this file is not under your "htdocs" directory.

NOTE 2: By default, apache is configured to ignore the .htaccess
file.  You may need to add something like this to your httpd.conf
file, below the Directory "<docroot>" setting, which sets AllowOverride
to "None":

<Directory "d:/apache/htdocs/pw/">
    AllowOverride FileInfo AuthConfig Limit
    Options MultiViews Indexes SymLinksIfOwnerMatch IncludesNoExec
    <Limit GET POST OPTIONS PROPFIND>
        Order allow,deny
        Allow from all
    </Limit>
    <LimitExcept GET POST OPTIONS PROPFIND>
        Order deny,allow
        Deny from all
    </LimitExcept>
</Directory>
