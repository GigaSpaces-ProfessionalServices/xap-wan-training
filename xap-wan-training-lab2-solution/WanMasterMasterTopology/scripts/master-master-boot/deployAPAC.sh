#!/bin/bash
#
cd ../../deploy
# PLEASE replace localhost with relevant HOSTNAME in production
export GS_LOOKUP_LOCATORS="127.0.0.1:4366"
${GS_HOME}/bin/gs.sh --cli-version=1 deploy -zones APAC wan-space-APAC
${GS_HOME}/bin/gs.sh --cli-version=1 deploy -cluster total_members=2 -zones APAC wan-gateway-APAC
