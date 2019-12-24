@echo off

set LAB_FOLDER=%~dp0\..

@call %GS_HOME%\bin\setenv.bat
set GS_LOOKUP_GROUPS="EMEA"
set GS_LOOKUP_LOCATORS="127.0.0.1:4266"

"%JAVA_HOME%"\bin\java -cp %GS_JARS%;feeder.jar; com.c123.billbuddy.client.AccountFeeder 127.0.0.1:4166 wanSpaceEMEA

