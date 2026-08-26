#!/usr/bin/env bash

# Run this on the node designated as siteA ({{siteA.gatewayName}}).

SCRIPTS_DIR="`dirname \"$0\"`"
SCRIPTS_DIR="`( cd \"$SCRIPTS_DIR\" && pwd )`"

source $SCRIPTS_DIR/settings.sh

export GS_MANAGER_SERVERS="$SITE_A_HOST"

ORIGINAL_GS_GSC_OPTIONS="$GS_GSC_OPTIONS"

# Space side: a manager plus 4 GSCs (2 partitions x 1 backup, matching wan-space's
# sla.xml), zoned so only this space's PU can land on them.
export GS_GSC_OPTIONS="$ORIGINAL_GS_GSC_OPTIONS -Dcom.gs.zones=${SITE_A_NAME}-space"

nohup $GS_HOME/bin/gs.sh host run-agent --manager --gsc=4 > /tmp/space-${SITE_A_NAME}.log 2>&1 &

# Gateway side: a single dedicated GSC in its own agent, with its own embedded LUS
# discovery/communication ports so it doesn't collide with the space agent above -
# these MUST match the localLookupPort/localCommunicationPort passed at deploy time.
export GS_GSC_OPTIONS="$ORIGINAL_GS_GSC_OPTIONS \
  -Dcom.sun.jini.reggie.initialUnicastDiscoveryPort=$GATEWAY_DISCOVERY_PORT \
  -Dcom.gs.transport_protocol.lrmi.bind-port=$GATEWAY_COMMUNICATION_PORT \
  -Dcom.gs.zones=${SITE_A_NAME}-gateway"

nohup $GS_HOME/bin/gs.sh host run-agent --gsc=1 > /tmp/gateway-${SITE_A_NAME}.log 2>&1 &
