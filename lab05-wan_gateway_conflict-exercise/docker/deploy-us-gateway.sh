#!/usr/bin/env bash
set -e

# See the matching comment in deploy-us-space.sh.
if [ ! -f /deploy/BillBuddyGateway.jar ]; then
  echo "ERROR: /deploy/BillBuddyGateway.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from lab05-wan_gateway_conflict-solution/ and retry." >&2
  exit 1
fi

echo "Waiting for us-manager REST..."
until curl -sf http://us-manager:9090/api/v3/info > /dev/null 2>&1; do
  echo "  us-manager not ready, retrying in 5s..."
  sleep 5
done

echo "Deploying US gateway (BillBuddyGateway.jar, with conflict resolver)..."
/opt/gigaspaces/bin/gs.sh --server=us-manager service deploy \
  --zones=US-gateway \
  -p localGatewayName=US \
  -p remoteGatewayName=EMEA \
  -p localSpaceUrl=jini://*/*/wanSpaceUS \
  -p localLookupHost=us-gateway-gsc \
  -p localLookupPort=4174 \
  -p localCommunicationPort=8201 \
  -p remoteLookupHost=emea-gateway-gsc \
  -p remoteLookupPort=4174 \
  -p remoteCommunicationPort=8201 \
  US-gateway /deploy/BillBuddyGateway.jar

echo "US gateway deployment complete."
