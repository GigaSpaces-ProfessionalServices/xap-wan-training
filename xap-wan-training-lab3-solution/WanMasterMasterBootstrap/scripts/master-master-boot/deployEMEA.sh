#!/bin/bash
#
cd ../../deploy
# PLEASE replace localhost with relevant HOSTNAME in production
export GS_LOOKUP_LOCATORS=localhost:4166
${GS_HOME}/bin/gs.sh --cli-version=1 deploy -zones EMEA wan-space-EMEA
${GS_HOME}/bin/gs.sh --cli-version=1 deploy -zones EMEA wan-gateway-EMEA
