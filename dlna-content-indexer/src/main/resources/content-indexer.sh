#!/bin/sh
DIRECTORY=/share/HDA_DATA/.qpkg/dlnaIndexer
/share/HDA_DATA/.qpkg/JRE/jre.sh start
java -jar $DIRECTORY/content-indexer-0.1.jar $DIRECTORY/indexer.properties 4444
