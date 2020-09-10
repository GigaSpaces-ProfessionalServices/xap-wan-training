#!/bin/bash
#
export GS_LOOKUP_GROUPS="EMEA"
# PLEASE replace localhost with relevant HOSTNAME in production
export GS_LOOKUP_LOCATORS=localhost:4166
export EXT_JAVA_OPTIONS="$EXT_JAVA_OPTIONS -Dcom.sun.jini.reggie.initialUnicastDiscoveryPort=4166"
export EXT_JAVA_OPTIONS="$EXT_JAVA_OPTIONS -Dcom.gigaspaces.system.registryPort=10098"
export EXT_JAVA_OPTIONS="$EXT_JAVA_OPTIONS -Dcom.gigaspaces.start.httpPort=9813"
export EXT_JAVA_OPTIONS="$EXT_JAVA_OPTIONS -Dcom.gs.zones=EMEA"

# Modify this as needed
export GS_GSC_OPTIONS=-Xmx128m
command_line='gsa.gsm 1 gsa.gsc 2'

${GS_HOME}/bin/gs-agent.sh $command_line