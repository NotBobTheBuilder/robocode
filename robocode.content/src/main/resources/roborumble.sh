#!/bin/sh
pwd=`pwd`
cd "${0%/*}"
java -Xmx512M -cp libs/gson-2.2.4.jar:libs/robocode.jar:libs/roborumble.jar:libs/codesize-1.1.jar roborumble.RoboRumbleAtHome http://192.168.0.4:5000/
cd "${pwd}"
