#!/bin/bash
#
cd ../../deploy
# PLEASE replace localhost with relevant HOSTNAME in production
export GS_LOOKUP_LOCATORS="127.0.0.1:4266"
${GS_HOME}/bin/gs.sh deploy -zones EMEA wan-space-EMEA
${GS_HOME}/bin/gs.sh deploy -zones EMEA wan-gateway-EMEA
