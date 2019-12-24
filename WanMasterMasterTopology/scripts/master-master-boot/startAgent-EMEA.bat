call setExampleEnv.bat

set GS_LOOKUP_GROUPS="EMEA"

rem PLEASE replace 127.0.0.1 with relevant HOSTNAME in production
set GS_LOOKUP_LOCATORS="127.0.0.1:4266"

set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.sun.jini.reggie.initialUnicastDiscoveryPort=4166
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.gigaspaces.system.registryPort=10098
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.gigaspaces.start.httpPort=9813
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.gs.zones=EMEA

rem Modify this as needed
set GS_GSC_OPTIONS=-Xmx128m

%GS_HOME%/bin/gs-agent.bat gsa.gsm 1 gsa.gsc 2
