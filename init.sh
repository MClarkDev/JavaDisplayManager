#!/bin/bash

dmServer=127.0.0.1
dmPort=9999
dmPass=jdm

GLOB=""

# Add first display
GLOB="$GLOB$dmPass\naddDisplay\n1,1\n\n"

# Add a simple text panel
GLOB="$GLOB$dmPass\naddPanel\n1\n1,1\nInfo\n\n"
GLOB="$GLOB$dmPass\nupdatePanel\n1\n1,1\n#000000|Welcome to JDM|Demo\n\n"

# Send to the server
echo -e "$GLOB" | nc $dmServer $dmPort
