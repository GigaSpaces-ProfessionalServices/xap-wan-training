#!/usr/bin/env bash

# Run from either node (or anywhere that can reach both managers) to check how
# many WanEvent documents each site's space currently holds.
#
#   ./verify.sh siteA
#   ./verify.sh siteB

SCRIPTS_DIR="`dirname \"$0\"`"
SCRIPTS_DIR="`( cd \"$SCRIPTS_DIR\" && pwd )`"

source $SCRIPTS_DIR/settings.sh

if [ "$1" == "siteA" ]; then
  HOST="$SITE_A_HOST"
  SPACE_NAME="$SITE_A_SPACE_NAME"
elif [ "$1" == "siteB" ]; then
  HOST="$SITE_B_HOST"
  SPACE_NAME="$SITE_B_SPACE_NAME"
else
  echo "Usage: $0 siteA|siteB"
  exit 1
fi

$GS_HOME/bin/gs.sh --server=$HOST space query $SPACE_NAME WanEvent \
  --max-results=2000 --columns=id,originSite
