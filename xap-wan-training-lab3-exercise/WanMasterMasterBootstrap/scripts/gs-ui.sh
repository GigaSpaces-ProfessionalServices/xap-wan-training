#!/bin/bash
#

export GS_LOOKUP_GROUPS="US,EMEA"
export GS_LOOKUP_LOCATORS=127.0.0.1:4266,127.0.0.1:4166
command_line=$*
${GS_HOME}/bin/gs-ui.sh $command_line
