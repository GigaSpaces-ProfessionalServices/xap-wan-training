#!/bin/bash
#
export GS_LOOKUP_GROUPS="APAC"
# PLEASE replace localhost with relevant HOSTNAME in production
export GS_LOOKUP_LOCATORS="127.0.0.1:4366"
export GS_OPTIONS_EXT="$GS_OPTIONS_EXT -Dcom.sun.jini.reggie.initialUnicastDiscoveryPort=4366"
export GS_OPTIONS_EXT="$GS_OPTIONS_EXT -Dcom.gigaspaces.system.registryPort=10298"
export GS_OPTIONS_EXT="$GS_OPTIONS_EXT -Dcom.gigaspaces.start.httpPort=10013"
export GS_OPTIONS_EXT="$GS_OPTIONS_EXT -Dcom.gs.zones=APAC"

# Modify this as needed
export GS_GSC_OPTIONS=-Xmx128m
command_line='gsa.gsm 1 gsa.lus 1 gsa.gsc 2'

${GS_HOME}/bin/gs-agent.sh $command_line
