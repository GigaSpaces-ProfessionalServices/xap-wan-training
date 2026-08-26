#!/usr/bin/env bash

# Run on a node to stop every GigaSpaces process started on it (space agent and
# gateway agent alike). Run on both nodes to tear down the whole topology.

SCRIPTS_DIR="`dirname \"$0\"`"
SCRIPTS_DIR="`( cd \"$SCRIPTS_DIR\" && pwd )`"

source $SCRIPTS_DIR/settings.sh

$GS_HOME/bin/gs.sh host kill-agent --all
