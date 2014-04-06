#!/bin/sh
DIRECTORY=/share/HDA_DATA/.qpkg/dlnaServer
/share/HDA_DATA/.qpkg/JRE/jre.sh start
java -jar $DIRECTORY/dlna-server-0.1.jar
