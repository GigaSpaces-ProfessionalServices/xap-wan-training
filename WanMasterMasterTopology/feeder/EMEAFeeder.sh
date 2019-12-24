#!/bin/bash
#
export GS_LOOKUP_GROUPS="US"
# PLEASE replace localhost with relevant HOSTNAME in production
export GS_LOOKUP_LOCATORS=127.0.0.1:4166

SCRIPT_CP="-cp $GS_JARS:./feeder.jar"

java $SCRIPT_CP com.c123.billbuddy.client.AccountFeeder 127.0.0.1:4266 wanSpaceEMEA
