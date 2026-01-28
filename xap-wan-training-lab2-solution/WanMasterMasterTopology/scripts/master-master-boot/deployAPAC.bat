@echo off
rem
cd ..\..\deploy
rem PLEASE replace localhost with relevant HOSTNAME in production
set GS_LOOKUP_LOCATORS=127.0.0.1:4366
call %GS_HOME%\bin\gs.bat --cli-version=1 deploy -zones APAC wan-space-APAC
call %GS_HOME%\bin\gs.bat --cli-version=1 deploy -cluster total_members=2 -zones APAC wan-gateway-APAC
