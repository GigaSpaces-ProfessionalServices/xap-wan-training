call setExampleEnv.bat

xcopy /E/I/F %~dp0..\..\deploy\wan-gateway-EMEA %GS_HOME%\deploy\wan-gateway-EMEA
xcopy /E/I/F %~dp0..\..\deploy\wan-space-EMEA %GS_HOME%\deploy\wan-space-EMEA

cd %GS_HOME%\bin
rem PLEASE replace 127.0.0.1 with relevant HOSTNAME in production
set GS_LOOKUP_LOCATORS=127.0.0.1:4166
call gs.bat --cli-version=1 deploy -zones EMEA wan-space-EMEA
call gs.bat --cli-version=1 deploy -zones EMEA wan-gateway-EMEA


