#!/usr/bin/env bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"/../
cd "${DIR}" || exit 1
. env.sh || exit 1

# remove plugin dependencies
for lib in `find "$DEV_CY3_BUNDLE_DIR" -name "*.jar"`; do
    rm "$lib"
done
