#!/usr/bin/env bash
set -e

# See the matching comment in deploy-us-space.sh.
if [ ! -f /deploy/BillBuddySpace.jar ]; then
  echo "ERROR: /deploy/BillBuddySpace.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from lab05-wan_gateway_conflict-solution/ and retry." >&2
  exit 1
fi

echo "Waiting for emea-manager REST..."
until curl -sf http://emea-manager:9090/api/v3/info > /dev/null 2>&1; do
  echo "  emea-manager not ready, retrying in 5s..."
  sleep 5
done

echo "Deploying EMEA space (BillBuddySpace.jar)..."
/opt/gigaspaces/bin/gs.sh --server=emea-manager service deploy \
  --zones=EMEA-space \
  -p localSpaceName=wanSpaceEMEA \
  -p localGatewayName=EMEA \
  -p remoteGatewayName=US \
  EMEA-space /deploy/BillBuddySpace.jar

echo "EMEA space deployment complete. EMEA-gateway is NOT deployed yet -- see"
echo "'docker compose --profile gateway up -d' after feeding both sites."
