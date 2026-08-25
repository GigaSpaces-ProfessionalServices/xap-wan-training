#!/usr/bin/env bash
set -e

# See the matching comment in deploy-us.sh.
if [ ! -f /deploy/BillBuddySpaceEMEA.jar ]; then
  echo "ERROR: /deploy/BillBuddySpaceEMEA.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from wan-active-passive-topology/ and retry." >&2
  exit 1
fi
if [ ! -f /deploy/BillBuddyGatewayEMEA.jar ]; then
  echo "ERROR: /deploy/BillBuddyGatewayEMEA.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from wan-active-passive-topology/ and retry." >&2
  exit 1
fi

echo "Waiting for emea-manager REST..."
until curl -sf http://emea-manager:9090/api/v3/info > /dev/null 2>&1; do
  echo "  emea-manager not ready, retrying in 5s..."
  sleep 5
done

echo "Deploying EMEA space (BillBuddySpaceEMEA.jar)..."
/opt/gigaspaces/bin/gs.sh --server=emea-manager service deploy \
  --zones=EMEA-space \
  EMEA-space /deploy/BillBuddySpaceEMEA.jar

echo "Deploying EMEA gateway (BillBuddyGatewayEMEA.jar)..."
/opt/gigaspaces/bin/gs.sh --server=emea-manager service deploy \
  --zones=EMEA-gateway \
  EMEA-gateway /deploy/BillBuddyGatewayEMEA.jar

echo "EMEA deployment complete."
