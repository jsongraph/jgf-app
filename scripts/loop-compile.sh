#!/usr/bin/env bash
shopt -s expand_aliases
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1

while inotifywait -qq -r --exclude ".*\.swp" -e modify "$DEV_SOURCE_DIR"; do
    mvn -o -q compile
    if [ "$?" != "0" ]; then 
        notify-send -t 3000 "Failed compilation.\n\n$RESULT"
        echo "$RESULT" 2>&1
    else
        notify-send -t 3000 "Success, build without error."
    fi
done
