call setExampleEnv.bat

xcopy /E/I/F  %~dp0..\..\deploy\wan-gateway-US %GS_HOME%\deploy\wan-gateway-US
xcopy /E/I/F  %~dp0..\..\deploy\wan-space-US %GS_HOME%\deploy\wan-space-US
xcopy /E/I/F ..\..\..\ConflictResolution\target\classes\com ..\..\deploy\wan-space-US

cd %GS_HOME%\bin
rem PLEASE replace 127.0.0.1 with relevant HOSTNAME in production
set GS_LOOKUP_LOCATORS=127.0.0.1:4266
call gs.bat --cli-version=1 deploy -zones US wan-space-US
call gs.bat --cli-version=1 deploy -zones US wan-gateway-US