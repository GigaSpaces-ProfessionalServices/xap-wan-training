#!/usr/bin/env bash
set -e

# See the matching comment in deploy-us.sh.
if [ ! -f /deploy/BillBuddySpaceEMEA.jar ]; then
  echo "ERROR: /deploy/BillBuddySpaceEMEA.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from lab04-wan_gateway_filter-solution/ and retry." >&2
  exit 1
fi
if [ ! -f /deploy/BillBuddyGateway.jar ]; then
  echo "ERROR: /deploy/BillBuddyGateway.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from lab04-wan_gateway_filter-solution/ and retry." >&2
  exit 1
fi

echo "Waiting for emea-manager REST..."
until curl -sf http://emea-manager:9090/api/v3/info > /dev/null 2>&1; do
  echo "  emea-manager not ready, retrying in 5s..."
  sleep 5
done

echo "Deploying EMEA space (BillBuddySpaceEMEA.jar, no replication filter)..."
/opt/gigaspaces/bin/gs.sh --server=emea-manager service deploy \
  --zones=EMEA-space \
  EMEA-space /deploy/BillBuddySpaceEMEA.jar

echo "Deploying EMEA gateway (BillBuddyGateway.jar)..."
/opt/gigaspaces/bin/gs.sh --server=emea-manager service deploy \
  --zones=EMEA-gateway \
  -p localGatewayName=EMEA \
  -p remoteGatewayName=US \
  -p localSpaceUrl=jini://*/*/wanSpaceEMEA \
  -p localLookupHost=emea-gateway-gsc \
  -p localLookupPort=4174 \
  -p localCommunicationPort=8201 \
  -p remoteLookupHost=us-gateway-gsc \
  -p remoteLookupPort=4174 \
  -p remoteCommunicationPort=8201 \
  EMEA-gateway /deploy/BillBuddyGateway.jar

echo "EMEA deployment complete."
