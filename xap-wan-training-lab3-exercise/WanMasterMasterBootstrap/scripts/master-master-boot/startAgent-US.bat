call setExampleEnv.bat

set GS_LOOKUP_GROUPS="US"

rem PLEASE replace 127.0.0.1 with relevant HOSTNAME in production
set GS_LOOKUP_LOCATORS=127.0.0.1:4266

set EXT_JAVA_OPTIONS=%EXT_JAVA_OPTIONS% -Dcom.sun.jini.reggie.initialUnicastDiscoveryPort=4266
set EXT_JAVA_OPTIONS=%EXT_JAVA_OPTIONS% -Dcom.gigaspaces.system.registryPort=10198
set EXT_JAVA_OPTIONS=%EXT_JAVA_OPTIONS% -Dcom.gigaspaces.start.httpPort=9913
set EXT_JAVA_OPTIONS=%EXT_JAVA_OPTIONS% -Dcom.gs.zones=US

rem Modify this as needed
set GS_GSC_OPTIONS=-Xmx128m

%GS_HOME%/bin/gs-agent.bat gsa.gsm 1 gsa.gsc 2
