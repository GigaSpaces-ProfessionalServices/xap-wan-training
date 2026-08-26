#!/usr/bin/env bash
set -e

# Guard against a real bug hit while validating this lab: if target/ was
# cleaned and docker compose started before a rebuild, Docker silently
# auto-creates the missing bind-mount source as an empty root-owned directory
# instead of erroring, and gs.sh will happily zip that empty directory into a
# valid-but-empty deploy artifact. Fail loudly here instead.
if [ ! -f /deploy/BillBuddySpace.jar ]; then
  echo "ERROR: /deploy/BillBuddySpace.jar not found." >&2
  echo "This usually means target/ was cleaned without rebuilding -- run 'mvn install' from lab05-wan_gateway_conflict-solution/ and retry." >&2
  exit 1
fi

echo "Waiting for us-manager REST..."
until curl -sf http://us-manager:9090/api/v3/info > /dev/null 2>&1; do
  echo "  us-manager not ready, retrying in 5s..."
  sleep 5
done

echo "Deploying US space (BillBuddySpace.jar)..."
/opt/gigaspaces/bin/gs.sh --server=us-manager service deploy \
  --zones=US-space \
  -p localSpaceName=wanSpaceUS \
  -p localGatewayName=US \
  -p remoteGatewayName=EMEA \
  US-space /deploy/BillBuddySpace.jar

echo "US space deployment complete. US-gateway is NOT deployed yet -- see"
echo "'docker compose --profile gateway up -d' after feeding both sites."
