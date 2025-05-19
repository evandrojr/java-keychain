#!/bin/bash
# Script to run Java app with auto reload using entr
# Usage: ./auto-reload.sh

find src/main/java -name '*.java' | entr -r ./gradlew run
