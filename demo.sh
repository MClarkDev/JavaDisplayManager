#!/bin/bash

#
# Config
#

tvIP=127.0.0.1
tvPort=9999
tvPass=jdm

tvInterval=15000


#
# Auth
#

BLOB="jdm\n"


#
# Clear Exisitng
#

BLOB="$BLOB\nclear\n"


#
# General
#
BLOB="$BLOB\nc\n4,4\n"

BLOB="$BLOB\nn\n0\n1,1\ninfo|#000000,#FFFFFF,false,_,Hello World,_\n"
BLOB="$BLOB\nn\n0\n2,1\nclockd|#222222,#999999\n"
BLOB="$BLOB\nn\n0\n2,1\nclocka|#222222,#999999,#BBBBBB\n"
BLOB="$BLOB\nn\n0\n3,0\ndate|#000000,#FFFFFF\n"
BLOB="$BLOB\nn\n0\n1,2\nstock|DWRE\n"
BLOB="$BLOB\nn\n0\n0,0\nweb|http://www.mclarkdev.com/jdm/\n"


#
# Settings
#
BLOB="$BLOB\ngoto\n0\n"
BLOB="$BLOB\ni\n$tvInterval\n"
BLOB="$BLOB\nfs\n"


#
# Send to server
#

BLOB="$BLOB\n"
echo -e $BLOB
echo -e "$BLOB" | nc $tvIP $tvPort

