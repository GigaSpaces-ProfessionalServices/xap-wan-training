#!/bin/bash
#

cd ../../deploy
# PLEASE replace localhost with relevant HOSTNAME in production

export GS_LOOKUP_LOCATORS=localhost:4266
${GS_HOME}/bin/gs.sh --cli-version=1 deploy -zones US wan-space-US
${GS_HOME}/bin/gs.sh --cli-version=1 deploy -zones US wan-gateway-US

