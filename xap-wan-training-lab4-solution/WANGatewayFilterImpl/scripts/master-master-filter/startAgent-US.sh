#!/bin/bash
#
export GS_LOOKUP_GROUPS="US"
# PLEASE replace localhost with relevant HOSTNAME in production
export GS_LOOKUP_LOCATORS="127.0.0.1:4266"
export GS_OPTIONS_EXT="${GS_OPTIONS_EXT} -Dcom.sun.jini.reggie.initialUnicastDiscoveryPort=4266"
export GS_OPTIONS_EXT="${GS_OPTIONS_EXT} -Dcom.gigaspaces.system.registryPort=10198"
export GS_OPTIONS_EXT="${GS_OPTIONS_EXT} -Dcom.gigaspaces.start.httpPort=9913"
export GS_OPTIONS_EXT="${GS_OPTIONS_EXT} -Dcom.gs.zones=US"

# Modify this as needed
export GS_GSC_OPTIONS=-Xmx128m

command_line='gsa.gsm 1 gsa.gsc 2'

${GS_HOME}/bin/gs-agent.sh $command_line