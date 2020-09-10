@echo off

set LAB_FOLDER=%~dp0\..

@call %GS_HOME%\bin\setenv.bat
set GS_LOOKUP_GROUPS="EMEA"
set GS_LOOKUP_LOCATORS=127.0.0.1:4166

"%JAVA_HOME%"\bin\java -cp %GS_JARS%;..\..\BillBuddyAccountFeeder\target\BillBuddyAccountFeeder.jar;..\..\BillBuddyModel\target\BillBuddyModel.jar; com.gigaspaces.training.billbuddy.client.AccountFeeder 127.0.0.1:4266 wanSpaceEMEA

