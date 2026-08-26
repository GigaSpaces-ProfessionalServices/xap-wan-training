#!/usr/bin/env bash

# Run on a node, after stop.sh, before the next start-grid/deploy cycle.

SCRIPTS_DIR="`dirname \"$0\"`"
SCRIPTS_DIR="`( cd \"$SCRIPTS_DIR\" && pwd )`"

source $SCRIPTS_DIR/settings.sh

echo "GS_HOME is: $GS_HOME"

if [ -n "$(ls -A $GS_HOME/work 2>/dev/null)" ]; then
  rm -r $GS_HOME/work/*
fi

if [ -n "$(ls -A $GS_HOME/logs 2>/dev/null)" ]; then
  rm -r $GS_HOME/logs/*
fi
