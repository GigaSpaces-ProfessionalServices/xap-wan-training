call setExampleEnv.bat

set GS_LOOKUP_GROUPS="US"

rem PLEASE replace 127.0.0.1 with relevant HOSTNAME in production
set GS_LOOKUP_LOCATORS=127.0.0.1:4266

set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.sun.jini.reggie.initialUnicastDiscoveryPort=4266
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.gigaspaces.system.registryPort=10198
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.gigaspaces.start.httpPort=9913
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.gs.zones=US

rem Modify this as needed
set GS_GSC_OPTIONS=-Xmx128m

%GS_HOME%/bin/gs-agent.bat gsa.gsm 1 gsa.gsc 2
