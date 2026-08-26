#!/usr/bin/env bash
set -e

# Guard against a real bug hit while validating this lab: if target/ was
# cleaned and docker compose started before a rebuild, Docker silently
# auto-creates the missing bind-mount source as an empty root-owned directory
# instead of erroring, and gs.sh will happily zip that empty directory into a
# valid-but-empty deploy artifact. Fail loudly here instead.
if [ ! -f /deploy/BillBuddySpaceUS.jar ]; then
  echo "ERROR: /deploy/BillBuddySpaceUS.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from lab04-wan_gateway_filter-solution/ and retry." >&2
  exit 1
fi
if [ ! -f /deploy/BillBuddyGateway.jar ]; then
  echo "ERROR: /deploy/BillBuddyGateway.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from lab04-wan_gateway_filter-solution/ and retry." >&2
  exit 1
fi

echo "Waiting for us-manager REST..."
until curl -sf http://us-manager:9090/api/v3/info > /dev/null 2>&1; do
  echo "  us-manager not ready, retrying in 5s..."
  sleep 5
done

echo "Deploying US space (BillBuddySpaceUS.jar, with replication filter)..."
/opt/gigaspaces/bin/gs.sh --server=us-manager service deploy \
  --zones=US-space \
  US-space /deploy/BillBuddySpaceUS.jar

echo "Deploying US gateway (BillBuddyGateway.jar)..."
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

echo "US deployment complete."
