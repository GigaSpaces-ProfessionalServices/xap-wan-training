#!/usr/bin/env bash

# Run this on the siteA ({{siteA.gatewayName}}) node, after start-grid-siteA.sh.
# Expects to be run from the bin/ of an extracted wan-gateway-node.tar.gz (built
# by `mvn install` via the wan-distribution module) - reads jars from ../lib,
# a sibling of bin/.

SCRIPTS_DIR="`dirname \"$0\"`"
SCRIPTS_DIR="`( cd \"$SCRIPTS_DIR\" && pwd )`"

source $SCRIPTS_DIR/settings.sh

PROJECT_DIR="`( cd \"$SCRIPTS_DIR/..\" && pwd )`"
SPACE_JAR="$PROJECT_DIR/lib/wan-space.jar"
GATEWAY_JAR="$PROJECT_DIR/lib/wan-gateway.jar"

echo "GS_HOME is: $GS_HOME"

# --server must be explicit: this node's manager REST endpoint only binds its own
# NIC address (GS_NIC_ADDRESS), not localhost - the CLI's implicit localhost:8090
# default doesn't reach it, so every gs.sh call here (and in verify.sh) targets it
# by address explicitly rather than relying on GS_MANAGER_SERVERS, which only
# affects where GSAs/GSCs register at start-grid time, not this REST client.

$GS_HOME/bin/gs.sh --server=$SITE_A_HOST service deploy \
  --zones=${SITE_A_NAME}-space \
  -p localSpaceName=$SITE_A_SPACE_NAME \
  -p localGatewayName=$SITE_A_NAME \
  -p remoteGatewayName=$SITE_B_NAME \
                                             ${SITE_A_NAME}-space $SPACE_JAR

$GS_HOME/bin/gs.sh --server=$SITE_A_HOST service deploy \
  --zones=${SITE_A_NAME}-gateway \
  -p localGatewayName=$SITE_A_NAME \
  -p remoteGatewayName=$SITE_B_NAME \
  -p localSpaceUrl=jini://*/*/$SITE_A_SPACE_NAME \
  -p localLookupHost=$SITE_A_HOST \
  -p localLookupPort=$GATEWAY_DISCOVERY_PORT \
  -p localCommunicationPort=$GATEWAY_COMMUNICATION_PORT \
  -p remoteLookupHost=$SITE_B_HOST \
  -p remoteLookupPort=$GATEWAY_DISCOVERY_PORT \
  -p remoteCommunicationPort=$GATEWAY_COMMUNICATION_PORT \
                                             ${SITE_A_NAME}-gateway $GATEWAY_JAR
