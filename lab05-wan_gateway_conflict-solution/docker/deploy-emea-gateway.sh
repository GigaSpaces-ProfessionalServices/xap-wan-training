#!/usr/bin/env bash
set -e

# See the matching comment in deploy-us-space.sh.
if [ ! -f /deploy/BillBuddyGateway.jar ]; then
  echo "ERROR: /deploy/BillBuddyGateway.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from lab05-wan_gateway_conflict-solution/ and retry." >&2
  exit 1
fi

echo "Waiting for emea-manager REST..."
until curl -sf http://emea-manager:9090/api/v3/info > /dev/null 2>&1; do
  echo "  emea-manager not ready, retrying in 5s..."
  sleep 5
done

echo "Deploying EMEA gateway (BillBuddyGateway.jar, with conflict resolver)..."
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

echo "EMEA gateway deployment complete."
