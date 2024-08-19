#!/usr/bin/env bash

###############################################################################
#  Licensed to the Apache Software Foundation (ASF) under one
#  or more contributor license agreements.  See the NOTICE file
#  distributed with this work for additional information
#  regarding copyright ownership.  The ASF licenses this file
#  to you under the Apache License, Version 2.0 (the
#  "License"); you may not use this file except in compliance
#  with the License.  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
# limitations under the License.
###############################################################################

COMMAND_STANDALONE="standalone-job"
COMMAND_HISTORY_SERVER="history-server"

# If unspecified, the hostname of the container is taken as the JobManager address
JOB_MANAGER_RPC_ADDRESS=${JOB_MANAGER_RPC_ADDRESS:-$(hostname -f)}
CONF_FILE="${FLINK_HOME}/conf/flink-conf.yaml"

drop_privs_cmd() {
    if [ $(id -u) != 0 ]; then
        # Don't need to drop privs if EUID != 0
        return
    elif [ -x /sbin/su-exec ]; then
        # Alpine
        echo su-exec flink
    else
        # Others
        echo gosu flink
    fi
}

copy_plugins_if_required() {
  if [ -z "$ENABLE_BUILT_IN_PLUGINS" ]; then
    return 0
  fi

  echo "Enabling required built-in plugins"
  for target_plugin in $(echo "$ENABLE_BUILT_IN_PLUGINS" | tr ';' ' '); do
    echo "Linking ${target_plugin} to plugin directory"
    plugin_name=${target_plugin%.jar}

    mkdir -p "${FLINK_HOME}/plugins/${plugin_name}"
    if [ ! -e "${FLINK_HOME}/opt/${target_plugin}" ]; then
      echo "Plugin ${target_plugin} does not exist. Exiting."
      exit 1
    else
      ln -fs "${FLINK_HOME}/opt/${target_plugin}" "${FLINK_HOME}/plugins/${plugin_name}"
      echo "Successfully enabled ${target_plugin}"
    fi
  done
}

set_config_option() {
  local option=$1
  local value=$2

  # escape periods for usage in regular expressions
  local escaped_option=$(echo ${option} | sed -e "s/\./\\\./g")

  # either override an existing entry, or append a new one
  if grep -E "^${escaped_option}:.*" "${CONF_FILE}" > /dev/null; then
        sed -i -e "s/${escaped_option}:.*/$option: $value/g" "${CONF_FILE}"
  else
        echo "${option}: ${value}" >> "${CONF_FILE}"
  fi
}

prepare_configuration() {
    set_config_option jobmanager.rpc.address ${JOB_MANAGER_RPC_ADDRESS}
    set_config_option blob.server.port 6124
    set_config_option query.server.port 6125

    if [ -n "${TASK_MANAGER_NUMBER_OF_TASK_SLOTS}" ]; then
        set_config_option taskmanager.numberOfTaskSlots ${TASK_MANAGER_NUMBER_OF_TASK_SLOTS}
    fi

    if [ -n "${FLINK_PROPERTIES}" ]; then
        echo "${FLINK_PROPERTIES}" >> "${CONF_FILE}"
    fi
    envsubst < "${CONF_FILE}" > "${CONF_FILE}.tmp" && mv "${CONF_FILE}.tmp" "${CONF_FILE}"
}

maybe_enable_jemalloc() {
    if [ "${DISABLE_JEMALLOC:-false}" == "false" ]; then
        JEMALLOC_PATH="/usr/lib/$(uname -m)-linux-gnu/libjemalloc.so"
        JEMALLOC_FALLBACK="/usr/lib/x86_64-linux-gnu/libjemalloc.so"
        if [ -f "$JEMALLOC_PATH" ]; then
            export LD_PRELOAD=$LD_PRELOAD:$JEMALLOC_PATH
        elif [ -f "$JEMALLOC_FALLBACK" ]; then
            export LD_PRELOAD=$LD_PRELOAD:$JEMALLOC_FALLBACK
        else
            if [ "$JEMALLOC_PATH" = "$JEMALLOC_FALLBACK" ]; then
                MSG_PATH=$JEMALLOC_PATH
            else
                MSG_PATH="$JEMALLOC_PATH and $JEMALLOC_FALLBACK"
            fi
            echo "WARNING: attempted to load jemalloc from $MSG_PATH but the library couldn't be found. glibc will be used instead."
        fi
    fi
}

