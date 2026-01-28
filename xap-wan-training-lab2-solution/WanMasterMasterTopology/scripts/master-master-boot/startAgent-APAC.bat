@echo off
rem
set GS_LOOKUP_GROUPS=APAC
rem PLEASE replace localhost with relevant HOSTNAME in production
set GS_LOOKUP_LOCATORS=127.0.0.1:4366
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.sun.jini.reggie.initialUnicastDiscoveryPort=4366
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.gigaspaces.system.registryPort=10298
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.gigaspaces.start.httpPort=10013
set GS_OPTIONS_EXT=%GS_OPTIONS_EXT% -Dcom.gs.zones=APAC

rem Modify this as needed
set GS_GSC_OPTIONS=-Xmx128m
set command_line=gsa.gsm 1 gsa.lus 1 gsa.gsc 2

call %GS_HOME%\bin\gs-agent.bat %command_line%
