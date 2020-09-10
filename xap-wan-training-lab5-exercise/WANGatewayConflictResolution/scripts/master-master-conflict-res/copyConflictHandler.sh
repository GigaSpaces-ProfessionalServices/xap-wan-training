#!/bin/bash
#

rm -rf ../../deploy/wan-gateway-US/com/
rm -rf ../../deploy/wan-gateway-EMEA/com/
cp -r ../../../ConflictResolution/target/classes/com ../../deploy/wan-gateway-US
cp -r ../../../ConflictResolution/target/classes/com ../../deploy/wan-gateway-EMEA