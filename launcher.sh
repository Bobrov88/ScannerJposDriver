#!/bin/bash

echo
echo
echo "Please wait while loading the JavaPos POSTest application ....."
echo
echo

cd "$(dirname "$0")"
oldcp=$CLASSPATH
CLASSPATH="DriverTest-1.0.jar"
CLASSPATH="$CLASSPATH:jpos1141.jar"
CLASSPATH="$CLASSPATH:xerces.jar"
CLASSPATH="$CLASSPATH:jssc-2.9.6.jar"
CLASSPATH="$CLASSPATH:JposDriver.jar"
CLASSPATH="$CLASSPATH:log4j-1.2.17.jar"
CLASSPATH="$CLASSPATH:json-simple-1.1.1.jar"
java -XX:+ShowMessageBoxOnError -cp "$CLASSPATH" test.PosTestMainWindow

CLASSPATH=$oldcp