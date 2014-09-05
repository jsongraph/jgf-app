#!/usr/bin/env bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# load custom environment if available
if [ -r "env.sh.custom" ]; then
    source env.sh.custom || exit 1
fi

# -- Directories --
export DEV_DIR="${DEV_DIR:=$DIR}"
export DEV_SOURCE_DIR="${DEV_SOURCE_DIR:=$DEV_DIR/src}"
export DEV_SCRIPTS_DIR="${DEV_SCRIPTS_DIR:=$DEV_DIR/scripts}"
export DEV_TOOLS_DIR="${DEV_TOOLS_DIR:=$DEV_DIR/tools}"
export DEV_CY3_DIR="${DEV_CY3_DIR:=$DEV_TOOLS_DIR/cytoscape}"
export DEV_MAVEN_DIR="${DEV_MAVEN_DIR:=$DEV_TOOLS_DIR/maven}"
export DEV_CY3_LIBS_DIR="${DEV_CY3_LIBS_DIR:=$DEV_CY3_DIR/framework/system}"
export DEV_CY3_WORK_DIR="${DEV_CY3_WORK_DIR:=$DEV_CY3_DIR/work}"
export DEV_CY3_BUNDLE_DIR="${DEV_CY3_BUNDLE_DIR:=$DEV_CY3_WORK_DIR/bundles}"
export DEV_CY3_DATA_DIR="${DEV_CY3_DATA_DIR:=$DEV_CY3_WORK_DIR/data}"

# -- Files --
export DEV_TOOLS_JAVA_REPL_FILE="${DEV_JAVA_REPL_FILE:=$DEV_TOOLS_DIR/javarepl.jar}"
export DEV_CY3_LOG_FILE="${DEV_CY3_LOG_FILE:=$DEV_CY3_WORK_DIR/cytoscape.log}"

# -- Vars --
export PATH="$DEV_MAVEN_DIR/bin:$PATH"
