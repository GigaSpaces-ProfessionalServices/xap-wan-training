call setExampleEnv.bat

set GS_LOOKUP_GROUPS=EMEA,US

rem PLEASE replace 127.0.0.1 with relevant HOSTNAME in production
set GS_LOOKUP_LOCATORS=127.0.0.1:4166,127.0.0.1:4266

%GS_HOME%/bin/gs-ui.bat