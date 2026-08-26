#!/usr/bin/env bash

# Run from either node (or anywhere that can reach the target site's manager) to
# write WanEvent documents into one site's space via wan-feeder.jar, then watch
# them replicate to the other side with verify.sh.
#
#   ./feed.sh siteA [count]
#   ./feed.sh siteB [count]
#
# If this can't find the space (FinderException/CannotFindSpaceException), check
# GS_OPTIONS_EXT for -Dcom.gs.smart-externalizable.enabled=false on the grid -
# see the lab README's "Feeder connectivity" section for why that flag (a
# leftover workaround from older XAP versions, not needed on 17.3.0) is the
# likely cause if it's still set anywhere.

SCRIPTS_DIR="`dirname \"$0\"`"
SCRIPTS_DIR="`( cd \"$SCRIPTS_DIR\" && pwd )`"

source $SCRIPTS_DIR/settings.sh

if [ "$1" == "siteA" ]; then
  HOST="$SITE_A_HOST"
  SITE_NAME="$SITE_A_NAME"
  SPACE_NAME="$SITE_A_SPACE_NAME"
elif [ "$1" == "siteB" ]; then
  HOST="$SITE_B_HOST"
  SITE_NAME="$SITE_B_NAME"
  SPACE_NAME="$SITE_B_SPACE_NAME"
else
  echo "Usage: $0 siteA|siteB [count]"
  exit 1
fi

COUNT="${2:-100}"

PROJECT_DIR="`( cd \"$SCRIPTS_DIR/..\" && pwd )`"
FEEDER_JAR="$PROJECT_DIR/lib/wan-feeder.jar"

$JAVA_HOME/bin/java \
  -Dcom.gs.jini_lus.locators=$HOST:$SPACE_MANAGER_LOOKUP_PORT \
  -jar $FEEDER_JAR $SPACE_NAME $SITE_NAME $COUNT