try_download_iplib() {
    local iplib_exists=$(find $FLINK_HOME/usrlib/iplib/ -name IP2LOCATION-LITE-DB3.BIN)
	local iplibv6_exists=$(find $FLINK_HOME/usrlib/iplib/ -name IP2LOCATION-LITE-DB3.IPV6.BIN)
	if [ -z "${iplib_exists}" ] || [ -z "${iplibv6_exists}" ]; then
		echo "Iplib not found, try downloading default iplib..."
	    # try download default iplib
		local wget_output=$(wget -q https://www.clklog.com/res/IP2LOCATION-LITE-DB3.tar.gz)
		if [ $? -ne 0 ]; then
			echo "Failed to download default iplib. Please check your network and retry."
			exit 1
		else
		   	tar -xzf $FLINK_HOME/IP2LOCATION-LITE-DB3.tar.gz -C $FLINK_HOME/usrlib/iplib/
			echo "Default iplib downloaded."
		fi
    fi
}

maybe_enable_jemalloc

copy_plugins_if_required

prepare_configuration

args=("$@")
if [ "$1" = "help" ]; then
    printf "Usage: $(basename "$0") (jobmanager|${COMMAND_STANDALONE}|taskmanager|${COMMAND_HISTORY_SERVER})\n"
    printf "    Or $(basename "$0") help\n\n"
    printf "By default, Flink image adopts jemalloc as default memory allocator. This behavior can be disabled by setting the 'DISABLE_JEMALLOC' environment variable to 'true'.\n"
    exit 0
elif [ "$1" = "jobmanager" ]; then
    args=("${args[@]:1}")

    echo "Starting Job Manager"
    try_download_iplib
    exec $(drop_privs_cmd) "$FLINK_HOME/bin/jobmanager.sh" start-foreground "${args[@]}"
elif [ "$1" = ${COMMAND_STANDALONE} ]; then
    args=("${args[@]:1}")

    echo "Starting Job Manager"
	try_download_iplib
	to_search="--fromSavepoint" #the value variable is assigned a regex that matches the exact value of the first argument
	if [[ $(echo ${args[@]} | fgrep -w -- $to_search) ]]; then
		echo "Savepoint is Set"
	else
		last_checkpoint=$(ls $FLINK_HOME/usrlib/checkpoints/$PROCESSING_JOB_ID/* -td | egrep 'chk-.+' 2>&1 | head -n1)
		if [ ! -z "${last_checkpoint}" ]; then
			echo "Savepoint Discovered"
			args=("${args[@]}" "--fromSavepoint" "${last_checkpoint}")
		fi
	fi
    exec $(drop_privs_cmd) "$FLINK_HOME/bin/standalone-job.sh" start-foreground "${args[@]}"
elif [ "$1" = ${COMMAND_HISTORY_SERVER} ]; then
    args=("${args[@]:1}")

    echo "Starting History Server"

    exec $(drop_privs_cmd) "$FLINK_HOME/bin/historyserver.sh" start-foreground "${args[@]}"
elif [ "$1" = "taskmanager" ]; then
    args=("${args[@]:1}")

    echo "Starting Task Manager"
    try_download_iplib
    exec $(drop_privs_cmd) "$FLINK_HOME/bin/taskmanager.sh" start-foreground "${args[@]}"
fi

args=("${args[@]}")

# Running command in pass-through mode
exec $(drop_privs_cmd) "${args[@]}"
