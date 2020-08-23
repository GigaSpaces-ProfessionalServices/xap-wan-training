#!/bin/bash

export GS_LOOKUP_GROUPS="US"
export GS_JARS=${GS_HOME}/lib/required/*
# PLEASE replace localhost with relevant HOSTNAME in production
export GS_LOOKUP_LOCATORS=127.0.0.1:4166

SCRIPT_CP="-cp $GS_JARS:../../BillBuddyAccountFeeder/target/BillBuddyAccountFeeder.jar\
:../../BillBuddyModel/target/BillBuddyModel.jar"

java $SCRIPT_CP com.gigaspaces.training.billbuddy.client.AccountFeeder 127.0.0.1:4166 wanSpaceEMEA