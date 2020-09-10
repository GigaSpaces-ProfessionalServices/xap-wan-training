call setExampleEnv.bat

RMDIR /S /Q %~dp0..\..\deploy\wan-gateway-US\com\
RMDIR /S /Q %~dp0..\..\deploy\wan-gateway-EMEA\com\
xcopy /E %~dp0..\..\target\classes\com\* %~dp0..\..\deploy\wan-gateway-US\com\
xcopy /E %~dp0..\..\target\classes\com\* %~dp0..\..\deploy\wan-gateway-EMEA\com\

