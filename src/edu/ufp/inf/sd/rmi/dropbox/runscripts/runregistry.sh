#!/usr/bin/env bash
#REM ************************************************************************************
#REM Description: start naming service (rmi registry)
#REM Author: Rui S. Moreira
#REM Date: 20/02/2014
#REM ************************************************************************************
source ./setenv.sh
echo ${ABSPATH2CLASSES}
cd ${ABSPATH2CLASSES}
#clear
#pwd
#rmiregistry &
rmiregistry